package com.zc.service;

import com.zc.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    /**
     * 判断用户名是否重复
     * @param username
     * @return true=重复 false 不重复
     */
    boolean checkUsername(String username);
    boolean insertUser(User user);
    List<User> getUserListByConection(int page, int limit,String username,String userStatus);


    long getUserCountByConection(String username,String userStatus);

    boolean deleteUserById(int id);

    User getUserById(int id);

    boolean editUser(User user, boolean isChange);

    boolean login(String username, String pwd, HttpServletResponse resp);
}
