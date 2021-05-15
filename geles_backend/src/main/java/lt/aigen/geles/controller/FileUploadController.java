package lt.aigen.geles.controller;

import lt.aigen.geles.uploadingfiles.StorageException;
import lt.aigen.geles.uploadingfiles.StorageFileNotFoundException;
import lt.aigen.geles.uploadingfiles.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileUploadController {
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
