package com.mitjul.domain.book.entity;

import com.mitjul.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도서 엔티티.
 *
 * ISBN 전역 유니크: 커뮤니티 검색이 서비스의 핵심이라, 같은 책은 어느 사용자가 등록했든
 * DB에 딱 하나만 존재해야 한다(CLAUDE.md §4). 그래서 isbn에 unique 제약을 건다. "이 ISBN의 책이
 * 있으면 재사용, 없으면 생성"하는 비즈니스 로직은 Service 계층에서 처리한다(엔티티는 제약만 보장).
 *
 * 제목만 필수로 두고 저자·출판사·표지는 nullable로 둔 이유: 이 값들은 외부 도서 API(마일스톤 5)에서
 * 채워지는데 응답에 일부가 빠질 수 있어 관대하게 받는다.
 */
@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;

    private String publisher;

    @Column(length = 500) // 표지 이미지 URL은 길 수 있어 넉넉히
    private String coverUrl;

    @Builder
    private Book(String isbn, String title, String author, String publisher, String coverUrl) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.coverUrl = coverUrl;
    }
}
