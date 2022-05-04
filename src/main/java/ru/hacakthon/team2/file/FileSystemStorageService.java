package ru.hacakthon.team2.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;

@Component
public class FileSystemStorageService implements IStorageService {

    private final Path storageLocation;

    public FileSystemStorageService(@Autowired Environment environment) throws URISyntaxException, IOException {
        storageLocation = Path.of(new URI(environment.getProperty("storage.location")));
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(storageLocation);
    }

    @Override
    public Path store(MultipartFile file) throws Exception {
        Files.copy(file.getInputStream(), storageLocation.resolve(file.getName()));
        return storageLocation.resolve(file.getName());
    }

    @Override
    public Path store(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.transferTo(new FileOutputStream(storageLocation.toFile()));
        return storageLocation.resolve(file.getName());
    }

    @Override
    public Path store(InputStream inputStream, String fileName) throws Exception {
        Files.copy(inputStream, storageLocation.resolve(fileName));
        return storageLocation.resolve(fileName);
    }

    @Override
    public Path load(String filename) {
        return storageLocation.resolve(filename);
    }

    @Override
    public List<Path> loadAll() {
        try {
            return Files.walk(storageLocation).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
