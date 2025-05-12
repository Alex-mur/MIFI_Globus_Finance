package it.globus.finance.service;

import it.globus.finance.model.entity.User;
import it.globus.finance.model.repo.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ReportStorageService {
    
    @Value("${pdf.storage.dir:./pdf-storage}")
    private String storageDirectory;

    private final UserService userService;
    private final UserRepo userRepo;

    public ReportStorageService(UserService userService, UserRepo userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }
    
    public String saveReport(byte[] pdfContent) throws IOException {
        Path dirPath = Paths.get(storageDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        String filename = UUID.randomUUID() + ".pdf";
        Path filePath = dirPath.resolve(filename);

        Files.write(filePath, pdfContent);
        User user = userService.getCurrentUser();
        user.addReportFileName(filename);
        userRepo.save(user);
        return filename;
    }
    
    public byte[] loadReport(String filename) throws IOException, IllegalAccessError {
        if (userService.getCurrentUser().getReportFileNames().contains(filename)) {
            Path filePath = Paths.get(storageDirectory, filename);
            if (!Files.exists(filePath)) {
                throw new IOException("Файл не найден");
            }
            return Files.readAllBytes(filePath);
        } else {
            throw new IllegalAccessError("У вас нет доступа к данному файлу");
        }
    }
}