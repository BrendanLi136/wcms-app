package com.wcms.constant;

/**
 * 性别枚举
 * 1:Male, 2:Female
 */
public enum GenderEnum {
    MALE(1, "男"),
    FEMALE(2, "女");
    final int code;
    final String name;

    GenderEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String fromCode(int code) {
        for (GenderEnum gender : values()) {
            if (gender.getCode() == code) {
                return gender.getName();
            }
        }
        throw new IllegalArgumentException("Invalid gender code: " + code);
    }

}
