package com.mitjul.domain.ocr.controller;

import com.mitjul.domain.ocr.dto.OcrResponse;
import com.mitjul.domain.ocr.service.OcrService;
import com.mitjul.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OcrResponse>> extract(@RequestParam("image") MultipartFile image) {  // json이 아니라 파일을 전달받음
        OcrResponse response = ocrService.extract(image);
        return ResponseEntity.ok(ApiResponse.success(response, "텍스트를 추출했습니다."));
    }
}
