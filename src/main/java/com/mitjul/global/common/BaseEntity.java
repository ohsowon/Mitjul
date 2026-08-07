package com.mitjul.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 모든 엔티티가 공통으로 갖는 생성/수정 시각을 담는 상위 클래스.
 *
 * 이 클래스는 @MappedSuperclass라서 그 자체로는 테이블이 되지 않는다. 대신 이걸 상속한
 * 엔티티(User, Book, Quote ...)의 테이블에 created_at / updated_at 컬럼으로 합쳐진다.
 * 즉 "공통 컬럼을 한 곳에 모아 상속으로 재사용"하는 장치다.
 *
 * 값은 AuditingEntityListener가 자동으로 채운다. 이게 동작하려면 진입점
 * (MitjulApplication)에 @EnableJpaAuditing이 켜져 있어야 한다 — 이미 켜 두었다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /** 최초 저장 시각. 한번 정해지면 바뀌지 않도록 updatable=false. */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /** 마지막 수정 시각. 엔티티가 변경될 때마다 자동 갱신된다. */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
