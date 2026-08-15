package com.mitjul.domain.ocr.service;

import com.mitjul.domain.ocr.dto.OcrResponse;
import com.mitjul.infra.ocr.OcrClient;
import com.mitjul.infra.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OcrService {

    private final OcrClient ocrClient;
    private final ImageStorage imageStorage;

    public OcrResponse extract(MultipartFile image) {
        String text = ocrClient.extractText(image);
        String imageUrl = imageStorage.store(image);
        return new OcrResponse(text, imageUrl);
    }
}
