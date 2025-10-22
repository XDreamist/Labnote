package com.ade.labnetai.model.dto;

import javax.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class DocumentRequest {
	
	private String documentTitle;
	
	@NotNull(message = "Document file is required")
	private MultipartFile documentFile;
	
	public String getDocumentTitle() {
		return documentTitle;
	}
	
	public void setDocumentTitle(String documentTitle) { 
		this.documentTitle = documentTitle;
	}
	
	public MultipartFile getDocumentFile() {
		return documentFile;
	}
	
	public void setDocumentFile(MultipartFile documentFile) {
		this.documentFile = documentFile;
		
		if ((this.documentTitle == null || this.documentTitle.isEmpty() && documentFile != null)) {
			this.documentTitle = documentFile.getOriginalFilename();
		}
	}
}