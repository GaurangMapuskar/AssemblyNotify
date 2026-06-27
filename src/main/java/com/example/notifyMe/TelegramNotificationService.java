package com.example.notifyMe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationService {

    @Value("${spring.application.api-key}")
    private String token;

    @Value("${spring.application.chat-id}")
    private String chatId;

    private final WebClient webClient;

    public void sendMessage(String message) {

        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage",
                token);

        SendMessageRequest request = new SendMessageRequest(
                chatId,
                message);

        webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Telegram notification sent.");
    }

    public void sendPdf(byte[] pdfBytes, String fileName, String caption) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part(
                "document",
                new ByteArrayResource(pdfBytes) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                });

        builder.part("chat_id", chatId);
        builder.part("caption", caption);

        webClient.post()
                .uri("https://api.telegram.org/bot{token}/sendDocument",
                        token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}