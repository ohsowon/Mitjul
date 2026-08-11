package com.mitjul.domain.quote.service;

import com.mitjul.domain.book.entity.Book;
import com.mitjul.domain.book.service.BookService;
import com.mitjul.domain.quote.dto.QuoteCreateRequest;
import com.mitjul.domain.quote.dto.QuoteResponse;
import com.mitjul.domain.quote.dto.QuoteUpdateRequest;
import com.mitjul.domain.quote.entity.Quote;
import com.mitjul.domain.quote.repository.QuoteRepository;
import com.mitjul.domain.user.entity.User;
import com.mitjul.domain.user.repository.UserRepository;
import com.mitjul.global.exception.BusinessException;
import com.mitjul.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    @Transactional
    public QuoteResponse create(Long userId, QuoteCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Book book = bookService.findOrCreate(request.book());

        Quote quote = Quote.builder()
                .content(request.content())
                .page(request.page())
                .imageUrl(request.imageUrl())
                .isPublic(request.isPublic())
                .user(user)
                .book(book)
                .build();
        return QuoteResponse.from(quoteRepository.save(quote));
    }

    @Transactional(readOnly = true)
    public Page<QuoteResponse> getMyQuotes(Long userId, Pageable pageable) {
        return quoteRepository.findByUserId(userId, pageable).map(QuoteResponse::from);
    }

    @Transactional(readOnly = true)
    public QuoteResponse getMyQuote(Long userId, Long quoteId) {
        return QuoteResponse.from(findOwnedQuote(userId, quoteId));
    }

    @Transactional
    public QuoteResponse update(Long userId, Long quoteId, QuoteUpdateRequest request) {
        Quote quote = findOwnedQuote(userId, quoteId);

        String content = request.content() != null ? request.content() : quote.getContent();
        Integer page = request.page() != null ? request.page() : quote.getPage();
        quote.edit(content, page);
        if (request.isPublic() != null) {
            quote.changePublic(request.isPublic());
        }

        return QuoteResponse.from(quote);
    }

    @Transactional
    public void delete(Long userId, Long quoteId) {
        Quote quote = findOwnedQuote(userId, quoteId);
        quoteRepository.delete(quote);
    }

    private Quote findOwnedQuote(Long userId, Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUOTE_NOT_FOUND));
        if (!quote.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return quote;
    }
}
