package com.example.notifyMe;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationPersistenceService {

    private static final Path FILE =
            Paths.get("notified-files.txt");

    public Set<String> loadNotifiedFiles() {

        try {

            if (!Files.exists(FILE)) {
                Files.createFile(FILE);
            }

            return new HashSet<>(Files.readAllLines(FILE));

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void saveNotifiedFiles(Set<String> files) {
        cleanup(files);
        try {

            Files.write(
                    FILE,
                    files,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
    private void cleanup(Set<String> files) {

        int today = LocalDate.now().getDayOfMonth();
        int tomorrow = today + 1;

        files.removeIf(file -> {

            try {

                int day = Integer.parseInt(file.substring(0, file.indexOf('-')));

                return day != today && day != tomorrow;

            } catch (Exception e) {

                return true;
            }
        });
    }
}