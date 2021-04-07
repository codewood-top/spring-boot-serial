package top.codewood.wx.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class Strings {

    private static final String RANDOM_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static final String EMPTY = "";

    public static String randomString(int length) {
        assert length > 0;
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_STR.charAt(r.nextInt(RANDOM_STR.length())));
        }
        return sb.toString();
    }

    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static boolean hasText(String content) {
        return content != null && content.trim().length() > 0;
    }

}
