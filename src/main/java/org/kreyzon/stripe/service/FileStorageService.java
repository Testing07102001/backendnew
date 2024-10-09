package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.FileDB;
import org.kreyzon.stripe.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

@Service
public class FileStorageService {
    @Autowired
    private FileDBRepository fileDBRepository;

    public FileStorageService() {
    }

    public FileDB store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes());
        return (FileDB)this.fileDBRepository.save(FileDB);
    }

    public FileDB getFile(String id) {
        return (FileDB)this.fileDBRepository.findById(id).get();
    }

    public Stream<FileDB> getAllFiles() {
        return this.fileDBRepository.findAll().stream();
    }

}
