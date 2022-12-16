package com.zc.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import com.zc.controller.front.UserController;
import com.zc.model.Data;
import com.zc.model.User;
import com.zc.service.UserService;
import com.zc.util.MD5Util;
import com.zc.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public boolean checkUsername(String username) {
        long count = Db.queryLong("SELECT COUNT(*) FROM `zc_user` WHERE `username`=?", username);
        return count > 0;
    }

    @Override
    public boolean insertUser(User user) {
        user.setPwd(MD5Util.GetMD5Code(user.getPwd()));
        user.setCreateTime(Util.getNow());
        return user.save();
    }

    @Override
    public List<User> getUserListByConection(int page, int limit, String username, String userStatus) {
        String where="WHERE 1=1";
        if (!StringUtils.isEmpty(username)){
            where+=" AND `username` LIKE '%"+username+"%'";
            if (!Util.sqlCheck(username)){
                return  null;
            }
        }
        if (!StringUtils.isEmpty(userStatus)){
            where+=" AND `userStatus` ="+userStatus;
            if (!Util.sqlCheck(userStatus)){
                return  null;
            }
        }
        Page<User> dbList = User.dao.paginate(page, limit, "SELECT * ", "FROM `zc_user`"+where+" ORDER BY `createTime` DESC ");
        List<User> list = dbList.getList();
        for (User user : list
        ) {
            user.setPwd("");
            user.setHeadImg(PropKit.get("urlPath")+user.getHeadImg())  ;
        }
        return list;
    }

    @Override
    public long getUserCountByConection(String username, String userStatus) {
        String where="WHERE 1=1";
        if (!StringUtils.isEmpty(username)){
            where+=" AND `username` LIKE '%"+username+"%'";
            if (!Util.sqlCheck(username)){
                return  0;
            }
        }
        if (!StringUtils.isEmpty(userStatus)){
            where+=" AND `userStatus` ="+userStatus;
            if (!Util.sqlCheck(userStatus)){
                return  0;
            }
        }
        return Db.queryLong("SELECT COUNT(*) FROM `zc_user`"+where);
    }



    @Override
    public boolean deleteUserById(int id) {
        return User.dao.deleteById(id);
    }

    @Override
    public User getUserById(int id) {
        User user=User.dao.findById(id);
        if (user==null)return null;
        user.setPwd("");
        user.setHeadImg(PropKit.get("urlPath")+user.getHeadImg())  ;
        return user;
    }

    @Override
    public boolean editUser(User user, boolean isChange) {
        //先从数据库里根据id获取原数据，把需要修改的内容修改掉，再更新
        User oldUser=User.dao.findById(user.getId());
        if (oldUser==null) return false;
        oldUser.setUserStatus(user.getUserStatus());
        if (user.getHeadImg()!=null){
            oldUser.setHeadImg(user.getHeadImg());

        }
        if (isChange){
            oldUser.setPwd(MD5Util.GetMD5Code(user.getPwd()));
        }
        return oldUser.update();
    }

    @Override
    public boolean login(String username, String pwd, HttpServletResponse resp) {
        Data rightStatus=Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key` = ? AND `value` = ?","userStatus","正常");
        User user=User.dao.findFirst("SELECT * FROM `zc_user` WHERE `username`= ? AND `pwd`=? AND `userStatus`=?",username,MD5Util.GetMD5Code(pwd),rightStatus.getValueId());
       if (user==null){
           return false;
       }
       //写入cookie登录与否的信息 token令牌随机字符Map<String,User>
        String token =Util.getToken();
        UserController.tokenMap.put(token,user);
        Util.addCookie(resp,PropKit.get("cookieToken"),token,7);
        return true;
    }
}
