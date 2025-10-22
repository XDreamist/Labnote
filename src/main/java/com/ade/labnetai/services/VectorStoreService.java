package com.ade.labnetai.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.ade.labnetai.utils.DocumentComparator;

@Service
public class VectorStoreService {
	private final VectorStore vectorStore;
	
	public VectorStoreService(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}
	
	// For Api
	public List<Document> getAllDocumentsInfo() {
		SearchRequest searchRequest = new SearchRequest.Builder()
			.query("")
			.topK(Integer.MAX_VALUE)
			.build();
		List<Document> results = this.vectorStore.similaritySearch(searchRequest);
		Collections.sort(results, new DocumentComparator());
		return results;
	}
	
	public List<Document> getDocumentTitlesInfo() {
		SearchRequest searchRequest = new SearchRequest.Builder()
			.query("")
			.filterExpression("type == \"title\"")
			.build();
		List<Document> results = this.vectorStore.similaritySearch(searchRequest);
		return results;
	}
	
	public String deleteDocument(String documentId) {
		// If we have a better way to delete the document by using the below comments, then do it like that.
//		StringBuilder filterBuilder = new StringBuilder();
//		filterBuilder.append("parent == \"").append(documentId).append("\"");
		
		List<Document> titlesInfo = this.getDocumentTitlesInfo();
		int segmentCount = 0;
		String documentName = "No document was found which has the id: " + documentId + "\n";
		for (Document document : titlesInfo) {
			if (document.getId().compareTo(documentId) == 0) {
				Map<String, Object> metadata = document.getMetadata();
				segmentCount = (int) metadata.get("count");
				documentName = "Document \"" + document.getText() + "\" deleted successfully!\n";
			}
		}

		List<String> idsToDelete = new ArrayList<String>();
		idsToDelete.add(documentId);
		for (int i = 1; i <= segmentCount; i++) {
			idsToDelete.add(documentId + "_seg_" + i);
		}
		
		this.vectorStore.delete(idsToDelete);//(filterBuilder.toString());
		return documentName;
	}
	
	
	// For Search
	public List<String> retrieveRelevantContexts(String userQuery, List<String> documentsToSearch, int topK) {
		StringBuilder expressionBuilder = new StringBuilder();
		for (String documentId : documentsToSearch) {
			if (!expressionBuilder.isEmpty()) expressionBuilder.append(" OR ");
			expressionBuilder.append("parent == \"" + documentId + "\"");
		}
		
		// We can also add similarity threshold to filter out extra non relevant information from being added.
		SearchRequest searchRequest = new SearchRequest.Builder()
			.query(userQuery)
			.filterExpression(expressionBuilder.toString())
			.topK(topK)
//			.similarityThreshold(threshold)
			.build();
		
		List<Document> searchResults = this.vectorStore.similaritySearch(searchRequest);
		
		List<String> results = new ArrayList<String>();
		if (searchResults != null) {
			for (Document document : searchResults) {
				results.add(document.getText());
			}
		}
		
		return results;
	}
}