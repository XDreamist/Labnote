// package com.ade.labnetai.services;

// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.io.InputStream;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.springframework.web.multipart.MultipartFile;

// import org.springframework.core.io.ClassPathResource;
// import org.springframework.stereotype.Service;

// @Service
// public class ExcelConverter {
//     private ClassPathResource excelFile;

//     private String excelText;
    
//     public void readExcel(MultipartFile file) throws IOException {
//         // excelFile = new ClassPathResource(resourcePath);

//         // List<MyDataModel> dataList = new ArrayList<>();
//         try (InputStream inputStream = file.getInputStream()) {
//             Workbook workbook = new XSSFWorkbook(inputStream);
//             Sheet sheet = workbook.getSheetAt(0);

//             int rowNum = 0;
//             for (Row row : sheet) {
//                 if (rowNum == 0) {
//                     rowNum++;
//                     continue;
//                 }

//                 // MyDataModel
//             }
//         }
//     }
    
//     private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
//         try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
//             byte[] data = new byte[1024];
//             int nRead;
//             while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//                 buffer.write(data, 0, nRead);
//             }
//             return buffer.toByteArray();
//         }
//     }
    
//     public String getText() {
//         return pdfText.isEmpty() ? null : pdfText;
//     }
// }