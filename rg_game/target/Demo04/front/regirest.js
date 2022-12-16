var form;
$(document).ready(function () {

    form = layui.form;
    $("#headImg").click(function () {
        $("[name='headImg']").click();
    })
    $("[name='headImg']").change(function () {
        var f = this.files[0];
        var fr = new FileReader();
        fr.readAsDataURL(f);
        fr.onload = function () {
            $("#headImg").attr("src", this.result);
        }
    })
    form.on("submit(add)",function (obj){
        var load=layer.load(2);
        var fd=new FormData(obj.form);
        $.ajax({
            url:config.url+'/back/user/add',
            data:fd,
            processData:false,
            contentType:false,
            type:'post',
            success:function (res){
                if (res.state=='ok'){
                    layer.msg(res.msg,{
                        time:1500
                    },function (){
                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index); //再执行关闭
                        parent.table.reload("list");
                    });
                }else if (res.state=='fail'){
                    layer.msg(res.msg);
                    layer.close(load);
                }
            }
        })
        return false;
    })
    var load=layer.load(2);
    // 数据加载
    $.ajax({
        url:config.url+"/data/getDataByKey",
        data: {
            key: "userStatus"
        },
        success: function (res) {
            if (res.state == "ok") {
                var d=res.data;
                $("#userStatus").empty();
                $.each(d,function (index,item){
                    $("#userStatus").append("<input type='radio' name='userStatus' value='"+item.valueId+"' title='"+item.value+"'/>")
                })
                $("[name='userStatus']:first").prop("checked",true);
                form.render("radio");
                layer.close(load);
            } else if (res.state == "fail") {
                layer.msg(res.msg);
            }
        }
    })
    form.verify({
        username: function (value) {
            if (value.length > 12 || value.length < 4) {
                return "用户名不能大于12位，小于4位"
            }
            // 验证用户名不重复
            var flag=false;
            var msg="";
            $.ajax({
                url:config.url+"/back/user/checkUsername",
                async:false,//同步
                data: {
                    username:value
                },
                success:function (res){
                    if (res.state == "ok") {

                    } else if (res.state == "fail") {
                        flag=true;
                        msg=res.msg;
                    }
                }
            })
            if (flag){
                return msg;
            }
        },
        pwd: function (value) {
            if (value.length > 12 || value.length < 4) {
                return "密码不能大于12位，小于4位"
            }
            var pwd = $("[name='pwd']").val();
            var pwdAgain = $("[name='pwdAgain']").val();
            if (pwd != pwdAgain) {
                return "密码输入不一致"
            }
        }
    })
})