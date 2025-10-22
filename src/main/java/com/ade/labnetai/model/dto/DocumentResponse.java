package com.ade.labnetai.model.dto;

public class DocumentResponse {
	private String message;
	private String documentId;
	private String status;
	
	public DocumentResponse(String message, String documentId) {
		this.message = message;
		this.documentId = documentId;
		this.status = "success";
	}
	
	public DocumentResponse(String message, String documentId, String status) {
		this.message = message;
		this.documentId = documentId;
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}