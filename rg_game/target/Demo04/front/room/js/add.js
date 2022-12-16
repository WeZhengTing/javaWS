var form;
$(document).ready(function () {

    form = layui.form;

    form.on("submit(add)", function (obj) {
        var load = layer.load(2);
        var fd = new FormData(obj.form);
        $.ajax({
            url: config.url + '/front/room/add',
            data:obj.field,
            type: 'post',
            success: function (res) {
                if (res.state == 'ok') {
                    layer.msg(res.msg, {
                        time: 1500
                    }, function () {
                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index); //再执行关闭
                        parent.window.location.href="room.html";
                        parent.table.reload("list");
                    });
                } else if (res.state == 'fail') {
                    layer.msg(res.msg);
                    layer.close(load);
                }
            }
        })
        return false;
    })
})

