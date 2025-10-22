package com.ade.labnetai.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ade.labnetai.utils.DocumentConverter;
import com.ade.labnetai.exception.DocumentProcessingException;
import com.ade.labnetai.model.dto.DocumentRequest;
import com.ade.labnetai.model.dto.DocumentResponse;
import com.ade.labnetai.services.DocumentToVector;
import com.ade.labnetai.services.VectorStoreService;
import com.ade.labnetai.utils.DocumentUtils.DocumentContainer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/")
@Validated
@Tag(name = "Vector Store API", description = "APIs for managing document in the vector store")
public class VectorStoreController {
	
	private static final Logger logger = LoggerFactory.getLogger(VectorStoreController.class);
	
	private final VectorStoreService vectorStoreService;
	private final DocumentToVector documentToVector;
	private final DocumentConverter documentConverter;
	
	private List<Document> documentTitlesInfo;
	private List<Document> documentsInfo;
	
	public VectorStoreController(VectorStoreService vectorStoreService, DocumentToVector documentToVector, DocumentConverter documentConverter) {
		this.vectorStoreService = vectorStoreService;
		this.documentToVector = documentToVector;
		this.documentConverter = documentConverter;
		
		this.updateDocumentTitlesInfo();
		this.updateAllDocumentsInfo();
	}
    
    @GetMapping("/docs")
    @Operation(summary = "Get all document", description = "Retrieves all documents stored in the vector store")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved documents")
    public List<Document> getAllDocumentsInfo() {
    	return this.documentsInfo;
    }
	
    @GetMapping("/titles")
    public List<Document> getDocumentTitlesInfo() {
    	return this.documentTitlesInfo;
    }
    
    @PostMapping("/embed")
    @Async
    @Operation(summary = "Embed document", description = "Embeds a document into the vector store")
    @ApiResponse(responseCode = "202", description = "Document embedding accepted")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<DocumentResponse> embed(@Valid @ModelAttribute DocumentRequest documentRequest) {
    	DocumentContainer document = new DocumentContainer(documentRequest.getDocumentTitle(), documentRequest.getDocumentFile().getOriginalFilename());
    	String documentId = document.getId();
    	logger.info("Starting document embedding for ID: {}", documentId);
    	
    	try {
    		Object convertedText = this.documentConverter.convertToText(documentRequest.getDocumentFile());
    		if (convertedText instanceof List) {
    			@SuppressWarnings("unchecked")
    			List<String> documentTexts = (List<String>) convertedText;
    			this.documentToVector.embedDocument(document.createDocuments(documentTexts));
    		}
    		else {
    			throw new DocumentProcessingException("Invalid document conversion result", "INVALID_CONVERSION_RESULT");
    		}
    		logger.info("Document embedding completed for ID: {}", documentId);
    		return ResponseEntity.status(HttpStatus.ACCEPTED).body(new DocumentResponse("Document embedding accepted", documentId));
		} catch (Exception e) {
			logger.error("Failed to embed document for ID: {}", documentId, e);
			throw new DocumentProcessingException("Failed to process document file", "DOCUMENT_PROCESSING_ERROR");
		} finally {
	    	this.updateDocumentTitlesInfo();
			this.updateAllDocumentsInfo();
		}
    }
//    curl -F "documentTitle=filename" -F "documentFile=@/path/to/file.pdf" http://localhost:8080/labnetai/api/embed
    
    @PostMapping("/delete")
    public ResponseEntity<String> deleteDocument(
    	@RequestParam("documentId") String documentId
    ) {
    	String response = this.vectorStoreService.deleteDocument(documentId);
    	
    	this.updateDocumentTitlesInfo();
		this.updateAllDocumentsInfo();
    	return ResponseEntity.ok(response);
    }
//    curl -F "documentId=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX" http://localhost:8080/labnetai/api/delete
    
    
    private void updateDocumentTitlesInfo() {
    	this.documentTitlesInfo = this.vectorStoreService.getDocumentTitlesInfo();
    }
    
    private void updateAllDocumentsInfo() {
    	this.documentsInfo = this.vectorStoreService.getAllDocumentsInfo();
    }
}