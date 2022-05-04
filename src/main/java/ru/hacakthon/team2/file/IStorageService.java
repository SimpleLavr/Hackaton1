package ru.hacakthon.team2.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;

public interface IStorageService {

    void init() throws Exception;

    Path store(MultipartFile file) throws Exception;

    Path store(File file) throws Exception;

    Path load(String filename);

    List<Path> loadAll();
}
