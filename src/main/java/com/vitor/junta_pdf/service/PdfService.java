package com.vitor.junta_pdf.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    public byte[] mergePdfs(List<MultipartFile> files) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            PdfMerger merger = new PdfMerger(pdfDoc);

            for (MultipartFile file : files) {
                PdfDocument srcDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(file.getBytes())));
                merger.merge(srcDoc, 1, srcDoc.getNumberOfPages());
                srcDoc.close();
            }
            pdfDoc.close();
            return baos.toByteArray();
        }
    }
}
