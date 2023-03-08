package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileOperations {

    private static FileOperations INSTANCE;

    public static FileOperations getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileOperations();
        }
        return INSTANCE;
    }

    public List<String> readCommandsFromFile(String fileName) {

        List<String> commands = Collections.emptyList();
        try {
            commands = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return commands;
    }

    public void writeToFile(String fileName, String content) {

        if (content == null)
            return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(content);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Function that parses the date read from file as String to Date .
    public LocalDateTime parseDateFromFile(String dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter);
    }

    public void cleanDirectory(String dirName) {
        for (File file : Objects.requireNonNull(new File(dirName).listFiles()))
            if (!file.isDirectory())
                file.delete();
    }
}