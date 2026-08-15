package com.mitjul.infra.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String store(MultipartFile file);
}
