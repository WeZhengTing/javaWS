package com.zc.validator;


import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;
import com.zc.service.UserService;
import com.zc.service.impl.UserServiceImpl;

public class UserValidator extends Validator {
    UserService us=new UserServiceImpl();
    @Override
    protected void validate(Controller controller) {
        setRet(Ret.fail());
        if ("/back/user/add".equals(getActionKey())){
            controller.getFile();
            validateRequired("username", "msg", "用户名不能为空");
            validateRequired("pwd", "msg", "密码不能为空");
            validateRequired("userStatus","msg","用户状态不能为空");
//            用户重复性验证
            String username=controller.getPara("username");
            if (username!=null){
                boolean flag=us.checkUsername(username);
                if (flag){
                    addError("msg","用户名重复!");
                }
            }


        }

    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson(getRet());
    }
}
