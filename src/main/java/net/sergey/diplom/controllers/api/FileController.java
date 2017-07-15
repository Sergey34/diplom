package net.sergey.diplom.controllers.api;

import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
public class FileController {
    private final FileSystemStorageService storageService;

    @Autowired
    public FileController(FileSystemStorageService storageService) {this.storageService = storageService;}

    //    http://localhost:8081/files/airfoil_img/a18-il.dat
    @RequestMapping("/files/{dir}/**/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        String restOfTheUrl = ((String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).split("/files/")[1];
        Resource file = storageService.loadAsResource(restOfTheUrl);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @RequestMapping("/reset")
    public void reset() {
        storageService.init();
    }
}
