package top.codewood.config.security.oauth2.provider.sms;

public interface SmsCodeService {

    /**
     * 生产一个随机码
     * @param phone
     * @return
     */
    String createCode(String phone);

    /**
     * 消费随机码
     * @param phone
     * @param code
     */
    void consumeCode(String phone, String code);

}
