package com.mitjul.domain.quote.repository;

import com.mitjul.domain.quote.entity.Quote;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByUserId(Long userId);

    Page<Quote> findByUserId(Long userId, Pageable pageable);

    List<Quote> findByIsPublicTrue();
}
