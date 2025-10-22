package com.ade.labnetai.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

// import dev.langchain4j.data.document.DocumentSplitter;
// import dev.langchain4j.data.document.splitter.DocumentSplitters;
// import dev.langchain4j.data.segment.TextSegment;

@Service
public class TexttoVector {
    // private final OllamaEmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    // private final int maxTokenSize = 1000;
    // private final int maxOverlapTokenSize = 100;

    public TexttoVector(OllamaEmbeddingModel embeddingModel, VectorStore vectorStore) {
        // this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    public int getChunkCount(String documentId) {
        SearchRequest searchRequest = new SearchRequest.Builder()
            .query("")
            .filterExpression("parentId == \"" + documentId + "\"")
            .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);

        return results.size();
    }

    public void embedDocument(String documentId, String documentTitle, String documentText) {
        // dev.langchain4j.data.document.Document docToSplit = dev.langchain4j.data.document.Document.document(documentText);

        // DocumentSplitter splitter = DocumentSplitters.recursive(maxTokenSize, maxOverlapTokenSize);
        // List<TextSegment> segments = splitter.split(docToSplit);

        List<String> chunks = new ArrayList<String>();

        // for (TextSegment segment : segments) {
        //     chunks.add(segment.text());
        // }           
        List<Document> documents = new ArrayList<Document>();
        documents.add(createDocument(documentId, documentTitle));

        int chunkIndex = 1;
        for (String chunk : chunks) {
            // System.out.println("Chunk_" + chunkIndex + ": " + chunk);
            Document document = new Document.Builder()
                .id(documentId + "_chunk_" + chunkIndex)
                .text(chunk)
                .metadata("parentId", documentId)
                .metadata("type", "chunk")
                .build();
            documents.add(document);
            chunkIndex++;
        }

        vectorStore.add(documents);
    }

    private Document createDocument(String documentId, String documentTitle) {
        return new Document.Builder()
            .id(documentId)
            .text(documentTitle)
            .metadata("type", "title")
            .build();
    }
}