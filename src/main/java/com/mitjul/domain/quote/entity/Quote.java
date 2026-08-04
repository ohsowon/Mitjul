package com.mitjul.domain.quote.entity;

import com.mitjul.domain.book.entity.Book;
import com.mitjul.domain.user.entity.User;
import com.mitjul.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문장(밑줄 그은 문장) 엔티티 — 서비스의 핵심 데이터.
 *
 * <p>한 문장은 한 명의 User가 한 권의 Book에서 수집한다. 그래서 User·Book과 각각
 * {@code @ManyToOne} 관계를 맺는다. FK(user_id, book_id)를 가진 "다(N)" 쪽이 Quote다.
 *
 * <p><b>지연 로딩(LAZY)을 명시한 이유</b>: {@code @ManyToOne}의 기본값은 EAGER라, Quote를 조회할
 * 때마다 User·Book을 항상 조인해 가져온다(불필요한 쿼리와 N+1의 원인). LAZY로 두면 그 연관을 실제로
 * 쓸 때만 조회한다. 대신 연관 객체는 트랜잭션(=Service) 안에서 접근해야 한다 — 이것이 application.yml의
 * {@code open-in-view: false}와 직결된다. 컨트롤러까지 엔티티를 들고 나가 LAZY 필드를 건드리면 세션이
 * 닫혀 {@code LazyInitializationException}이 난다. 그래서 Service 안에서 DTO로 변환해 반환한다.
 *
 * <p>연관을 단방향(Quote → User/Book)으로만 둔 이유: User·Book에 {@code @OneToMany}를 달지 않아도
 * 이 서비스 기능은 충분하다. 양방향은 동기화 부담만 늘리므로 필요할 때만 추가한다.
 */
@Entity
@Table(name = "quotes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String content;

    /** 문장이 있는 페이지. 사용자가 기록 안 할 수 있어 nullable. */
    private Integer page;

    /** 원본 촬영 이미지 URL. 선택. */
    @Column(length = 500)
    private String imageUrl;

    /** 커뮤니티 공개 여부. 기본은 비공개(false). */
    @Column(nullable = false)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Builder
    private Quote(String content, Integer page, String imageUrl, boolean isPublic, User user, Book book) {
        this.content = content;
        this.page = page;
        this.imageUrl = imageUrl;
        this.isPublic = isPublic;
        this.user = user;
        this.book = book;
    }

    /** 문장 내용·페이지 수정 (마일스톤 3의 PATCH에서 사용). */
    public void edit(String content, Integer page) {
        this.content = content;
        this.page = page;
    }

    /** 공개/비공개 전환 (커뮤니티 공유 토글). */
    public void changePublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
