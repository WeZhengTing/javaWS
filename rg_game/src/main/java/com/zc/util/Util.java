package com.zc.util;

import com.jfinal.kit.PropKit;
import com.zc.model.Data;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {


    /**
     * 将某文件移动到某文件夹里 重新修改名字(时间戳)
     * @param file
     * @param path
     * @return 文件名，如果上传失败则返回null
     */
    public static String moveFileToDireTory(File file,String path){
        if (!new File(path).exists()){
          boolean flag = new File(path).mkdirs();
          if (!flag){
              System.out.println("文件夹创建失败!");
              return null;
          }
        }
//        无论如何一定有这个文件夹
        int indexOf=file.getName().lastIndexOf(".");
        if (indexOf==-1){
            System.out.println("文件有异常，无后缀名！");
            return  null;
        }
        String exist=file.getName().substring(indexOf);
        String fileName=System.currentTimeMillis()+exist;
        boolean flag= file.renameTo(new File(path,fileName));
        if (flag){
            return PropKit.get("userHeadImgPathRel")+fileName;
        }else {
            return null;
        }
    }
    public static String getNow(){
        Date date =new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(date);
    }

    /**
     * 判断sql注入
     * @param str
     * @return 返回true=安全 false=危险
     */
    public static boolean sqlCheck(String str){
        String begin="您的请求参数信息";

//可以通过配置文件，去配置这些特殊字符，以便随时添加一些关键字。
        String pattern="and^exec^execute^insert^select^delete^update^count^drop^%^chr^mid^master^truncate^char^declare^sitename^net user^xp_cmdshell^;^or^-^+^,^like";
        String[] searchStr=pattern.split("^");
        for (String string:searchStr
             ) {
            if (str.indexOf(string)!=-1){
                return  false;
            }
        }
        return true;
    }


    public  static String getToken(){
        return UUID.randomUUID().toString();
    }
    public  static void  addCookie(HttpServletResponse resp,String name,String value,int day){
        Cookie cookie=new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(60*60*24*day);
        resp.addCookie(cookie);
    }
    public  static  String  getCookie(HttpServletRequest req,String name){
        Cookie[] cookies =req.getCookies();
        if(cookies==null) return  null;
        for (Cookie cookie:cookies){
            if(name.equals(cookie.getName())){
                return  cookie.getValue();
            }
        }
        return null;
    }
}
