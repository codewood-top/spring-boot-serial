package top.codewood.config.security.oauth2.provider.sms;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultSmsCodeService implements SmsCodeService {

    private static final Map<String, String> PHONE_CODE_MAP;

    static {
        PHONE_CODE_MAP = new HashMap<>();
    }

    @Override
    public String createCode(String phone) {
        verifyPhoneNumber(phone);
        String code = randomNumberCode();
        PHONE_CODE_MAP.put(phone, code);
        return code;
    }

    @Override
    public void consumeCode(String phone, String code) {
        assert phone != null && code != null;
        String storedCode = PHONE_CODE_MAP.get(phone);
        if (storedCode == null || !storedCode.equals(code)) throw new InvalidGrantException("无效的验证码！");
        PHONE_CODE_MAP.remove(phone);
    }

    private void verifyPhoneNumber(String phone) {
        assert phone != null;
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid phone number: " + phone);
        }

    }

    /**
     * 随机生产6位数字
     * @return
     */
    private String randomNumberCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
