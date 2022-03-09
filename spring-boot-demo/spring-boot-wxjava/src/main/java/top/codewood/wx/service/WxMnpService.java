package top.codewood.wx.service;

import top.codewood.wx.mnp.bean.auth.WxMnpCode2SessionResult;
import top.codewood.wx.mnp.bean.user.WxMnpPhoneInfo;
import top.codewood.wx.mnp.bean.user.WxMnpUserInfo;

public interface WxMnpService {

    WxMnpCode2SessionResult code2Session(String code);

    String getAccessToken();

    WxMnpUserInfo getUserInfo(String openid, String encryptedData, String iv);

    boolean checkEncryptedData(String encryptedData);

    WxMnpPhoneInfo getPhoneNumber(String openid, String encryptedData, String iv);

    WxMnpPhoneInfo getPhoneNumber(String code);
}
