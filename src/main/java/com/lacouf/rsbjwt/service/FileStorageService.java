package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.FichierCorrompuException;
import com.lacouf.rsbjwt.Exception.FichierTropVolumineuxException;
import com.lacouf.rsbjwt.Exception.FichierTypeInvalideException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads/offres");

    public String storeOfferFile(MultipartFile file) throws IOException {

        validate(file);

        Files.createDirectories(root);

        String extension = ".pdf";
        String fileName = UUID.randomUUID() + extension;

        Path destination = root.resolve(fileName);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FichierCorrompuException();
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new FichierTypeInvalideException();
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FichierTropVolumineuxException();
        }
    }
}

