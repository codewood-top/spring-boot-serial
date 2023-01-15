package top.codewood.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codewood.config.property.WxAppProperties;
import top.codewood.entity.User;
import top.codewood.entity.WxId;
import top.codewood.entity.vo.user.WxUserInfo;
import top.codewood.exception.UserNotFoundException;
import top.codewood.service.repository.UserRepository;
import top.codewood.service.repository.WxIdRepository;
import top.codewood.wx.mnp.api.WxMnpUserApi;
import top.codewood.wx.mnp.bean.user.WxMnpUserInfo;

import java.util.Optional;


@Service
public class UserService {

    static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WxIdRepository wxIdRepository;

    @Autowired
    private WxAppProperties wxAppProperties;

    @Transactional
    public User getOrCreate(WxUserInfo wxUserInfo) {
        assert wxUserInfo != null;
        User user = null;
        WxId wxId = wxIdRepository.get(wxUserInfo.getOpenid());

        if (wxId != null && wxId.getUserId() != null) {
            user = userRepository.get(wxId.getUserId());
        }

        if (user != null) return user;

        user = new User();
        if (wxUserInfo.getUnionid() != null) {
            user.setUsername("wx_" + wxUserInfo.getUnionid().substring(wxUserInfo.getUnionid().length() - 8));
        } else {
            user.setUsername("wx_" + wxUserInfo.getOpenid().substring(wxUserInfo.getOpenid().length() - 8));
        }
        user.setPassword(wxUserInfo.getDefaultPassword());

        if (wxUserInfo.getAvatarUrl() != null && wxUserInfo.getNickname() != null) {
            user.setAvatar(wxUserInfo.getAvatarUrl());
            user.setNickname(wxUserInfo.getNickname());
            user.setAuthorized(true);
        }

        userRepository.save(user);

        wxId = wxId == null ? new WxId() : wxId;
        wxId.setOpenid(wxUserInfo.getOpenid());
        wxId.setAppid(wxUserInfo.getAppid());
        wxId.setUnionid(wxUserInfo.getUnionid());
        wxId.setSessionKey(wxUserInfo.getSessionKey());
        wxId.setUserId(user.getId());
        wxIdRepository.save(wxId);

        return user;
    }

    public User get(String username) {
        return userRepository.getByUsername(username);
    }

    @Transactional
    public void update(String username, String appid, String encryptedData, String iv) {
        User user = Optional.ofNullable(userRepository.getByUsername(username)).orElseThrow(UserNotFoundException::new);
        WxId userWxId = Optional.ofNullable(wxIdRepository.getByAppidAndUserId(appid, user.getId())).orElseThrow(() -> new RuntimeException("先登录，后执行更新用户信息操作。"));
        WxMnpUserInfo wxMnpUserInfo = WxMnpUserApi.getInstance().getUserInfo(userWxId.getSessionKey(), encryptedData, iv);
        user.setNickname(wxMnpUserInfo.getNickname());
        user.setAvatar(wxMnpUserInfo.getAvatarUrl());
        user.setAuthorized(true);
        userRepository.save(user);
    }
}
