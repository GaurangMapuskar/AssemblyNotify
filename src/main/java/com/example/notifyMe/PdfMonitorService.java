package com.example.notifyMe;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfMonitorService {

    private static final String BASE_URL =
            "https://mls.org.in/PDF2026/monsoon/OOD/";

    private final TelegramNotificationService notificationService;
    private final RestClient restClient;

    private final NotificationPersistenceService persistenceService;


    public void check() {

        Set<String> notifiedFiles = persistenceService.loadNotifiedFiles();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        checkPdf(today.getDayOfMonth() + "-COUNCIL.pdf",notifiedFiles);
        checkPdf(today.getDayOfMonth() + "-ASSEMBLY.pdf",notifiedFiles);

        checkPdf(tomorrow.getDayOfMonth() + "-COUNCIL.pdf",notifiedFiles);
        checkPdf(tomorrow.getDayOfMonth() + "-ASSEMBLY.pdf",notifiedFiles);
    }

    private void checkPdf(String fileName,Set<String> notifiedFiles) {

        if (notifiedFiles.contains(fileName)) {
            return;
        }

        try {

            ResponseEntity<byte[]> response = restClient.get()
                    .uri(BASE_URL + fileName)
                    .retrieve()
                    .toEntity(byte[].class);

            if (response.getStatusCode().is2xxSuccessful()) {

                notificationService.sendPdf(
                        response.getBody(),
                        fileName,
                        "✅ " + fileName + " is now available.");

                notifiedFiles.add(fileName);
                persistenceService.saveNotifiedFiles(notifiedFiles);
            }

        } catch (HttpClientErrorException.NotFound e) {

            // PDF not yet published

        } catch (Exception e) {
            log.info("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            log.info(e.getMessage());
            log.info("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }
}