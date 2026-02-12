package com.zero9platform.common.enums;

import lombok.Getter;

@Getter
public enum Category {

    // 식품
    DESSERT("디저트"),
    INSTANT("인스턴트"),
    BEVERAGE("음료"),
    COFFEE_TEA("커피/차"),
    SNACK("과자"),
    NOODLE("면류"),
    MEAT("육류"),
    SEAFOOD("해산물"),
    FRUIT("과일"),
    HEALTH_FOOD("건강식품"),

    // 생활 / 주방
    KITCHEN("주방용품"),
    HOUSEHOLD("생활용품"),
    CLEANING("청소용품"),
    BATHROOM("욕실용품"),
    STORAGE("수납/정리"),

    // 패션 / 뷰티
    FASHION("패션"),
    SHOES("신발"),
    BAG("가방"),
    ACCESSORY("액세서리"),
    BEAUTY("뷰티"),
    COSMETIC("화장품"),

    // 디지털 / 가전
    DIGITAL("디지털"),
    APPLIANCE("가전제품"),
    MOBILE_ACCESSORY("모바일 액세서리"),

    // 취미 / 기타
    HOBBY("취미/여가"),
    PET("반려동물"),
    BABY("유아/아동"),
    BOOK("도서"),
    ETC("기타");

    private final String description;

    Category(String description) {
        this.description = description;
    }

//    // 변환 메서드
//    public static Category from(String value) {
//        return Arrays.stream(values()) // // values() : enum에 있는 모든 값들을 배열로 가져옴
//                .filter(c -> c.description.equals(value))
//                .findFirst()
//                .orElseThrow(() ->
//                        new CustomException(ExceptionCode.GPP_CATEGORY_NOT_FOUND));
//    }
}