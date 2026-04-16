package org.example.untitled.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class S3Controller {

    private static final Logger log = LoggerFactory.getLogger(S3Controller.class);

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service){
        this.s3Service = s3Service;
    }

    @GetMapping("/upload")
    public String home(Model model){
        log.info("welcome page");
        model.addAttribute("files", s3Service.listFiles());
        return "upload";
    }

    @GetMapping("/upload/api/files")
    @ResponseBody
    public List<String> listFiles(){
        return s3Service.listFiles();
    }

    @GetMapping("/upload/api/files/upload-url")
    @ResponseBody
    public Map<String, String> getUploadUrl(@RequestParam String fileName, @RequestParam String contentType){
        String url = s3Service.generateS3PreUploadUrl(fileName, contentType);
        return Map.of("url",url);
    }

    @GetMapping("/upload/api/files/download-url")
    @ResponseBody
    public Map<String, String> downloadFile(@RequestParam String fileName){
        String url = s3Service.generateS3DownloadUrl(fileName);
        return Map.of("url", url);
    }

    @PostMapping("/upload/api/files/callback")
    @ResponseBody
    public Map<String, String> uploadCallback(@RequestParam String fileName) {
        log.info("Callback received: File {} has been uploaded successfully", fileName);
        return Map.of("status", "success", "message", "Callback received for " + fileName);
    }
}
