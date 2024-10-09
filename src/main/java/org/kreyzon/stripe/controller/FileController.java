package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.FileDB;
import org.kreyzon.stripe.message.ResponseFile;
import org.kreyzon.stripe.message.ResponseMessage;
import org.kreyzon.stripe.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "https://impulz-lms.com")
public class FileController {
    @Autowired
    private FileStorageService storageService;

    public FileController() {
    }

    @PostMapping({"/upload"})
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        try {
            FileDB uploadedFile = this.storageService.store(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            message = message + " (ID: " + uploadedFile.getId() + ")";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception var4) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping({"/files"})
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        List<ResponseFile> files = (List)this.storageService.getAllFiles().map((dbFile) -> {
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/").path(dbFile.getId()).toUriString();
            return new ResponseFile(dbFile.getName(), fileDownloadUri, dbFile.getType(), (long)dbFile.getData().length);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping({"/files/{id}"})
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        FileDB fileDB = this.storageService.getFile(id);
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().header("Content-Disposition", new String[]{"attachment; filename=\"" + fileDB.getName() + "\""})).body(fileDB.getData());
    }
}
