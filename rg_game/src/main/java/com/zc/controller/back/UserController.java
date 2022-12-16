package com.zc.controller.back;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Path;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.zc.model.User;
import com.zc.service.UserService;
import com.zc.service.impl.UserServiceImpl;
import com.zc.util.Util;
import com.zc.validator.UserValidator;

import java.util.List;

@Path("/back/user")
public class UserController extends BackBaseController{
    UserService us=new UserServiceImpl();
    public  void checkUsername(){
        String username= getPara("username");
        boolean flag=us.checkUsername(username);
        if (flag){
            renderJson(Ret.fail("用户名重复！"));
        }else {
            renderJson(Ret.ok("用户名不重复！"));
        }
    }
    @Before(UserValidator.class)
    public void add(){
        //二进制流文件上传
           UploadFile uf = getFile();
//           文件移到
       String fileName= Util.moveFileToDireTory(uf.getFile(), PropKit.get("userHeadImgPath"));
        User user=getBean(User.class,"");
        if (fileName==null){
            renderJson(Ret.fail("上传文件失败!"));
        }else {
           user.setHeadImg(fileName);
        }
        boolean flag=us.insertUser(user);
        if (flag){
            renderJson(Ret.ok("添加成功！"));
        }else {
            renderJson(Ret.fail("添加失败！"));
        }

    }
    //必须验证getFile() 文件上传
    public void edit(){
        //二进制流文件上传
        UploadFile uf = getFile();
        User user=getBean(User.class,"");
      //不改头像
        if(uf==null){
            user.setHeadImg(null);
        }else {
            String fileName= Util.moveFileToDireTory(uf.getFile(), PropKit.get("userHeadImgPath"));

            if (fileName==null){
                renderJson(Ret.fail("上传文件失败!"));
                return;
            }else {
                user.setHeadImg(fileName);
            }
        }
        boolean isChange=getParaToBoolean("isChange");
        boolean flag=us.editUser(user,isChange);
        if (flag){
            renderJson(Ret.ok("编辑成功！"));
        }else {
            renderJson(Ret.fail("编辑失败！"));
        }


    }
    public void del(){
        int id =getParaToInt("id");
        boolean flag=us.deleteUserById(id);
        if (flag){
            renderJson(Ret.ok("删除成功!"));
        }else {
            renderJson(Ret.fail("删除失败!请重试!"));
        }
    }
    public void list(){
        int limit=getParaToInt("limit");//一页几个
        int page=getParaToInt("page");//第几页
        String username=getPara("username");
        String userStatus= getPara("userStatus");
        List<User> list=us.getUserListByConection(page,limit,username,userStatus);
        long count=us.getUserCountByConection(username,userStatus);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code",0);
        jsonObject.put("msg","");
        jsonObject.put("data",list);
        jsonObject.put("count",count);
        renderJson(jsonObject);
    }
    public void show(){
        int id=getParaToInt("id");
        User user=us.getUserById(id);
        if (user==null){
            renderJson(Ret.fail("当前用户不存在，或出现异常！"));
        }else {
            renderJson(Ret.ok("data",user));
        }
    }
}
