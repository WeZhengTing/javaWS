package com.zc.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private String roomName;
    private  String pwd;
    private  Integer maxCount;
    private Player roomOwner;
    private List<Player> players =new ArrayList<Player>();
    public Room(){

    }
    public Room(Room r){
        this.setId(r.getId());
        this.setMaxCount(r.getMaxCount());
        for (Player p:r.getPlayers()
             ) {
            this.players.add(new Player(p));
        }
        this.setPwd(r.getPwd());
        this.setRoomName(r.getRoomName());
        this.setRoomOwner(r.getRoomOwner());
        this.setRoomStatus(r.getRoomStatus());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Player getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(Player roomOwner) {
        this.roomOwner = roomOwner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Integer getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(Integer roomStatus) {
        this.roomStatus = roomStatus;
    }

    private Integer roomStatus;
}
