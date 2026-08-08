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

/**
 * 문장 도메인 서비스. 모든 조회·수정·삭제는 "본인 문장"에 대해서만 허용한다(소유권 검증).
 */
@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    /** 문장 저장. 책은 ISBN 기준 있으면 재사용, 없으면 생성. */
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

    /** 내 문장 목록 (페이징). */
    @Transactional(readOnly = true)
    public Page<QuoteResponse> getMyQuotes(Long userId, Pageable pageable) {
        return quoteRepository.findByUserId(userId, pageable).map(QuoteResponse::from);
    }

    /** 내 문장 상세. */
    @Transactional(readOnly = true)
    public QuoteResponse getMyQuote(Long userId, Long quoteId) {
        return QuoteResponse.from(findOwnedQuote(userId, quoteId));
    }

    /** 문장 수정 (부분). 값이 있는 필드만 반영한다. */
    @Transactional
    public QuoteResponse update(Long userId, Long quoteId, QuoteUpdateRequest request) {
        Quote quote = findOwnedQuote(userId, quoteId);

        // 미지정(null) 필드는 기존 값을 유지한다.
        String content = request.content() != null ? request.content() : quote.getContent();
        Integer page = request.page() != null ? request.page() : quote.getPage();
        quote.edit(content, page);
        if (request.isPublic() != null) {
            quote.changePublic(request.isPublic());
        }
        // 더티 체킹: 트랜잭션이 끝날 때 변경분이 자동으로 UPDATE 된다(save 호출 불필요).
        return QuoteResponse.from(quote);
    }

    /** 문장 삭제. */
    @Transactional
    public void delete(Long userId, Long quoteId) {
        Quote quote = findOwnedQuote(userId, quoteId);
        quoteRepository.delete(quote);
    }

    /** 소유권 검증: 없으면 404, 남의 문장이면 403. */
    private Quote findOwnedQuote(Long userId, Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUOTE_NOT_FOUND));
        if (!quote.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return quote;
    }
}
