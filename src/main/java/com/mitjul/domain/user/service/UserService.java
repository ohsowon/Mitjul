package com.mitjul.domain.user.service;

import com.mitjul.domain.user.dto.LoginRequest;
import com.mitjul.domain.user.dto.SignupRequest;
import com.mitjul.domain.user.dto.SignupResponse;
import com.mitjul.domain.user.dto.TokenResponse;
import com.mitjul.domain.user.dto.UserResponse;
import com.mitjul.domain.user.entity.User;
import com.mitjul.domain.user.repository.UserRepository;
import com.mitjul.global.exception.BusinessException;
import com.mitjul.global.exception.ErrorCode;
import com.mitjul.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 도메인 서비스. 비즈니스 로직은 여기 모으고 컨트롤러는 얇게 유지한다(CLAUDE.md §7).
 * 오류 상황은 BusinessException으로 던지고, 상태코드·응답 변환은 전역 핸들러에 맡긴다.
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
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호는 반드시 해싱해서 저장 (평문 저장 금지, CLAUDE.md §7)
        String encodedPassword = passwordEncoder.encode(request.password());
        User saved = userRepository.save(request.toEntity(encodedPassword));

        return SignupResponse.from(saved);
    }

    /**
     * 로그인: 이메일로 조회 → 비밀번호 대조 → JWT 발급. 읽기 전용 트랜잭션.
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // 보안: "이메일 없음"과 "비밀번호 틀림"을 같은 오류로 처리
        //  → 어떤 이메일이 가입돼 있는지 공격자가 알아내지 못하게(계정 존재 노출 방지)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        return TokenResponse.bearer(accessToken);
    }

    /**
     * 내 정보 조회. JWT 필터가 넣어 준 userId로 회원을 찾아 응답 DTO로 반환한다.
     */
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }
}
