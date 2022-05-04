package ru.hacakthon.team2.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class ZipArchiver {

    @Autowired
    private IStorageService storageService;

    public void unpack(Path zipArchive) throws Exception {
        ZipFile zipFile = new ZipFile(zipArchive.toFile());
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipArchive.toFile()));

        ZipEntry entry = zipInputStream.getNextEntry();

        while(entry != null) {
            String entryName = entry.getName();
            if(entryName.endsWith(".pdf") || entryName.endsWith(".csv")) {
                storageService.store(zipFile.getInputStream(entry), entryName);
            }
            entry = zipInputStream.getNextEntry();
        }
        zipArchive.toFile().delete();
    }
}
