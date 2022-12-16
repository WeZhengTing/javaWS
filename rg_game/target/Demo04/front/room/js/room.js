var token;
var ws;
var table;
var playerStatusList;
var roleList;
var owner;
var player;
var role;
$(document).ready(function () {
    table = layui.table;
    //获取房间信息
    //房间名，是否有密码，房间列表，每个玩家的角色。。。
    token = getCookie(config.cookieToken)
    if (!token) {
        layer.msg("未登录！", {
            time: 1500
        }, function () {
            location.href = '../../login.html'
        })
    }
    ws = new WebSocket(config.wsUrl + "/gameWs/" + token);
    ws.onopen = function () {
        console.log("连接上了！")
    }
    ws.onmessage = function (e) {
        var res = JSON.parse(e.data)
        if (res.method == 'error') {
            layer.msg(res.msg, {
                time: 1500
            }, function () {
                location.href = '../../login.html'
            })
        } else if (res.method == 'sendChat') {
            $("#chatView").append("<div class='line other'>" + res.username + ":" + res.msg + "</div>")
            $("chatView")[0].scrollTop = $("#chatView")[0].scrollHeight;
        } else if (res.method == 'otherEnterRoom') {
            $("#chatView").append("<div class='notice'>" + res.username + "进入房间,大家欢迎！</div>")
            $("#chatView")[0].scrollTop = $("#chatView")[0].scrollHeight;
            reloadTable();

        } else if (res.method == 'otherOutRoom') {
            $("#chatView").append("<div class='notice'>" + res.username + "退出房间！</div>")
            $("#chatView")[0].scrollTop = $("#chatView")[0].scrollHeight;
            reloadTable();
        } else if (res.method == 'playerStatusChange') {
            reloadTable();
        } else if (res.method == 'startGame') {
            var load = layer.load(2);
            $.ajax({
                url: config.fUrl + '/front/room/getRoomInfo',
                async: false,//同步
                success: function (res) {
                    if (res.state == 'ok') {
                        layer.close(load);
                        var d = res.data.room;//房间信息
                        //当前的直接信息，[用户名，状态]
                       player = res.data.player;//当前玩家信息
                        role=player.role;
                        $.each(roleList,function (index,item){
                            if (item.valueId==player.role){
                                role=item.value;
                                return;
                            }
                        })
                    }
                }
            });
            //房间切换
            $("#myselfColor").text(role);
            $(".playerList").hide();
            $(".gameMain").show();
            init();
            //角色进行获取
            if (role=='黑方'){
                flag=true;//开锁
            }
        }else if (res.method=="down"){
            play(res.x,res.y);
            if (role=='黑方'||role=='白方'){
                flag=true;
            }
        }
    }
    $.ajax({
        url: config.url + '/data/getDataByKey',
        async: false,//同步
        data: {
            key: "playerStatus"
        },
        success: function (res) {
            if (res.state == 'ok') {
                playerStatusList = res.data;

            }
        }
    });
    $.ajax({
        url: config.url + '/data/getDataByKey',
        async: false,//同步
        data: {
            key: "role"
        },
        success: function (res) {
            if (res.state == 'ok') {
                roleList = res.data;

            }
        }
    });
    $("#ready").click(function () {
        //点击了准备按钮
        //玩家+未准备+点击=准备
        //玩家+准备中+点击=取消准备
        //观众+点击=报错
        //房主+点击=开始游戏
        var load = layer.load(2);
        $.ajax({
            url: config.url + "/front/room/ready",
            success: function (res) {
                layer.close(load);
                if (res.state == 'ok') {
                    //自己进行刷新
                    reloadTable();
                } else if (res.state == 'fail') {
                    layer.msg(res.msg);
                }
            }
        })
    })
    $("#outRoom").click(function () {
        var load = layer.load(2);
        $.ajax({
            url: config.fUrl + "/front/room/outRoom",
            success: function (res) {
                layer.close(load);
                if (res.state == 'ok') {
                    layer.msg(res.msg);
                    location.href = 'list.html'
                } else if (res.state = 'fail') {
                    layer.msg(res.msg);
                }
            }
        })
    })
    $("#inp").keypress(function (e) {
        if (e.keyCode == 13) {//按了回车键后
            var obj = new Object();
            obj.method = "sendChat";
            obj.msg = this.value;
            ws.send(JSON.stringify(obj));
            $("#chatView").append("<div class='line myself'>" + (this.value) + ":我</div>")
            $("#chatView")[0].scrollTop = $("#chatView")[0].scrollHeight;
            this.value = "";
        }
    })
    var load = layer.load(2);
    $.ajax({
        url: config.fUrl + '/front/room/getRoomInfo',
        async: false,//同步
        success: function (res) {
            if (res.state == 'ok') {
                layer.close(load);
                var d = res.data.room;//房间信息
                //当前的直接信息，[用户名，状态]
                player = res.data.player;//当前玩家信息
                var status;
                $.each(playerStatusList, function (index, item) {
                    if (item.valueId == player.playerStatus) {
                        status = item.value;
                        return;
                    }
                })

                var role;
                $.each(roleList, function (index, item) {
                    if (item.valueId == player.role) {
                        role = item.value;
                        return;
                    }
                })
                if (role == '观众') {
                    $("#ready").hide();
                }
                //当前状态
                if (status == "未准备") {
                    //显示未准备的按钮
                    $("#ready").text("准备")
                } else if (status == "已准备") {
                    $("#ready").text("取消准备")
                }
                owner = d.roomOwner;
                if (owner.id == player.id) {
                    $("#ready").text("开始")
                }
                var roomName = d.roomName;
                $("#roomName").html(roomName);
                if (d.pwd) {
                    $("#hasPwd").show();
                } else {
                    $("#hasPwd").hide();
                }
                //房间内的房间列表功能
                var list = d.players;
                table.render({
                    elem: '#list',
                    data: list
                    , cols: [[ //表头
                        {
                            title: '头像', templet: function (d) {
                                return "<img src='" + d.headImg + "'>";
                            }, align: "center"
                        }
                        , {field: 'username', title: '用户名', align: "center"}
                        , {
                            title: '角色', templet: function (d) {
                                var value = "未找到";
                                $.each(roleList, function (index, item) {
                                    if (item.valueId == d.role) {
                                        value = item.value;
                                        return;
                                    }
                                })
                                return value;
                            }, align: "center"
                        }
                        , {
                            title: '状态', templet: function (d) {
                                //如果是观众，则不显示
                                var role;
                                $.each(roleList, function (index, item) {
                                    if (item.value == "观众") {
                                        role = item;
                                        return;
                                    }
                                })
                                if (d.role == role.valueId) return "";
                                //如果是房主，则直接显示房主
                                if (owner.id == d.id) return "房主";
                                var value = "未找到";
                                $.each(playerStatusList, function (index, item) {
                                    if (item.valueId == d.playerStatus) {
                                        value = item.value;
                                        return;
                                    }
                                })
                                return value;
                            }, align: "center"
                        }
                    ]]
                });
            } else if (res.state == 'fail') {
                layer.msg(layer.msg, {
                    time: 1500
                }, function () {
                    window.close();
                })
            }
        }
    })
})

