package africode.banking.reportsAndMail.controller;

import africode.banking.reportsAndMail.service.EmailService;
import africode.banking.reportsAndMail.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 @Author Ella Ty Karambizi
 // * */

@RestController
public class FileUploadController {

    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    private EmailService emailService;
    @PostMapping
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
         fileUploadService.fileUpload(file);


    }
//    @GetMapping
//    public void generatePDF() throws IOException, URISyntaxException {
//        fileUploadService.pdfGenerate();
//    }
    @GetMapping
    public void readFolder( @RequestParam("file")String folderAdress) throws IOException, URISyntaxException {
        fileUploadService.processlistFilesForFolder(folderAdress,emailService);
    }
}
