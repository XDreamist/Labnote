package com.ade.labnetai.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ade.labnetai.exception.DocumentProcessingException;
import com.ade.labnetai.utils.DocumentSplitter;
import com.ade.labnetai.utils.DocumentUtils;

@Service
public class DocumentConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentConverter.class);
	private static String[] SUPPORTED_EXTENSIONS = { ".pdf", ".xls", ".xlsx" };
	private static long MAX_FILE_SIZE = 10 * 1024 * 1024;
	
    private static final int maxTokenSize = 1000;
    private static final int maxOverlapTokenSize = 100;
	
	public Object convertToText(MultipartFile file) {
		this.validateFile(file);
		String extension = DocumentUtils.getFileExtension(file.getOriginalFilename()).toLowerCase();
		logger.info("Converting file: {} with extension: {}", file.getOriginalFilename(), extension);
		
		switch (extension) {
			case ".pdf":
				return this.convertPdfToText(file);
			case ".xls":
			case ".xlsx":
				return this.convertExcelToText(file);
			default:
				throw new DocumentProcessingException("Unsupported file extension: " + extension, "UNSUPPORTED_FILE_TYPE");
		}
	}
    
    public List<String> convertPdfToText(MultipartFile file) {
    	logger.debug("Processing PDF file: {}", file.getOriginalFilename());
    	try (InputStream inputStream = file.getInputStream()) {
    		byte[] pdfBytes = this.inputStreamToByteArray(inputStream);
    		try (PDDocument document = Loader.loadPDF(pdfBytes)) {
    			PDFTextStripper pdfStripper = new PDFTextStripper();
    			String pdfText = pdfStripper.getText(document);
    			
    		    // Use any advanced splitting algorithm to split the text segments based on the 
    		    // context and not directly on paragraphs or some spacing.
    			DocumentSplitter documentSplitter = new DocumentSplitter(maxTokenSize, maxOverlapTokenSize);
    			return documentSplitter.split(pdfText);
    		}
    	} catch (IOException e) {
    		logger.error("Failed to process PDF file: {}", file.getOriginalFilename(), e);
    		throw new DocumentProcessingException(
    			"Error processing PDF: " + e.getMessage(), "PDF_PROCESSING_ERROR");
    	}
    }
    
    public List<String> convertExcelToText(MultipartFile file) {
    	logger.debug("Processing Excel file: {}", file.getOriginalFilename());
    	List<String> textResponse = new ArrayList<>();
    	try (InputStream inputStream = file.getInputStream();
    		 Workbook workbook = WorkbookFactory.create(inputStream)) {
    		for (Sheet sheet : workbook) {
    			String sheetName = sheet.getSheetName();
    			Row headerRow = sheet.getRow(0);
    			List<String> headers = new ArrayList<>();
    			
    			if (headerRow != null) {
    				for (Cell cell : headerRow) {
    					headers.add(this.getCellValueAsString(cell));
    				}
    			}
    			
    			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
    				Row row = sheet.getRow(rowIndex);
    				if (row == null) continue;
    				
    				StringBuilder text = new StringBuilder();
    				boolean isInvalidRow = true;
    				for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
    					Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    					String value = getCellValueAsString(cell);
    					if (!value.isEmpty()) isInvalidRow = false;
    					text.append(headers.get(colIndex)).append(": ").append(value).append(", ");
    				}
    				if (!isInvalidRow) {
    					textResponse.add(text.toString().trim());
    				}
    			}
    		}
    		return textResponse;
    	} catch (IOException e) {
    		logger.error("Failed to process Excel file: {}", file.getOriginalFilename(), e);
    		throw new DocumentProcessingException(
    			"Error processing Excel: " + e.getMessage(), "EXCEL_PROCESSING_ERROR");
    	}
    }
    
    private void validateFile(MultipartFile file) {
    	if (file == null || file.isEmpty()) {
    		throw new DocumentProcessingException("File cannot be null or empty", "INVALID_FILE");
    	}
    	if (file.getSize() > MAX_FILE_SIZE) {
    		throw new DocumentProcessingException(
    			"File size exceeds maximum limit of 10MB",
    			"FILE_TOO_LARGE");
    	}
    	String extension = DocumentUtils.getFileExtension(file.getOriginalFilename());
    	boolean isSupported = false;
    	for (String supportedExt : SUPPORTED_EXTENSIONS) {
    		if (extension.equalsIgnoreCase(supportedExt)) {
    			isSupported = true;
    			break;
    		}
    	}
    	if (!isSupported) {
    		throw new DocumentProcessingException(
    			"Unsupported file extension: " + extension,
    			"UNSUPPORTYED_FILE_TYPE");
    	}
    }
    
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}