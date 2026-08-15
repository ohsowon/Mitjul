package com.mitjul.infra.ocr;

import com.mitjul.global.exception.BusinessException;
import com.mitjul.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MockOcrClient implements OcrClient{

    @Override
    public String extractText(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_IMAGE);
        }
        return "여기에 OCR로 인식된 문장이 들어갑니다. 실제 인식 대신 예시 텍스트를 반환하는 mock 구현입니다.";
    }
}
