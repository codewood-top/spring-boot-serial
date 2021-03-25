package top.codewood.web.entity;

import java.io.Serializable;

public class User implements Serializable {

    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String avatar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 此方法返回的 user 密码相当明文(即未经加密)
     * @param username
     * @return
     */
    public static User getUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setNickname("code_" + username);
        user.setId(1L);
        user.setPassword("123456");
        user.setAvatar("http://img1.codewood.top/developer/images/code-logo-large.png");
        return user;
    }

}
