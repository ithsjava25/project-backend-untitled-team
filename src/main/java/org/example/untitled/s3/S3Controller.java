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

    public S3Controller(){ }

    @GetMapping("/upload")
    public String home(Model model){
        log.info("upload");
        return "upload";
    }

}
