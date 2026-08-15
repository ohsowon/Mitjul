package com.mitjul.infra.storage;

import com.mitjul.global.exception.BusinessException;
import com.mitjul.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalImageStorage implements ImageStorage{

    private final Path baseDir;

    public LocalImageStorage(@Value("{storage.local.path:upload}") String path) {
        this.baseDir = Paths.get(path).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_IMAGE);
        }
        try {
            Files.createDirectories(baseDir);  // 지정 경로(baseDir)에 필요한 상위 폴더가 없을 경우 디렉토리 생성
            String filename = UUID.randomUUID() + extractExtension(file.getOriginalFilename());  // 한글 충돌, 보안 등의 위험을 방지하기 위해 UUID로 파일명을 새로 생성
            file.transferTo(baseDir.resolve(filename));  // 파일을 디스크에 저장
            return "/images/" + filename;  // 이 반환값은 Quote에서 imageURL로 사용
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_STORE_FAILED);
        }
    }

    private String extractExtension(String originalName) {
        if (originalName == null) {
            return "";
        }
        int dot = originalName.lastIndexOf('.');
        return dot == -1 ? "" : originalName.substring(dot);
    }
}
