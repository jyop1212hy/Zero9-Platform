package com.zero9platform.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 공통
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 사용자
    ADMIN_DATA_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자 관련 데이터는 사용할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    USER_IS_NOT_INFLUENCER(HttpStatus.FORBIDDEN, "해당 사용자는 인플루언서가 아닙니다."),
    INFLUENCER_SOCIAL_LINK_REQUIRED(HttpStatus.BAD_REQUEST, "인플루언서 계정은 소셜 링크 입력이 필수입니다."),
    INFLUENCER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 인플루언서입니다."),
    INFLUENCER_ALREADY_APPROVED(HttpStatus.CONFLICT, "이미 승인된 인플루언서입니다."),
    LOGINID_EXIST(HttpStatus.CONFLICT, "중복되는 아이디가 존재합니다."),
    EMAIL_EXIST(HttpStatus.CONFLICT, "중복되는 이메일이 존재합니다."),
    PHONE_EXIST(HttpStatus.CONFLICT, "중복되는 핸드폰번호가 존재합니다."),
    NICKNAME_EXIST(HttpStatus.CONFLICT, "중복되는 닉네임이 존재합니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다. 남은 수량: %d"),
    PRODUCT_DELETED_CANNOT_VIEW_PRODUCT_POST(HttpStatus.BAD_REQUEST, "해당 상품이 삭제되어 상품 게시물을 조회할 수 없습니다."),
    PROFILE_IMAGE_NOT_FOUND_OR_INVALID(HttpStatus.BAD_REQUEST, "삭제할 프로필 이미지가 없거나 올바르지 않습니다."),

    // 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    NOT_FOUND_GPP(HttpStatus.NOT_FOUND, "공동구매 게시물을 찾을 수 없습니다."),
    NOT_FOUND_GPP_SUBSCRIPTION(HttpStatus.NOT_FOUND, "팔로우한 공동구매 게시물이 없습니다."),
    PRODUCT_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 게시물이 존재하지 않습니다."),
    PRODUCT_POST_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 게시물 옵션을 찾을 수 없습니다."),

    // jwt
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),

    // 댓글
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // GPP - 400 BAD REQUEST
    GPP_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 하며, 시작일은 오늘 이전일 수 없습니다."),
    PP_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 오늘 이전일 수 없으며, 종료일은 시작일 이후여야 합니다."),
    PP_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "시작일과 종료일은 필수입니다."),
    ORDER_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 주문 금액과 일치하지 않습니다."),

    // GPP - 401 FORBIDDEN
    GPP_NO_PERMISSION(HttpStatus.FORBIDDEN, "공동구매 게시물에 대한 권한이 없습니다."),

    // GPP - 404 NOT FOUND
    GPP_NOT_FOUND(HttpStatus.NOT_FOUND, "공동구매 게시물을 찰을 수 없습니다."),
    GPP_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    GPP_APPROVAL_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "승인상태를 찾을 수 없습니다."),
    GPP_PROGRESS_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "진행상태를 찾을 수 없습니다."),
    PP_PROGRESS_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "진행상태를 찾을 수 없습니다."),
    ORDER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 진행상태를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),

    //검색
    INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "검색어가 비어있을 수 없습니다"),
    CATEGORY_FALSE(HttpStatus.NOT_FOUND, "category는 product_title, product_name 또는 influencer 둘 중 하나여야 합니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품이 없습니다."),
    NOT_FOUND_SEARCH_CONTEXT(HttpStatus.NOT_FOUND, "검색리스트가 없습니다."),
    PROFANITY_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "비속어 설정 파일을 찾을 수 없습니다."),
    PROFANITY_FILE_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "비속어 파일 처리 중 오류가 발생했습니다."),
    PROFANITY_ALREADY_EXISTS(HttpStatus.CONFLICT, "[%s]은(는) 이미 등록된 단어입니다."),
    PROFANITY_NOT_FOUND(HttpStatus.NOT_FOUND, "[%s]은(는) 등록되지 않은 단어입니다."),
    PROFANITY_NOT_ALLOWED(HttpStatus.BAD_REQUEST, " 입력하신 [%s]은(는) 비속어 단어 입니다."),

    //찜
    ALREADY_FAVORITE(HttpStatus.BAD_REQUEST, "이미 찜한 상품입니다."),
    NOT_FOUND_FAVORITE(HttpStatus.NOT_FOUND, "이미 찜이 취소되었거나 존재하지 않습니다."),

    //랭킹
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "기간 랭킹은 DAILY / WEEKLY / MONTHLY 만 지원합니다."),
    DUPLICATE_PERIOD(HttpStatus.BAD_REQUEST, "기간은 하나만 선택할 수 있습니다."),
    PERIOD_REQUIRED(HttpStatus.BAD_REQUEST, "기간(period)은 필수 값입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "조회 시작일은 종료일보다 이후일 수 없습니다."),
    NOT_FOUND_RANKING_LIST(HttpStatus.NOT_FOUND, "랭킹리스트가 없습니다."),

    // GppComment - 404 NOT FOUND
    GPP_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "공동구매 게시물 댓글을 찾을 수 없습니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 옵션을 찾을 수 없습니다."),
    ORDERITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문 상품을 찾을 수 없습니다."),
    ALREADY_ORDERED(HttpStatus.BAD_REQUEST, "이미 주문이 완료된 상품입니다."),

    // 409
    ALREADY_SUBSCRIBED_GPP(HttpStatus.CONFLICT, "이미 팔로우한 공동구매 게시물입니다."),

    // FILE
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST,"업로드할 파일이 존재하지 않습니다."),

    // 옵션
    OPTION_IS_DISABLED(HttpStatus.BAD_REQUEST, "옵션이 비활성화 상태입니다."),
    OPTION_CANNOT_DELETE_LAST(HttpStatus.BAD_REQUEST, "옵션은 최소 1개 이상 유지되어야 합니다."),
    OPTION_SOLD_OUT(HttpStatus.BAD_REQUEST, "선택하신 옵션은 이미 품절되었습니다."),
    OPTION_INVALID_STOCK_INCREASE_QUANTITY(HttpStatus.BAD_REQUEST, "증가 수량은 1 이상이어야 합니다."),

    // 주문 상품
    CANNOT_CREATE_AN_ORDERITEM(HttpStatus.NOT_FOUND, "상품 게시물이 비활성화 상태라 주문 상품을 생성할 수 없습니다."),
    OPTION_CHANGE_NOT_ALLOWED_AFTER_SALE_START(HttpStatus.BAD_REQUEST,  "진행 중이거나 종료된 상품 판매 게시물의 옵션은 추가하거나 삭제할 수 없습니다."),
    SALE_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "판매가 진행 중인 상품만 주문할 수 있습니다."),

    // 주문
    ALREADY_ORDERED_ORDERITEM(HttpStatus.CONFLICT, "이미 주문된 주문 상품입니다."),

    // 리프래쉬 토큰
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Refresh Token이 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "Refresh Token 재사용이 감지되었습니다.")
    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }
}
