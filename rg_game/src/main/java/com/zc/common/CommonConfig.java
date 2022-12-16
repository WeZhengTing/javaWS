package com.zc.common;

import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.zc.controller.front.RoomConteoller;
import com.zc.model.Room;
import com.zc.model._MappingKit;

public class CommonConfig extends JFinalConfig {
    public  static Prop p;
    public static DruidPlugin createDruidPlugin() {
        loadConfig();

        return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
    }

    private static void loadConfig() {
        if (p == null) {
            p = PropKit.useFirstFound("config.txt");
        }
    }

    @Override
    public void configConstant(Constants constants) {
        loadConfig();
        constants.setDevMode(true);
        constants.setEncoding("UTF8");
        constants.setMaxPostSize(1024*1024*1024);
        constants.setBaseUploadPath(p.get("baseUploadPath"));
        Room romm1 =new Room();
        romm1.setId(System.currentTimeMillis()+"");
        romm1.setMaxCount(5);
        romm1.setRoomName("房间1");
        romm1.setRoomStatus(1);
        RoomConteoller.rooms.add(romm1);

        Room romm2 =new Room();
        romm2.setId(System.currentTimeMillis()+"");
        romm2.setMaxCount(5);
        romm2.setRoomName("房间2");
        romm2.setRoomStatus(1);
        RoomConteoller.rooms.add(romm2);

        Room romm3 =new Room();
        romm3.setId(System.currentTimeMillis()+"");
        romm3.setMaxCount(5);
        romm3.setRoomName("房间3");
        romm3.setRoomStatus(2);
        RoomConteoller.rooms.add(romm3);

        Room romm4 =new Room();
        romm4.setId(System.currentTimeMillis()+"");
        romm4.setMaxCount(5);
        romm4.setRoomName("房间4");
        romm4.setRoomStatus(1);
        RoomConteoller.rooms.add(romm4);

        Room romm5 =new Room();
        romm5.setId(System.currentTimeMillis()+"");
        romm5.setMaxCount(5);
        romm5.setRoomName("房间5");
        romm5.setRoomStatus(1);
        RoomConteoller.rooms.add(romm5);

        Room romm6 =new Room();
        romm6.setId(System.currentTimeMillis()+"");
        romm6.setMaxCount(5);
        romm6.setRoomName("房间6");
        romm6.setRoomStatus(1);
        RoomConteoller.rooms.add(romm6);

    }

    @Override
    public void configRoute(Routes routes) {
                routes.scan("com.zc.controller");
    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
        // 配置 druid 数据库连接池插件
        DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
        plugins.add(druidPlugin);

        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);
        plugins.add(arp);
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {

    }

    @Override
    public void configHandler(Handlers handlers) {

        handlers.add(new WebSocketHandler("^/hallWs"));
        handlers.add(new WebSocketHandler("^/gameWs"));
    }
}
