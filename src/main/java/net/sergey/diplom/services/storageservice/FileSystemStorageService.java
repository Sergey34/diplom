package net.sergey.diplom.services.storageservice;

import net.sergey.diplom.services.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
public class FileSystemStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
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
            FileSystemUtils.deleteRecursively(rootLocation.toFile());
            Files.createDirectory(rootLocation);
            Files.createDirectory(Paths.get(location + "/airfoil_img"));
            Files.createDirectory(Paths.get(location + "/chartTemp"));
            Files.createDirectory(Paths.get(location + "/tmpCsv"));
            Files.createDirectory(Paths.get(location + "/scadFiles"));
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize storage", e);
        }
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
        } catch (Exception e) {
            LOGGER.warn("Could not read file: {}", filename, e);
            return null;
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public void removeFiles(String shortName, List<String> chartNames) {
        FileSystemUtils.deleteRecursively(new File(location + "/airfoil_img/" + shortName + ".dat"));
        FileSystemUtils.deleteRecursively(new File(location + "/airfoil_img/" + shortName + ".png"));
        FileSystemUtils.deleteRecursively(new File(location + "/chartTemp/"));
        FileSystemUtils.deleteRecursively(new File(location + "/tmpCsv/" + shortName + "_100.scad"));
        try {
            Files.createDirectory(Paths.get(location + "/chartTemp"));
        } catch (IOException e) {
            LOGGER.warn("не удалось создать папку chartTemp", e);
        }
        for (String chartName : chartNames) {
            FileSystemUtils.deleteRecursively(new File(location + "/scadFiles/" + shortName + "-" + chartName + ".csv"));
        }
    }
}
