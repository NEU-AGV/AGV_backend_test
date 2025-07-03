package com.moxin.agvbackend.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

public class CaptchaUtils {

    public static class Captcha {
        public String uuid;
        public String imgBase64;
        public String code;
    }

    public static Captcha generateCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(120, 40, 4, 20);

        Captcha result = new Captcha();
        result.uuid = UUID.randomUUID().toString();
        result.code = captcha.getCode();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        captcha.write(out);
        result.imgBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());

        return result;
    }
}
