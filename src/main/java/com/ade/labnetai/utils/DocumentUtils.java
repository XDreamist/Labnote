package com.ade.labnetai.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;

public class DocumentUtils {
	
	public static class DocumentContainer {
		private final String id; 
		private final String title;
		private final String fileName;
		private Document titleDocument;
		private List<Document> subDocuments;
		
		public DocumentContainer(String title, String documentName) {
			this.id = UUID.randomUUID().toString();
			this.title = title;
			this.fileName = documentName;
		}
		
		public List<Document> createDocuments(List<String> documentTexts) {
			this.subDocuments = createDocumentSegments(this.id, documentTexts);
			this.titleDocument = createTitleDocument(this.id, this.title, this.fileName, this.subDocuments.size());
			this.subDocuments.addFirst(titleDocument);
			
			return subDocuments;
		}

	    // Add any other info eg: Date, Author, Section, etc..
	    private Document createTitleDocument(String documentId, String documentTitle, String documentName, int segmentCount) {
	    	return new Document.Builder()
	    		.id(documentId)
	    		.text(documentTitle)
	    		.metadata("type", "title")
	    		.metadata("file", documentName)
	    		.metadata("format", getFileExtension(documentName))
	    		.metadata("count", segmentCount)
	    		.build();
	    }

	    // Add any other info eg: Rule no, segment no, chapter, etc..
	    private List<Document> createDocumentSegments(String documentId, List<String> documentTexts) {
	    	List<Document> documentSegments = new ArrayList<Document>();
	    	int segmentIndex = 1;
	    	for (String text : documentTexts) {
	    		Document documentSegment = new Document.Builder()
	    			.id(documentId + "_seg_" + segmentIndex)
	    			.text(text)
	    			.metadata("parent", documentId)
	    			.metadata("type", "segment")
	    			.build();
	    		documentSegments.add(documentSegment);
	    		++segmentIndex;
	    	}
	    	
	    	return documentSegments;
	    }
	    
	    public String getId() { return this.id; } 
		
		public List<Document> getDocuments() { return this.subDocuments; }
	}
	
    public static String getFileExtension(String filename) {
    	if (filename == null || filename.lastIndexOf('.') == -1) {
    		return "";
    	}
    	return filename.substring(filename.lastIndexOf('.'));
    }
}