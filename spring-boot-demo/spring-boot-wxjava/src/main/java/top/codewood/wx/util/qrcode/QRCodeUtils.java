package top.codewood.wx.util.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import top.codewood.wx.util.Strings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;

public class QRCodeUtils {

    static final String CHAR_SET = "utf-8";
    static final String FORMAT_NAME = "jpg";

    static final int QRCODE_SIZE = 300;
    static final int LOGO_SIZE = 60;

    private static BufferedImage doCreate(String content, BufferedImage logo) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHAR_SET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (logo != null) {
            insertImage(image, logo);
        }
        return image;
    }

    private static void insertImage(BufferedImage source, BufferedImage logo) throws IOException {
        int width = logo.getWidth(null);
        int height = logo.getHeight(null);

        Image logoImg = logo;

        Graphics2D graph = source.createGraphics();
        if (width > LOGO_SIZE) {
            width = height = LOGO_SIZE;
            Image image = logo.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            logoImg = image;
        }
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(logoImg, x, y, width, height, null);
        Shape shape =  new RoundRectangle2D.Float(x, y, width, height, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成二维码
     * @param content 二维码内容
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public static BufferedImage create(String content) throws IOException, WriterException {
        return doCreate(content, null);
    }

    /**
     *
     * @param content
     * @param logoFile
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public static BufferedImage create(String content, File logoFile) throws IOException, WriterException {
        return doCreate(content, ImageIO.read(logoFile));
    }

    /**
     *
     * @param content
     * @param logoInputStream
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public static BufferedImage create(String content, InputStream logoInputStream) throws IOException, WriterException {
        return doCreate(content, ImageIO.read(logoInputStream));
    }

    /**
     *
     * @param content
     * @param logoUrl
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public static BufferedImage create(String content, String logoUrl) throws IOException, WriterException {
        return doCreate(content, ImageIO.read(new URL(logoUrl)));
    }

    public static void write(BufferedImage image, OutputStream outputStream) throws IOException {
        ImageIO.write(image, FORMAT_NAME, outputStream);
    }

    /**
     * 解析二维码内容
     * @param image
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decode(BufferedImage image) throws IOException, NotFoundException {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHAR_SET);
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 解析二维码内容
     * @param inputStream
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decode(InputStream inputStream) throws IOException, NotFoundException {
        return decode(ImageIO.read(inputStream));
    }

    /**
     * 解析二维码内容
     * @param url
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decode(String url) throws IOException, NotFoundException {
        return decode(ImageIO.read(new URL(url)));
    }

}