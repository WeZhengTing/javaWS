package com.zc.controller.front;

import com.jfinal.aop.Before;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.zc.model.User;
import com.zc.service.UserService;
import com.zc.service.impl.UserServiceImpl;
import com.zc.util.VerifyCode;
import com.zc.validator.FrontUserValidator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Path("/front/user")
public class UserController extends FrontBaseController{
    public  static Map<String,User> tokenMap =new HashMap<String,User>();
    UserService us= new UserServiceImpl();

    public void getVerifyCode(){
        VerifyCode verifyCode =new VerifyCode();
        getSession().setAttribute("code",verifyCode.getCode());
        renderJson(Ret.ok("data",verifyCode.getImg()));
    }
    @Before(FrontUserValidator.class)
    public void login(){
        String username=getPara("username");
        String pwd=getPara("pwd");
        String verifyCode=getPara("verifyCode");
        if (getSession().getAttribute("code")==null){
            renderJson(Ret.fail("验证码已失效，请重试！"));
        }else{
            String sessionCode= (String) getSession().getAttribute("code");
            if (verifyCode.toUpperCase().equals(sessionCode.toUpperCase())){
                boolean flag=us.login(username,pwd,getResponse());
                if (flag){
                    renderJson(Ret.ok("登入成功！"));
                }else {
                    renderJson(Ret.fail("登入失败,账号密码错误或状态非正常！"));
                }
            }else {
                renderJson(Ret.fail("验证码错误,请重新！"));
            }
        }

    }
}
