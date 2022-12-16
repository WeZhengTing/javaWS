var table;
var roomStatusList;
var form;
var token;
var ws;
$(document).ready(function () {
    table = layui.table;
    form = layui.form;
    // 获取用户状态的信息
    //获取token cookie
    token = getCookie(config.cookieToken)
    if (!token){
        layer.msg("未登录！",{
            time:1500
        },function () {
            location.href='../../login.html'
        })
    }
    ws = new WebSocket(config.wsUrl+"/hallWs/"+token);
    ws.onopen=function (){
        console.log("连接上了！")
    }
    ws.onmessage=function (e) {
        var res = JSON.parse(e.data)
        if (res.method == 'error'){
            layer.msg(res.msg,{
                time:1500
            },function () {
                location.href='../../login.html'
            })
        }else if (res.method == 'freshTable'){
            table.reload("list")
        }
    }
    $.ajax({
        url: config.url + '/data/getDataByKey',
        async: false,//同步
        data: {
            key: "roomStatus"
        },
        success: function (res) {
            if (res.state == 'ok') {
                roomStatusList = res.data;
                //遍历显示到下拉框
                $("[name='roomStatus']").html("<option value=''>全部</option>")
                $.each(roomStatusList, function (index, item) {
                    $("[name='roomStatus']").append("<option value='" + item.valueId + "'>" + item.value + "</option>");
                });
                form.render("select")
            }
        }
    });
    form.on("submit(search)", function (obj) {
        table.reload("list", {
            where: obj.field,
        })
        return false;
    })
    table.render({
        elem: '#list'
        , url: config.url + '/front/room/list' //数据接口
        , page: true //开启分页
        , limit: 4
        , limits: [4, 20, 30, 100]
        , done: function (res, curr, count) {
            //如果是异步请求数据方式，res即为你接口返回的信息。
            //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
            console.log(res);
            if (res.data.length == 0 && count != 0) {//当前页面是空的，但是总数还存在
                table.reload("list", {
                    page: {
                        curr: curr - 1
                    }
                })
            }
            //得到当前页码
            console.log(curr);

            //得到数据总量
            console.log(count);
        }
        , cols: [[ //表头
            {
                field: 'roomName', title: '房间名', templet: function (d) {
                    if (d.pwd == 'true') {
                        return "<i class='layui-icon layui-icon-password'></i>" + d.roomName;
                    } else {
                        return d.roomName;
                    }
                }
            },
            {
                title: '房间人员', templet: function (d) {
                    return d.players.length + "/" + d.maxCount
                }
            },
            {
                field: 'roomStatus', title: '房间状态', templet: function (d) {
                    var value = "未找到";
                    $.each(roomStatusList, function (index, item) {
                        if (item.valueId == d.roomStatus) {
                            value = item.value;
                            return;
                        }
                    })
                    return value;
                }
            },
            {
                title: "操作", toolbar: "#tool"
            }
        ]]
    })
    table.on("tool(list)", function (obj) {
        if (obj.event == 'enter') {
            // 进入房间是否有密码,有密码输入
            var pwd;
            if (obj.data.pwd == 'true') {//弹出后不卡死界面
                layer.prompt({formType: 1, title: "请输入密码！"}, function (value, index, elem) {
                    pwd = value;
                    var load = layer.load(2);
                    $.ajax({
                        url: config.fUrl + "/front/room/enterRoom",
                        data: {
                            roomId: obj.data.id,
                            pwd: pwd
                        },
                        success: function (res) {
                            if (res.state == 'ok') {
                                layer.msg(res.msg, {
                                    time: 5000
                                }, function () {
                                    location.href = 'room.html';//进入房间
                                });
                            } else if (res.state == 'fail') {
                                layer.msg(res.msg);
                                layer.close(load);
                            }
                        }
                    })
                });
            } else {
                var load = layer.load(2);
                $.ajax({
                    url: config.fUrl + "/front/room/enterRoom",
                    data: {
                        roomId: obj.data.id
                    },
                    success: function (res) {
                        if (res.state == 'ok') {
                            layer.msg(res.msg, {
                                time: 5000
                            }, function () {
                                location.href = 'room.html';//进入房间
                            });
                        } else if (res.state == 'fail') {
                            layer.msg(res.msg);
                            layer.close(load);
                        }
                    }
                })
            }

        }
    })
    $("#add").click(function () {
        layer.open({
            type: 2,
            content: "add.html",
            area: ["500px", "300px"],
            maxmin: true,
            title: "创建房间"
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
                    table.reload("list");
                } else if (res.state = 'fail') {
                    layer.msg(res.msg);
                }
            }
        })
    })
})