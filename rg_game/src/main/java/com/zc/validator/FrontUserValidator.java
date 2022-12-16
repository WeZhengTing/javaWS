package com.zc.validator;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

public class FrontUserValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        setRet(Ret.fail());
        if ("/front/user/login".equals(getActionKey())){
            validateRequired("username", "msg", "用户名不能为空");
            validateRequired("pwd", "msg", "密码不能为空");
            validateRequired("verifyCode","msg","验证码不能为空");
        }
    }

    @Override
    protected void handleError(Controller controller) {
            controller.renderJson(getRet());
    }
}
