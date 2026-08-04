package com.mitjul.domain.user.entity;

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
 * 회원 엔티티.
 *
 * <p><b>테이블명을 {@code users}로 둔 이유</b>: {@code user}는 PostgreSQL·H2 등에서 예약어라
 * 테이블명으로 쓰면 쿼리가 깨질 수 있다. 개발 H2도 MODE=PostgreSQL로 맞춰 두었으므로 동일하게 적용된다.
 *
 * <p><b>비밀번호</b>는 여기서 문자열을 그대로 저장만 한다. BCrypt 해싱은 Service 계층(마일스톤 2)에서
 * 처리한다 — 엔티티는 "무엇을 저장할지"만 알고, "어떻게 안전하게 만들지"는 서비스의 책임.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요하지만, 외부에서 빈 User를 못 만들게 막는다
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 auto-increment에 위임 (H2/PostgreSQL 모두 지원)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    /**
     * 생성은 빌더로만. 필드가 늘어도 호출부가 명확하고, 불변에 가깝게 다룰 수 있다.
     * 생성자를 private으로 두어 "빌더를 통한 생성"만 허용한다.
     */
    @Builder
    private User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    /**
     * 닉네임 변경. setter를 열어두는 대신 의미가 드러나는 도메인 메서드로 노출한다.
     * (엔티티의 상태 변경 지점을 통제하기 위함)
     */
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
