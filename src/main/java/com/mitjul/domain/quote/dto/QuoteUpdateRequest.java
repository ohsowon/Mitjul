package com.mitjul.domain.quote.dto;

/**
 * 문장 수정 요청 DTO (PATCH, 부분 수정).
 * 값이 있는(non-null) 필드만 반영한다. 예: isPublic만 보내면 공개 여부만 토글된다.
 * (isPublic이 Boolean 래퍼인 이유: "미지정(null)"과 "false"를 구분하기 위해서다.)
 */
public record QuoteUpdateRequest(
        String content,
        Integer page,
        Boolean isPublic
) {
}
