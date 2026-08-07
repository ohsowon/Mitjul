package com.mitjul.domain.user.repository;

import com.mitjul.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 회원 저장소.
 *
 * JpaRepository<User, Long>만 상속해도 save/findById/findAll/delete 등 기본 CRUD가
 * 자동으로 제공된다. 구현 클래스를 우리가 만들 필요가 없다 — Spring Data가 런타임에 프록시로 생성한다.
 *
 * 아래 두 메서드는 쿼리 메서드(Query Method)다. 메서드 이름을 규칙에 맞게 지으면
 * Spring Data가 그에 맞는 SQL을 자동으로 만들어 준다. (findBy/existsBy + 필드명)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 이메일로 회원 조회 → select ... from users where email = ? (로그인 시 사용, 마일스톤 2) */
    Optional<User> findByEmail(String email);

    /** 이메일 중복 여부 → select count(*) ... where email = ? (회원가입 시 중복 검사) */
    boolean existsByEmail(String email);
}
