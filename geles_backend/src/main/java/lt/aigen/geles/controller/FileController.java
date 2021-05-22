package lt.aigen.geles.controller;

import lt.aigen.geles.uploadingfiles.StorageFileNotFoundException;
import lt.aigen.geles.uploadingfiles.StorageService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/files")
public class FileController {
    private final StorageService storageService;

    public FileController(ApplicationContext applicationContext, Environment environment, StorageService storageService) {
        String serviceName = environment.getProperty("storage.service.name");

        if (Arrays.asList(applicationContext.getBeanDefinitionNames()).contains(serviceName)){
            this.storageService = applicationContext.getBean(serviceName, StorageService.class);
        } else {
            this.storageService = storageService;
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.saveMultipartFile(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{filename}")
    public ResponseEntity<String> getFile(@PathVariable String filename) {
        try {
            return new ResponseEntity<>(storageService.getFileAsBase64(filename), HttpStatus.OK);
        } catch (IOException | StorageFileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
