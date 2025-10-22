package com.ade.labnetai.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PdfConverter {
    private ClassPathResource pdfFile;
    private PDDocument document;
    private PDFTextStripper pdfStripper;
    private String pdfText;
    
    public void loadPdf(String resourcePath) {
        pdfFile = new ClassPathResource(resourcePath);
        pdfStripper = new PDFTextStripper();

        try (InputStream pdfStream = pdfFile.getInputStream()) {
        	byte[] pdfBytes = inputStreamToByteArray(pdfStream);
			document = Loader.loadPDF(pdfBytes);
	        pdfText = pdfStripper.getText(document);
	        document.close();
		} catch (IOException e) {
			System.err.println("Error loading pdf: " +  e.getMessage());
			e.printStackTrace();
		} finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    System.err.println("Error closing document: " + e.getMessage());
                }
            }
        }
    }
    
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
    
    public String getText() {
        return pdfText.isEmpty() ? null : pdfText;
    }
}