function reloadTable() {
    var load = layer.load(2);
    $.ajax({
        url: config.fUrl + '/front/room/getRoomInfo',
        async: false,//同步
        success: function (res) {
            if (res.state == 'ok') {
                layer.close(load);
                var d = res.data.room;
                owner = d.roomOwner;
                player = res.data.player;//当前玩家信息
                var status;
                $.each(playerStatusList, function (index, item) {
                    if (item.valueId == player.playerStatus) {
                        status = item.value;
                        return;
                    }
                })

                var role;
                $.each(roleList, function (index, item) {
                    if (item.valueId == player.role) {
                        role = item.value;
                        return;
                    }
                })
                if (role == '观众') {
                    $("#ready").hide();
                } else {
                    $("#ready").show();
                }
                //当前状态
                if (status == "未准备") {
                    //显示未准备的按钮
                    $("#ready").text("准备")
                } else if (status == "已准备") {
                    $("#ready").text("取消准备")
                }
                owner = d.roomOwner;
                if (owner.id == player.id) {
                    $("#ready").text("开始")
                }
                var roomName = d.roomName;
                $("#roomName").html(roomName);
                if (d.pwd) {
                    $("#hasPwd").show();
                } else {
                    $("#hasPwd").hide();
                }
                //房间内的房间列表功能
                var list = d.players;
                table.reload("list", {
                    data: list
                })
            } else if (res.state == 'fail') {
                layer.msg(layer.msg, {
                    time: 1500
                }, function () {
                    window.close();
                })
            }
        }
    })
}