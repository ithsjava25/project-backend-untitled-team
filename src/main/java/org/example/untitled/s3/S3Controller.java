package org.example.untitled.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class S3Controller {

    private static final Logger log = LoggerFactory.getLogger(S3Controller.class);

    private final S3Service s3Service;

    public S3Controller(S3Service s3s){this.s3Service = s3s; }

    @GetMapping("/tickets/upload")
    public String home(Model model){
        log.info("welcome page");
        model.addAttribute("files", s3Service.listFiles());
        return "upload";
    }


}
