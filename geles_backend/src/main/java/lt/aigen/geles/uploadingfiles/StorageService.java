package lt.aigen.geles.uploadingfiles;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    void init();

    void store(MultipartFile file);

    String getFileAsBase64(String filename) throws IOException;

    void deleteFile(String filename) throws IOException;
}
