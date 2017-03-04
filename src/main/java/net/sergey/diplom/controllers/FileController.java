package net.sergey.diplom.controllers;

import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
    @Autowired
    private FileSystemStorageService storageService;

    //    http://localhost:8081/files/airfoil_img/a18-il.dat
    @RequestMapping("/files/{dir}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, @PathVariable String dir) {
        Resource file = storageService.loadAsResource(dir + "/" + filename);
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
