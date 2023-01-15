package top.codewood.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthService {

    static final Logger LOGGER = LoggerFactory.getLogger(OAuthService.class);

    private static final Map<String, CodeAuth> CODE_AUTH_MAP = new ConcurrentHashMap<>();

    public String generateCode() {
        String code = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        CODE_AUTH_MAP.put(code, new CodeAuth(code));
        return code;
    }

    public void setCode(String code, String username) {
        CodeAuth codeAuth = Optional.ofNullable(CODE_AUTH_MAP.get(code)).orElseThrow(() -> new RuntimeException("无效的code"));
        if (codeAuth.expireTime.isBefore(LocalDateTime.now())) {
            codeAuth.status = -1;
            CODE_AUTH_MAP.put(code, codeAuth);
            throw new RuntimeException("二维码已过期");
        }
        codeAuth.username = username;
        codeAuth.status = 2;
        codeAuth.expireTime = LocalDateTime.now().plusMinutes(1);
        CODE_AUTH_MAP.put(code, codeAuth);
    }

    public void confirmCode(String code) {
        CodeAuth codeAuth = Optional.ofNullable(CODE_AUTH_MAP.get(code)).orElseThrow(() -> new RuntimeException("无效的code"));
        if (codeAuth.status != 2) throw new RuntimeException("状态异常");
        codeAuth.status = 1;
        CODE_AUTH_MAP.put(code, codeAuth);
    }

    public String codeUsername(String code) {
        CodeAuth codeAuth = Optional.ofNullable(CODE_AUTH_MAP.get(code)).orElseThrow(() -> new RuntimeException("无效的code"));
        if (codeAuth.status != 1) throw new RuntimeException("未扫描确认");
        return codeAuth.username;
    }

    public CodeAuth getCodeAuth(String code) {
        CodeAuth codeAuth = Optional.ofNullable(CODE_AUTH_MAP.get(code)).orElseThrow(() -> new RuntimeException("无效的code"));
        if (codeAuth.expireTime.isBefore(LocalDateTime.now())) {
            codeAuth.status = -1;
            CODE_AUTH_MAP.put(code, codeAuth);
        }
        return codeAuth;

    }

    public static class CodeAuth {
        private String code;
        private String username;
        private int status;
        private LocalDateTime expireTime;

        CodeAuth(String code) {
            this.code = code;
            expireTime = LocalDateTime.now().plusMinutes(1);
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public LocalDateTime getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
        }
    }

}
