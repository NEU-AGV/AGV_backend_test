package com.moxin.agvbackend.utils;

public class EmailUtils {

    public static String genEmailMessage(Integer code) {
        return "您的验证码是：" + code + "，请不要泄露给他人。";
    }

    public static String genEmailSubject() {
        return "【仓储管理系统】邮箱验证码";
    }

    public static Integer genEmailCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }
}
