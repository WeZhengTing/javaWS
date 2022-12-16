package com.zc.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.zc.controller.front.RoomConteoller;
import com.zc.controller.front.UserController;
import com.zc.model.Data;
import com.zc.model.Player;
import com.zc.model.Room;
import com.zc.model.User;
import com.zc.service.RoomService;
import com.zc.util.Util;
import com.zc.ws.GameWebSocket;
import com.zc.ws.HallWebSocket;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomServiceImpl implements RoomService {
    static String[] roleArr = {"黑方", "白方"};//用于随机黑白方
    public static Data wzbData = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`=? AND `value`=?", "playerStatus", "未准备");
    public static Data zbzData = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`=? AND `value`=?", "playerStatus", "已准备");
    public static Data blackRole = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`=? AND `value`=?", "role", "黑方");
    public static Data whileRole = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`=? AND `value`=?", "role", "白方");
    public static Data lookerRole = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`=? AND `value`=?", "role", "观众");

    @Override
    public Ret createRoom(Room room, HttpServletRequest req) {
        //当前用户是否已在房间内
        room.setId(System.currentTimeMillis() + "");
        Data normalStatus = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key` =? AND `value` =?", "roomStatus", "等待中");
        if (normalStatus == null) return Ret.fail("状态值出错！");
        room.setRoomStatus(normalStatus.getValueId());
        String token = Util.getCookie(req, PropKit.get("cookieToken"));
        if (token == null) return Ret.fail("账户异常！");
        User user = UserController.tokenMap.get(token);
        if (user == null) return Ret.fail("账户异常！");
        Player owner = new Player(user);
        RoomConteoller.playerMap.put(user.getId(),owner);
        if (RoomConteoller.playerRoom.get(owner.getId()) != null) {
            return Ret.fail("当前玩家已在房间内，请退出房间后再次创建房间！");
        }
        Random random = new Random();
        int r = random.nextInt(2);
        Data roleData = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`= ? AND `value`= ? ", "role", roleArr[r]);
        if (roleData == null) return Ret.fail("数据异常");
        owner.setRole(roleData.getValueId());//设置角色
        room.setRoomOwner(owner);
        room.getPlayers().add(owner);
        RoomConteoller.rooms.add(room);//存入内存之中
        RoomConteoller.playerRoom.put(owner.getId(), room);//用户映射关系放入
        JSONObject msg = new JSONObject();
        msg.put("method", "freshTable");
        HallWebSocket.sendMsg(token, msg.toString());
        return Ret.ok("创建成功!");
    }

    @Override
    public List<Room> getRooms(int page, int limit) {//3个
        //第5页，每页3个，下标为12-14
        List<Room> list = new ArrayList<Room>();
        List<Room> totalList = RoomConteoller.rooms;//10个
        int thisIndex = (totalList.size() - 1) - ((page - 1) * limit);
        for (int i = 0; i < limit; i++) {
            try {
                if (totalList.get(thisIndex - i) != null) {
                    Room r = new Room(totalList.get(thisIndex - i));
                    if (StringUtils.isEmpty(r.getPwd())) {
                        r.setPwd("false");
                    } else {
                        r.setPwd("true");
                    }
                    list.add(r);
                }
            } catch (Exception e) {
            }
        }
        return list;
    }

    @Override
    public Ret enterRoom(String roomId, String pwd, HttpServletRequest req) {

        String token = Util.getCookie(req, PropKit.get("cookieToken"));
        if (token == null) return Ret.fail("账户异常！");
        User user = UserController.tokenMap.get(token);
        if (user == null) return Ret.fail("账户异常！");
        Player player = new Player(user);
        RoomConteoller.playerMap.put(user.getId(),player);
        //获取当前房间对象
        Room thisRoom = null;
        for (Room room : RoomConteoller.rooms
        ) {
            if (room.getId().equals(roomId)) {
                thisRoom = room;
                break;
            }
        }
        if (thisRoom == null) {
            return Ret.fail("房间已不存在！");
        }
        //判断房间游戏状态
        Data platStatus = Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key` =? AND `value` =?", "roomStatus", "游戏中");
        if (platStatus == null) return Ret.fail("状态值出错！");
        //判断密码是否正确
        if (!StringUtils.isEmpty(thisRoom.getPwd())) {
            System.out.println("当前房间密码：" + thisRoom.getPwd() + ",输入的密码：" + pwd);
            if (!thisRoom.getPwd().equals(pwd)) {
                return Ret.fail("密码错误!");
            }
        }
        if (thisRoom.getRoomStatus() == platStatus.getValueId()) return Ret.fail("房间正在游戏中!");
        //判断用户情况
        if (RoomConteoller.playerRoom.get(user.getId()) != null) {
            return Ret.fail("当前玩家已在房间内，请退出房间后再次进入房间！");
        }
        if (thisRoom.getMaxCount() <= thisRoom.getPlayers().size()) {
            return Ret.fail("房间已满！");
        }
        player.setPlayerStatus(wzbData.getValueId());
        //设置角色
        //判断角色是否被占满
        boolean hasBlackRole = false;
        boolean hasWhiteRole = false;
        for (Player p : thisRoom.getPlayers()) {
            if (p.getRole() == whileRole.getValueId()) {
                hasWhiteRole = true;
            }
            if (p.getRole() == blackRole.getValueId()) {
                hasBlackRole = true;
            }
        }
        if (hasWhiteRole && hasBlackRole) {
            //角色已满
            player.setRole(lookerRole.getValueId());
        } else {
            //一个角色false
            if (!hasWhiteRole) {
                player.setRole(whileRole.getValueId());
            } else {
                player.setRole(blackRole.getValueId());
            }
        }

        thisRoom.getPlayers().add(player);//进入房间用户集合
        RoomConteoller.playerRoom.put(player.getId(), thisRoom);//用户房间映射关系
        JSONObject msg = new JSONObject();
        msg.put("method", "freshTable");
        HallWebSocket.sendMsg(token, msg.toString());
        return Ret.ok("成功进入房间！");
    }

    @Override
    public long getRoomCount(String roomName, String roomStatus) {
        List<Room> totalList = RoomConteoller.rooms;
        int count = 0;
        for (Room room : totalList
        ) {
            if (!StringUtils.isEmpty(roomName)) {
                if (room.getRoomName().indexOf(roomName) == -1) {
                    continue;
                }
            }
            if (!StringUtils.isEmpty(roomStatus)) {
                if (!(room.getRoomStatus() + "").equals(roomStatus)) {
                    continue;
                }
            }
            count++;
        }
        return count;
    }

    @Override
    public List<Room> getRooms(int page, int limit, String roomName, String roomStatus) {
        List<Room> list = new ArrayList<Room>();
        List<Room> totalList = RoomConteoller.rooms;//10个
        long outIndex = 0;
        for (int i = totalList.size() - 1; i >= 0; i--) {
            Room room = totalList.get(i);
            if (!StringUtils.isEmpty(roomName)) {
                if (room.getRoomName().indexOf(roomName) == -1) {
                    continue;
                }
            }
            if (!StringUtils.isEmpty(roomStatus)) {
                if (!(room.getRoomStatus() + "").equals(roomStatus)) {
                    continue;
                }
            }
//            outIndex判断其是否满足
            if (outIndex >= (page - 1) * limit) {
                Room r = new Room(room);
                if (StringUtils.isEmpty(r.getPwd())) {
                    r.setPwd("false");
                } else {
                    r.setPwd("true");
                }
                list.add(r);
            }
            outIndex++;
            if (outIndex == page * limit) {
                break;
            }

        }
        return list;
    }

    @Override
    public Ret outRoom(HttpServletRequest request) {
//        1.判断账户情况
//        2.房间情况（当前用户是否再房间内，房间是否存在）
//        3.用户退出房间是否影响游戏进行
//        4.如果用户退出玩房间没人了，销毁房间
//        5.如果用户退出房间并且用户是房间，随机选一个人作为房主
        String token = Util.getCookie(request, PropKit.get("cookieToken"));
        if (token == null) return Ret.fail("账户异常！");
        User user = UserController.tokenMap.get(token);
        if (user == null) return Ret.fail("账户异常！");
        Player player = RoomConteoller.playerMap.get(user.getId());

        if (RoomConteoller.playerRoom.get(user.getId()) == null) {
            return Ret.fail("当前玩家不在房间内！");
        }
        Room thisRoom = RoomConteoller.playerRoom.get(user.getId());
        if (!RoomConteoller.rooms.contains(thisRoom)) {
            return Ret.fail("房间不存在!");
        }
        //        3.用户退出房间是否影响游戏进行
        //判断角色
        if (player.getRole() == RoomServiceImpl.blackRole.getValueId() || player.getRole() == RoomServiceImpl.whileRole.getValueId()) {
            //退出之前把游戏关闭掉
            //发送游戏关闭ws
            int thisRole = player.getRole();
            //继承给房间里其他人
            for (Player p : thisRoom.getPlayers()) {
                System.out.println(p.getUsername()+"角色："+p.getRole());

                if (p.getRole() != RoomServiceImpl.blackRole.getValueId() && p.getRole() != RoomServiceImpl.whileRole.getValueId()){
                    p.setRole(thisRole);//角色继承给其他人
                    break;
                }
            }
        }
        RoomConteoller.playerRoom.remove(player.getId());
        for (Player p : thisRoom.getPlayers()) {
            if (p.getId() == player.getId()) {
                thisRoom.getPlayers().remove(p);
                break;
            }
        }
        RoomConteoller.playerMap.remove(player.getId());
        JSONObject msg = new JSONObject();
        msg.put("method", "freshTable");
        HallWebSocket.sendMsg(token, msg.toString());

        if (thisRoom.getPlayers().size() == 0) {
            RoomConteoller.rooms.remove(thisRoom);
            return Ret.ok("退出房间，房间已关闭");
        }
        if (thisRoom.getRoomOwner().equals(player)) {
            //玩家就是房主的时候
            Random random = new Random();
            Player p = thisRoom.getPlayers().get(random.nextInt(thisRoom.getPlayers().size()));
            thisRoom.setRoomOwner(p);//新房主
        }
        return Ret.ok("退出房间");
    }

    @Override
    public Ret getRoomInfo(HttpServletRequest request) {
        String token = Util.getCookie(request, PropKit.get("cookieToken"));
        if (token == null) return Ret.fail("账户异常！");
        User user = UserController.tokenMap.get(token);
        if (user == null) return Ret.fail("账户异常！");
        if (RoomConteoller.playerRoom.get(user.getId()) == null) {
            return Ret.fail("当前玩家不在房间内！");
        }
        Room thisRoom = RoomConteoller.playerRoom.get(user.getId());
        Player player =RoomConteoller.playerMap.get(user.getId());
        System.out.println(thisRoom.getPlayers().get(0).getHeadImg() + "应该是没有拼接的图片");
        Room r = new Room(thisRoom);
        for (Player p : r.getPlayers()) {
            p.setHeadImg(PropKit.get("urlPath") + p.getHeadImg());
        }
        if (StringUtils.isEmpty(r.getPwd())) {
            r.setPwd(null);
        } else {
            r.setPwd("true");
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("room",r);
        jsonObject.put("player",player);
        return Ret.ok("data", jsonObject);
    }

    @Override
    public Ret ready(HttpServletRequest request) {

        String token = Util.getCookie(request, PropKit.get("cookieToken"));
        if (token == null) return Ret.fail("账户异常！");
        User user = UserController.tokenMap.get(token);
        if (user == null) return Ret.fail("账户异常！");
        Room thisRoom=RoomConteoller.playerRoom.get(user.getId());
        if (thisRoom==null) return  Ret.fail("不在房间内！");
        Player player=RoomConteoller.playerMap.get(user.getId());
        if (player==null) return Ret.fail("账户异常！");
        Data thisRole=Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key`=? AND `valueId`=? ","role",player.getRole());
        if ("观众".equals(thisRole.getValue())){
            //点击+观众
            return  Ret.fail("观众无法点击，是否出现错误？");
        }else if (thisRoom.getRoomOwner().getId()== player.getId()){
            //房主
            //开始按钮点击 房间里文件是否已经准备
            int count=0;
            for (Player p:thisRoom.getPlayers()) {
                if (p.getRole()!=lookerRole.getValueId() && p.getId()!=player.getId()&& p.getPlayerStatus()==zbzData.getValueId()){
                    //不是观众也不是房主且准备中
                    count++;
                }
            }
            if (count==1){
                //有一人装备即可
                //房间里的其他人发送游戏开始指令
                Data startStatus =Data.dao.findFirst("SELECT * FROM `zc_data` WHERE `key` =? AND `value`= ?","roomStatus","游戏中");
                thisRoom.setRoomStatus(startStatus.getValueId());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method","startGame");
                GameWebSocket.sendMsgAll(token,jsonObject.toString());
                return Ret.ok("开始游戏!");
            }else {
                return Ret.fail("房间内的玩家还未准备！");
            }
        }
        else if ("黑方".equals(thisRole.getValue())||"白方".equals(thisRole.getValue())){
            if (player.getPlayerStatus()==zbzData.getValueId()){
                //准备中
                //取消准备
                player.setPlayerStatus(wzbData.getValueId());
            }else if (player.getPlayerStatus()==wzbData.getValueId()){
                //未准备
                player.setPlayerStatus(zbzData.getValueId());
            }
            //发送给其他人有人状态变化了
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("method","playerStatusChange");
            GameWebSocket.sendMsg(token,jsonObject.toString());
            return Ret.ok("");
        }
        return Ret.fail("未知异常！");
    }
}
