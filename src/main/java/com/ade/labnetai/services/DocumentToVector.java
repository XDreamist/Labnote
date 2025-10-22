package com.ade.labnetai.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentToVector {
	
	private final VectorStore vectorStore;

    public DocumentToVector(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
    
    // Main embedding into the Vector Store happens here
    public void embedDocument(List<Document> documentSegments) {
        try {
            this.vectorStore.add(documentSegments);
        } catch (Exception e) {
            System.err.println("Failed to add document segments to the vector store: " + e.getMessage());
            e.printStackTrace();
        }
    }
}