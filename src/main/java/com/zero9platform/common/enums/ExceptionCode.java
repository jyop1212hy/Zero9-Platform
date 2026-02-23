package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 인증/인가
    AUTH_LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    AUTH_NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 리프래쉬 토큰
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Refresh Token이 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "Refresh Token 재사용이 감지되었습니다."),

    // 사용자
    USER_ADMIN_DATA_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자 관련 데이터는 사용할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
    USER_PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    USER_NOT_INFLUENCER(HttpStatus.FORBIDDEN, "해당 사용자는 인플루언서가 아닙니다."),
    USER_INFLUENCER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 인플루언서입니다."),
    USER_INFLUENCER_ALREADY_APPROVED(HttpStatus.CONFLICT, "이미 승인된 인플루언서입니다."),
    USER_LOGIN_ID_DUPLICATED(HttpStatus.CONFLICT, "중복되는 아이디가 존재합니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "중복되는 이메일이 존재합니다."),
    USER_PHONE_DUPLICATED(HttpStatus.CONFLICT, "중복되는 핸드폰번호가 존재합니다."),
    USER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "중복되는 닉네임이 존재합니다."),

    // 일반 게시물
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),

    // 댓글
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 답변이 완료된 문의사항입니다."),

    // 공동구매 게시물
    GPP_NOT_FOUND(HttpStatus.NOT_FOUND, "공동구매 게시물을 찰을 수 없습니다."),
    GPP_SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우한 공동구매 게시물이 없습니다."),
    GPP_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 하며, 시작일은 오늘 이전일 수 없습니다."),
    GPP_NO_PERMISSION(HttpStatus.FORBIDDEN, "공동구매 게시물에 대한 권한이 없습니다."),

    // 공동구매 게시물 일정 팔로우
    GROUP_PURCHASE_POST_ALREADY_SUBSCRIBED(HttpStatus.CONFLICT, "이미 팔로우한 공동구매 게시물입니다."),

    // 옵션
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 옵션을 찾을 수 없습니다."),
    OPTION_CANNOT_DELETE_LAST(HttpStatus.BAD_REQUEST, "옵션은 최소 1개 이상 유지되어야 합니다."),
    OPTION_SOLD_OUT(HttpStatus.BAD_REQUEST, "선택하신 옵션은 이미 품절되었습니다."),
    OPTION_INVALID_STOCK_INCREASE_QUANTITY(HttpStatus.BAD_REQUEST, "증가 수량은 1 이상이어야 합니다."),
    OPTION_CHANGE_NOT_ALLOWED_AFTER_SALE_START(HttpStatus.BAD_REQUEST,  "진행 중이거나 종료된 상품 판매 게시물의 옵션은 추가하거나 삭제할 수 없습니다."),
    OPTION_INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다. 남은 수량: %d"),

    // 상품판매 게시물
    PRODUCT_POST_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "판매가 진행 중인 상품만 주문할 수 있습니다."),
    PRODUCT_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 게시물이 존재하지 않습니다."),
    PRODUCT_POST_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 게시물 옵션을 찾을 수 없습니다."),
    PRODUCT_POST_CANNOT_UPDATE_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "판매가 이미 시작되었거나 종료된 게시물은 수정할 수 없습니다."),
    PRODUCT_POST_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 오늘 이전일 수 없으며, 종료일은 시작일 이후여야 합니다."),
    PRODUCT_POST_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "시작일과 종료일은 필수입니다."),

    // 주문 상품
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문 상품을 찾을 수 없습니다."),
    ORDER_ITEM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 주문된 상품입니다."),

    // 주문
    ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 주문입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다."),
    ORDER_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 주문 금액과 일치하지 않습니다."),

    // 검색
    SEARCH_LOGS_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "category는 product_title, product_name 또는 influencer 중 하나여야 합니다."),
    SEARCH_LOGS_PROFANITY_FILE_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "비속어 파일 처리 중 오류가 발생했습니다."),
    SEARCH_LOGS_PROFANITY_ALREADY_EXISTS(HttpStatus.CONFLICT, "[%s]은(는) 이미 등록된 단어입니다."),
    SEARCH_LOGS_PROFANITY_NOT_FOUND(HttpStatus.NOT_FOUND, "[%s]은(는) 등록되지 않은 단어입니다."),
    SEARCH_LOGS_PROFANITY_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "입력하신 [%s]은(는) 비속어 단어입니다."),
    SEARCH_LOGS_BULK_INDEXING_PRODUCT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "상품 게시글 벌크 인덱싱 중 오류가 발생했습니다."),
    SEARCH_LOGS_BULK_INDEXING_GPP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공동구매 게시글 벌크 인덱싱 중 오류가 발생했습니다."),

    // 찜
    FAVORITE_ALREADY(HttpStatus.BAD_REQUEST, "이미 찜한 상품입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 찜이 취소되었거나 존재하지 않습니다."),

    // 랭킹
    RANKING_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "기간 랭킹은 DAILY / WEEKLY / MONTHLY 만 지원합니다."),

    // FILE
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST,"업로드할 파일이 존재하지 않습니다."),

    // 토스 페이먼츠
    TOSS_PAYMENT_CONFIRM_FAIL(HttpStatus.BAD_REQUEST, "토스 결제 승인에 실패했습니다."),
    TOSS_PAYMENT_CONFIRM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토스 결제 승인 처리 중 서버 오류가 발생했습니다."),
    TOSS_PAYMENT_CANCEL_FAIL(HttpStatus.BAD_REQUEST, "토스 결제 취소에 실패했습니다."),
    TOSS_PAYMENT_CANCEL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토스 결제 취소 처리 중 서버 오류가 발생했습니다."),
    TOSS_PAYMENT_KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "결제 키를 찾을 수 없습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}