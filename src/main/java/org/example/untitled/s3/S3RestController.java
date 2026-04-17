package org.example.untitled.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class S3RestController {

    private final Logger log = LoggerFactory.getLogger(S3RestController.class);
    private final S3Service s3Service;

    public S3RestController(S3Service s3Service){
        this.s3Service = s3Service;
    }

    @GetMapping("/upload/api/files")
    @ResponseBody
    public List<String> listFiles(){
        return s3Service.listFiles();
    }

    @GetMapping("/upload/api/files/upload-url")
    @ResponseBody
    public Map<String, String> getUploadUrl(@RequestParam String fileName, @RequestParam String contentType){
        log.info("uploading file " + fileName);
        String url = String.valueOf(s3Service.generateS3PreUploadUrl(fileName, contentType));
        return Map.of("url",url);
    }

    @GetMapping("/upload/api/files/download-url")
    @ResponseBody
    public Map<String, String> downloadFile(@RequestParam String fileName){
        log.info("Trying to download file " + fileName);
        String url = s3Service.generateS3DownloadUrl(fileName);
        return Map.of("url", url);
    }

    @GetMapping("/upload/api/files/delete-url")
    @ResponseBody
    public void deleteFile(@RequestParam String fileName){
        log.info("Deleting file " + fileName);
        s3Service.deleteFile(fileName);
    }

    @PostMapping("/upload/api/files/callback")
    @ResponseBody
    public Map<String, String> uploadCallback(@RequestParam String fileName) {
        log.info("Callback received: File {} has been uploaded successfully", fileName);
        return Map.of("status", "success", "message", "Callback received for " + fileName);
    }
}
