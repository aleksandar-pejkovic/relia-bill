package dev.alpey.reliabill.controller.fileUpload;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.alpey.reliabill.service.fileUpload.FileUploadService;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/products")
    public void uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file selected.");
        }
        String filename = file.getOriginalFilename();
        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file data.", e);
        }
        fileUploadService.saveProductsFromFile(fileData, filename, principal);
    }
}
