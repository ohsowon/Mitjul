package com.mitjul.domain.community.service;

import com.mitjul.domain.community.dto.CommunityQuoteResponse;
import com.mitjul.domain.quote.entity.Quote;
import com.mitjul.domain.quote.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final QuoteRepository quoteRepository;

    @Transactional(readOnly = true)
    public Page<CommunityQuoteResponse> getPublicQuotes(String bookTitle, Pageable pageable) {
        Page<Quote> quotes = (bookTitle == null || bookTitle.isBlank())
                ? quoteRepository.findByIsPublicTrue(pageable)
                : quoteRepository.findByIsPublicTrueAndBook_TitleContaining(bookTitle, pageable);
        return quotes.map(CommunityQuoteResponse::from);
    }
}
