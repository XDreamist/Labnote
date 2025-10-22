package com.ade.labnetai.exception;

public class DocumentProcessingException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private final String errorCode;
	
	public DocumentProcessingException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public DocumentProcessingException(String message, Throwable cause, String errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
}