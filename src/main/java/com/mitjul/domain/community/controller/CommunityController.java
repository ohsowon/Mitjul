package com.mitjul.domain.community.controller;

import com.mitjul.domain.community.dto.CommunityQuoteResponse;
import com.mitjul.domain.community.service.CommunityService;
import com.mitjul.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/quotes")
    public ResponseEntity<ApiResponse<Page<CommunityQuoteResponse>>>
    getPublicQuotes(@RequestParam(required = false) String book, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(communityService.getPublicQuotes(book, pageable)));
    }
}
