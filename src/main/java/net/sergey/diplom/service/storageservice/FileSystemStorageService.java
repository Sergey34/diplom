package net.sergey.diplom.service.storageservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileSystemStorageService {
    private final Path rootLocation;
    @Value("upload-dir")
    private String location = "upload-dir";

    public FileSystemStorageService() {
        this.rootLocation = Paths.get(location);
    }

    public Path getRootLocation() {
        return rootLocation;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectory(rootLocation);
            Files.createDirectory(Paths.get(location + "/airfoil_img"));
            Files.createDirectory(Paths.get(location + "/chartTemp"));
            Files.createDirectory(Paths.get(location + "/tmpCsv"));
            Files.createDirectory(Paths.get(location + "/scadFiles"));
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize storage", e);
        }
    }

    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalArgumentException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not read file: " + filename, e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public String listUploadedFiles(String filename) throws IOException {

        Path path = load(filename);
        String serveFile = MvcUriComponentsBuilder
                .fromMethodName(FileSystemStorageService.class, "serveFile", path.getFileName().toString())
                .build().toString();

        return serveFile;
    }

}
