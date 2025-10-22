package com.ade.labnetai.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.reactivestreams.Subscription;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Service
public class ContextSearch {
	private final VectorStoreService vectorStoreService;
	private final OllamaChatModel ollamaChatModel;
    private final boolean canLLMThink = true;
	
	// Max number of relevant contexts retrieved. Best results for pdf: 4 and excel: 10 depending on the size of pdf paragraphs and excel.
	private static final int TOPK = 10;
	
	private static final int HISTORYAWARENESS = 4;
	
	// We can refine the prompt if we want more flexible or more specific results. We can just mention in this below prompt.
	private static final String SYSPROMPT = 
		"You are a helpful and accurate assistant. "
		+ "Use ONLY the information provided in the document content below to answer the question.\n"
		+ "If the answer is not contained within the content, respond with "
		+ "\"Sorry. I don't have answers for this question\" or a similar phrase—do not guess or fabricate information.";
	
	private Disposable currentSubscription;
	private List<Message> chatMessages = new ArrayList<>();
	
	public ContextSearch(VectorStoreService vectorStoreService, OllamaChatModel ollamaChatModel) {
		this.vectorStoreService = vectorStoreService;
		this.ollamaChatModel = ollamaChatModel;
	}
	
	public Flux<String> generateResponseFromDocuments(String userQuery, List<String> documentsToSearch) {
		List<String> contexts = this.vectorStoreService.retrieveRelevantContexts(userQuery, documentsToSearch, TOPK);
		
		StringBuilder promptBuilder = new StringBuilder();		
		promptBuilder.append(SYSPROMPT).append("\n\n");
		
		for (String context : contexts) {
			promptBuilder.append(context).append("\n\n");
		}
		promptBuilder.append("Question: ").append(userQuery);
		
		Flux<ChatResponse> responseStream = this.ollamaChatModel.stream(new Prompt(promptBuilder.toString()));
		
		boolean shouldPrependThink = this.canLLMThink;
		
		Flux<String> finalStream = responseStream
			.map(chatResponse -> chatResponse.getResult().getOutput().getText());
		
		if (shouldPrependThink) {
			finalStream = Flux.concat(
				Flux.just("<think>"),
				finalStream
			);
		}
		
		return finalStream
			.doOnTerminate(() -> {
				System.out.println("Streaming Completed");
				this.clearCurrentSubscription();
			}
		);
	}
	
	public Flux<String> chatWithContextFromDocuments(String userQuery, List<String> documentsToSearch) {
		List<String> contexts = this.vectorStoreService.retrieveRelevantContexts(userQuery, documentsToSearch, TOPK);
		
		List<Message> messages = new ArrayList<>();
		
		if (this.chatMessages != null && !this.chatMessages.isEmpty()) {
			messages.addAll(this.chatMessages);
		}
		
        boolean shouldPrependThink = this.canLLMThink;

		if (!contexts.isEmpty()) {
			StringBuilder contextBuilder = new StringBuilder();
			for (String context : contexts) {
				contextBuilder.append(context).append("\n\n");
			}
			// This is a SystemMessage. But DeepSeekR1 doesn't support the usage of SystemMessage, so temporarily replaced with UserMessage.
			messages.add(shouldPrependThink ? 
				new UserMessage(SYSPROMPT + "\n\nContent:\n\n" + contextBuilder.toString()) : 
				new SystemMessage(SYSPROMPT + "\n\nContent:\n\n" + contextBuilder.toString())
			);
		}
		messages.add(new UserMessage(userQuery));

		System.out.println(messages);
		Message[] messagesArray = messages.toArray(new Message[0]);
		System.out.println(messagesArray);
		
		Flux<String> responseStream = this.ollamaChatModel.stream(messagesArray);
		
		Flux<String> finalStream = responseStream;
		if (shouldPrependThink) {
			finalStream = Flux.concat(
				Flux.just("<think>"),
				responseStream
			);
		}
		
	    return finalStream
	    	.doOnComplete(() -> {
//	    		messages.add(new AssistantMessage(responseBuilder.toString()));
	    		if (messages.size() > HISTORYAWARENESS) {
	                this.chatMessages = new ArrayList<>(messages.subList(messages.size() - HISTORYAWARENESS, messages.size()));
	            } 
	    		else {
	                this.chatMessages = new ArrayList<>(messages);
	            }
	    		
	    		System.out.println("Streaming Completed");
	    		this.clearCurrentSubscription();
	    	})
	    	.doFinally(signal -> {
	    		System.out.println("Streaming terminated with signal: " + signal);
	    	}
	    );
	}
	

	// ===== Subscriptions to manage user request =====
	// Only one user request will be accepted per session.
	// This can be used to stop generation, and manage number of users and stuff.
	// Need to check the implementation again. Not working in some areas.
	// Stop is not working as expected. And some other functionalities too.
	// Need to test it thoroughly.
	public void setCurrentSubscription(Subscription subscription) {
		if (subscription instanceof Disposable) {
			this.currentSubscription = (Disposable) subscription;
		}
	}
	
	public void clearCurrentSubscription() {
		this.currentSubscription = null;
	}
	
	private Disposable startStreaming(String userQuery, List<String> documentsToSearch, Consumer<String> onNext) {
		Flux<String> responseFlux = this.generateResponseFromDocuments(userQuery, documentsToSearch);
		currentSubscription = responseFlux.subscribe(onNext);
		return currentSubscription;
	}
	
	public void stopTextGeneration() {
		if (currentSubscription != null && !currentSubscription.isDisposed()) {
			currentSubscription.dispose();
			System.out.println("Streaming stopped");
			this.clearCurrentSubscription();
		}
	}
}