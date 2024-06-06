package top.codewood.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RestController
public class CryptoController {

    private static final String SECRET_KEY = "YPHsX0t63NFwgelw"; // Replace with your secret key
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String INIT_VECTOR = "UxWGB99Q7bITYb7z"; // Replace with your IV

    @PostMapping("/encrypt")
    public String encryptData(String data) throws Exception {
        return encrypt(data);
    }

    @PostMapping("/decrypt")
    public String decryptData(String data, String iv) throws Exception {
        return decrypt(data, iv);
    }

    private String encrypt(String data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Arrays.copyOf(SECRET_KEY.getBytes(StandardCharsets.UTF_8), 16), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptedBytes);
    }

    private String decrypt(String data, String iv)  throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Arrays.copyOf(SECRET_KEY.getBytes(StandardCharsets.UTF_8), 16), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decryptedBytes = new BASE64Decoder().decodeBuffer(data);
        return new String(cipher.doFinal(decryptedBytes));
    }

}
