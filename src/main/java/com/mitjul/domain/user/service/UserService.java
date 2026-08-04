package com.mitjul.domain.user.service;

import com.mitjul.domain.user.dto.LoginRequest;
import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.dto.TokenResponse;
import com.mitjul.domain.user.entity.User;
import com.mitjul.domain.user.repository.UserRepository;
import com.mitjul.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 도메인 서비스. 비즈니스 로직은 여기 모으고 컨트롤러는 얇게 유지한다(CLAUDE.md §7).
 */
@Service
@RequiredArgsConstructor // final 필드 생성자 주입
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입: 이메일 중복 검사 → 비밀번호 해싱 → 저장.
     * 쓰기 작업이므로 트랜잭션 경계를 이 public 메서드에 둔다.
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1) 이메일 중복 검사 (이미 만들어 둔 쿼리 메서드 재사용)
        if (userRepository.existsByEmail(request.email())) {
            // 마일스톤 3에서 커스텀 예외 + 전역 처리로 409 응답으로 다듬는다.
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        // 2) 비밀번호는 반드시 해싱해서 저장 (평문 저장 금지, CLAUDE.md §7)
        String encodedPassword = passwordEncoder.encode(request.password());
        User saved = userRepository.save(request.toEntity(encodedPassword));

        // 3) 엔티티가 아니라 응답 DTO로 변환해 반환
        return SignupResponse.from(saved);
    }

    /**
     * 로그인: 이메일로 조회 → 비밀번호 대조 → JWT 발급. 읽기 전용 트랜잭션.
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 보안: "이메일 없음"과 "비밀번호 틀림"을 같은 메시지로 처리
        //  → 어떤 이메일이 가입돼 있는지 공격자가 알아내지 못하게(계정 존재 노출 방지)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        return TokenResponse.bearer(accessToken);
    }
}
