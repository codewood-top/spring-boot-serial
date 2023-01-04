package top.codewood.controller;

import bootstrap.SpringBootWebApplication;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/upload")
public class UploadController {

    final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @ResponseBody
    @RequestMapping("img")
    public Map<String, Object> img(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            String fileMd5Str = DigestUtils.md5DigestAsHex(file.getInputStream());
            String[] fileNameComponents = file.getOriginalFilename().split("\\.");
            String path = String.format("/img/%d%02d%s.%s", LocalDate.now().getYear(), LocalDate.now().getMonthValue(), fileMd5Str, fileNameComponents[fileNameComponents.length - 1]);

            File nFile = new File(SpringBootWebApplication.UPLOAD_PATH + path);
            if (!nFile.getParentFile().exists()) {
                nFile.getParentFile().mkdirs();
            }
            file.transferTo(nFile);
            result.put("url", SpringBootWebApplication.DOMAIN + "/upload" + path);
        } catch (Exception e) {
            logger.error("upload img err: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return result;
    }

}
