package d3fix4m.ru.testdownloadservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DownloadController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Error 1");
        }


        File serverFile = new File("src/main/resources/file/tempFile");
        try (FileOutputStream outputStream = new FileOutputStream(serverFile)) {
            byte[] bytes = file.getBytes();
            outputStream.write(bytes);
        }

        String fileUrl = "storage/download/tempFile";
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(fileUrl);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {

        File file = new File("src/main/resources/file/" + fileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // Создаем заголовки для ответа
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);


        // Возвращаем файл в ответе на запрос
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

}