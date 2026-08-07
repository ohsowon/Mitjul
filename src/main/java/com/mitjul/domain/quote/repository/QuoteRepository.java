package com.mitjul.domain.quote.repository;

import com.mitjul.domain.quote.entity.Quote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 문장 저장소.
 *
 * findByUserId는 연관 엔티티의 필드를 파고드는 쿼리 메서드다. Spring Data가
 * User user의 id를 user.id로 해석해 where user_id = ?를 만든다.
 * (목록 조회는 마일스톤 3에서 Pageable을 붙여 페이징으로 확장한다.)
 */
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    /** 특정 사용자의 문장 목록 (내 문장 목록, CLAUDE.md §5) */
    List<Quote> findByUserId(Long userId);

    /** 공개(isPublic=true) 문장만 (커뮤니티 피드, CLAUDE.md §5) */
    List<Quote> findByIsPublicTrue();
}
