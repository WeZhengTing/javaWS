package com.zc.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.zc.model.Player;
import com.zc.model.Room;
import com.zc.service.RoomService;
import com.zc.service.impl.RoomServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/front/room")
public class RoomConteoller extends FrontBaseController{
    public static List<Room> rooms =new ArrayList<Room>();
    public  static Map<Integer,Room> playerRoom=new HashMap<Integer,Room>();
    public  static  Map<Integer, Player> playerMap=new HashMap<Integer,Player>();
    RoomService rs =new RoomServiceImpl();
    public void  getRoomInfo(){
        renderJson(rs.getRoomInfo(getRequest()));
    }
    public void ready(){
        renderJson(rs.ready(getRequest()));
    }
    public void  add(){
        Room room=getBean(Room.class,"");
        renderJson(rs.createRoom(room,getRequest()));
    }
    public  void  list(){
        int page=getParaToInt("page");
        int limit=getParaToInt("limit");
        String roomName=getPara("roomName");
        String roomStatus=getPara("roomStatus");
        List<Room> list= rs.getRooms(page,limit,roomName,roomStatus);
        long count=rs.getRoomCount(roomName,roomStatus);
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("code",0);
        jsonObject.put("msg","");
        jsonObject.put("data",list);
        jsonObject.put("count",count);
        renderJson(jsonObject);
    }
    public  void outRoom(){
        renderJson(rs.outRoom(getRequest()));
    }
    public void enterRoom(){
        String roomId=getPara("roomId");
        String pwd=getPara("pwd");
        renderJson(rs.enterRoom(roomId,pwd,getRequest()));
    }
}
