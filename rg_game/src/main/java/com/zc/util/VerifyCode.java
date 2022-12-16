package com.zc.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class VerifyCode {
    private int width = 160;
    private int height = 40;
    private BufferedImage bufferedImage;
    private String code = new String();
    private String string = "wertyuiasdfghjkzxcvbnmQWERTYUIASDFGHJKLZXCVBNM2345678";

    public String getCode() {
        return code;
    }

    public VerifyCode() {
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();  //创建画笔
        graphics.setColor(Color.WHITE); //白色底色
        graphics.fillRect(0, 0, width, height); //绘制矩形
        Font font = new Font("", Font.BOLD, 20);    //验证码字体
        graphics.setFont(font);
        graphics.setColor(Color.black); //验证码颜色
        graphics.drawRect(0, 0, width - 1, height - 1); //绘制的边框
        Random random = new Random();
        //生成验证码
        for (int i = 1; i <= 4; i++) {
            graphics.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));  //验证码颜色
            char c = string.charAt(random.nextInt(string.length())); //随机一个验证码
            int h=random.nextInt(11)+25;//25--35
            graphics.drawString(c + "", width / 4 * i-(width/8), h); //验证码在画板上
            code += c;
        }
        //生成干扰线
        for (int i=0;i<15;i++){
            //干扰线颜色
            graphics.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            int x1=random.nextInt(width);
            int x2=random.nextInt(width);
            int y1=random.nextInt(height);
            int y2=random.nextInt(height);
            graphics.drawLine(x1,y1,x2,y2);
        }

    }
    public String getImg(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
        try {
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "data:image/png;base64,"+base64;

    }

}