package com.lacouf.rsbjwt.utils;

import com.lacouf.rsbjwt.Exception.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class StockageFichierUtils {
    private static final String TYPE_PDF = "application/pdf";
    private static final long TAILLE_MAX = 5 * 1024 * 1024;
    private StockageFichierUtils() {
    }

    public static Path sauvegarderPdf(MultipartFile file, Path dossier)
            throws FichierCorrompuException,
            FichierTropVolumineuxException,
            FichierTypeInvalideException,
            IOException {
        validerPdf(file, TAILLE_MAX);

        Files.createDirectories(dossier);

        String nomFichier = UUID.randomUUID() + ".pdf";
        Path destination = dossier.resolve(nomFichier).normalize();

        Files.copy(file.getInputStream(), destination);

        return destination;
    }

    private static void validerPdf(MultipartFile file, long tailleMax)
            throws FichierCorrompuException,
            FichierTropVolumineuxException,
            FichierTypeInvalideException {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw new FichierCorrompuException();
        }

        if (file.getSize() > tailleMax) {
            throw new FichierTropVolumineuxException();
        }

        if (!TYPE_PDF.equals(file.getContentType())) {
            throw new FichierTypeInvalideException();
        }
    }

    public static void supprimerFichier(Path chemin)
            throws SuppressionEchoueeFichierException {
        try {
            Files.deleteIfExists(chemin);
        } catch (IOException e) {
            throw new SuppressionEchoueeFichierException();
        }
    }

    public static Resource chargerFichier(Path chemin)
            throws FichierIntrouvableException {
        try {
            Resource resource = new UrlResource(chemin.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FichierIntrouvableException();
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new FichierIntrouvableException();
        }
    }
}
