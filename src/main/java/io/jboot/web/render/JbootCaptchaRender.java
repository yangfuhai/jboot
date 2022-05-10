package io.jboot.web.render;

import com.jfinal.captcha.CaptchaRender;
import com.jfinal.kit.StrKit;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class JbootCaptchaRender extends CaptchaRender {

    // 验证码随机字符数组
    protected static char[] charArray = "1234567890ABCDEFGHJKMNPQRSTUVWXY".toCharArray();


    /**
     * @param randomArrayString
     */
    public static void setRandomArrayString(String randomArrayString) {
        if (StrKit.isBlank(randomArrayString)) {
            throw new IllegalArgumentException("randomArrayString can not be blank.");
        }
        charArray = randomArrayString.toCharArray();
    }


    @Override
    protected String getRandomString() {
        char[] randomChars = new char[4];
        for (int i = 0; i < randomChars.length; i++) {
            randomChars[i] = charArray[ThreadLocalRandom.current().nextInt(charArray.length)];
        }
        return String.valueOf(randomChars);
    }


    @Override
    protected void drawGraphic(String randomString, BufferedImage image) {
        // 获取图形上下文
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // 图形抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体抗锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 设定背景色
        g.setColor(getRandomColor(210, 250, random));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //绘制小字符背景
        Color color = null;
        for (int i = 0; i < 20; i++) {
            color = getRandomColor(120, 200, random);
            g.setColor(color);
            String rand = String.valueOf(charArray[random.nextInt(charArray.length)]);
            g.drawString(rand, random.nextInt(WIDTH), random.nextInt(HEIGHT));
            color = null;
        }

        //设定字体
        g.setFont(RANDOM_FONT[random.nextInt(RANDOM_FONT.length)]);
        // 绘制验证码
        for (int i = 0; i < randomString.length(); i++) {
            //旋转度数 最好小于45度
            int degree = random.nextInt(28);
            if (i % 2 == 0) {
                degree = degree * (-1);
            }
            //定义坐标
            int x = 22 * i, y = 21;
            //旋转区域
            g.rotate(Math.toRadians(degree), x, y);
            //设定字体颜色
            color = getRandomColor(20, 130, random);
            g.setColor(color);
            //将认证码显示到图象中
            g.drawString(String.valueOf(randomString.charAt(i)), x + 8, y + 10);
            //旋转之后，必须旋转回来
            g.rotate(-Math.toRadians(degree), x, y);
        }
        //图片中间曲线，使用上面缓存的color
        g.setColor(color);
        //width是线宽,float型
        BasicStroke bs = new BasicStroke(3);
        g.setStroke(bs);
        //画出曲线
        QuadCurve2D.Double curve = new QuadCurve2D.Double(0d, random.nextInt(HEIGHT - 8) + 4, WIDTH / 2, HEIGHT / 2, WIDTH, random.nextInt(HEIGHT - 8) + 4);
        g.draw(curve);
        // 销毁图像
        g.dispose();
    }


}
