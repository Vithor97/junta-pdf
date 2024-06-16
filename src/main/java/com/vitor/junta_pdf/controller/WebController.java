package com.vitor.junta_pdf.controller;

import com.vitor.junta_pdf.service.PdfService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class WebController {

    private final PdfService pdfService;

    public WebController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/merge")
    public String mergePdfs(@RequestParam("files") List<MultipartFile> files, Model model, RedirectAttributes redirectAttributes) {
        try {
            byte[] mergedPdf = pdfService.mergePdfs(files);
            File tempFile = File.createTempFile("merged", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(mergedPdf);
            }
            String encodedPath = URLEncoder.encode(tempFile.getAbsolutePath(), StandardCharsets.UTF_8.toString());
            model.addAttribute("filePath", encodedPath);
            return "result";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Erro ao mesclar os PDFs. Por favor, tente novamente.");
            return "redirect:/";
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String path) {
        try {
            String decodedPath = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8.toString());
            File file = new File(decodedPath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "merged.pdf");
            headers.setContentLength(file.length());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}