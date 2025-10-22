package com.ade.labnetai.model.dto;

import java.util.List;

public class PromptRequest {
	private String prompt;
	private List<String> documentIds;
	
	public String getPrompt() {
		return prompt;
	}
	
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public List<String> getDocumentIds() {
		return documentIds;
	}
	
	public void setDocumentIds(List<String> documentIds) {
		this.documentIds = documentIds;
	}
}