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

    private Integer page;

    @Column(length = 500)
    private String imageUrl;

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

    public void edit(String content, Integer page) {
        this.content = content;
        this.page = page;
    }

    public void changePublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
