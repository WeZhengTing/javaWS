package com.zc.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Player extends User{
    private Integer playerStatus;//准备
    private  Integer role;//角色
    private  User user;//用户


    public User getUser() {
        return user;
    }



    public void setUser(User user) {
        this.user = user;
    }

    public  Player(User user){
            this.user=user;
            this.setId(user.getId());
            this.setHeadImg(user.getHeadImg());
            this.setUsername(user.getUsername());
            this.setUserStatus(user.getUserStatus());
            this.setPlayerStatus(0);
    }
    public  Player(Player p){
        this.user=p.getUser();
        this.setId(p.getId());
        this.setHeadImg(p.getHeadImg());
        this.setUsername(p.getUsername());
        this.setUserStatus(p.getUserStatus());
        this.setPlayerStatus(p.getPlayerStatus());
        this.setRole(p.getRole());
    }
    public Integer getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(Integer playerStatus) {
        set("playerStatus",playerStatus);
        this.playerStatus = playerStatus;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        set("role",role);
        this.role = role;
    }

}
