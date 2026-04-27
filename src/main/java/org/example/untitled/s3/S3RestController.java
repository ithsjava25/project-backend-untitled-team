package org.example.untitled.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets/upload/api/files")
public class S3RestController {

    private final Logger log = LoggerFactory.getLogger(S3RestController.class);
    private final S3Service s3Service;

    public S3RestController(S3Service s3Service){
        this.s3Service = s3Service;
    }

    @GetMapping()
    @ResponseBody
    public List<String> listFiles(){
        return s3Service.listFiles();
    }

    @GetMapping("/upload-url")
    @ResponseBody
    public Map<String, String> getUploadUrl(@RequestParam(required = false) Long caseId, @RequestParam String fileName, @RequestParam String contentType){
        log.info("uploading file " + fileName);
        S3Service.S3UploadResponse resp = s3Service.generateS3PreUploadUrl(caseId, fileName, contentType);
        return Map.of("url", resp.url(), "fileName", resp.fileName());
    }

    @GetMapping("/download-url")
    @ResponseBody
    public Map<String, String> downloadFile(@RequestParam String fileName){
        log.info("Trying to download file " + fileName);
        String url = s3Service.generateS3DownloadUrl(fileName);
        return Map.of("url", url);
    }

    @DeleteMapping("/delete-url")
    public void deleteFile(@RequestParam String fileName){
        log.info("Deleting file " + fileName);
        s3Service.deleteFile(fileName);
    }

    @PostMapping("/callback")
    @ResponseBody
    public Map<String, String> uploadCallback(@RequestParam String fileName) {
        log.info("Callback received: File {} has been uploaded successfully", fileName);
        return Map.of("status", "success", "message", "Callback received for " + fileName);
    }
}
