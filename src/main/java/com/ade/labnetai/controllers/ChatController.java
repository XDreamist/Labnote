package com.ade.labnetai.controllers;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ade.labnetai.services.ContextSearch;
import com.ade.labnetai.model.dto.PromptRequest;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/")
//@CrossOrigin(origins = "*", methods = RequestMethod.POST) // Use this if we need individual cors origin configuration for controllers
public class ChatController {

    private final ContextSearch contextSearch;
    
    public ChatController(ContextSearch contextSearch) {
        this.contextSearch = contextSearch;
    }
    
    @PostMapping("/search")
    public Flux<String> chat(@RequestBody PromptRequest request) {
//    	return this.contextSearch.generateResponseFromDocuments(request.getPrompt(), request.getDocumentIds());
        return this.contextSearch.chatWithContextFromDocuments(request.getPrompt(), request.getDocumentIds());
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        this.contextSearch.stopTextGeneration();
        return ResponseEntity.ok("Streaming stopped");
    }
}