function getVerifyCode(){
    var load=layer.load(2);
    $.ajax({
        url:config.fUrl+"/front/user/getVerifyCode",
        success:function (res){
            layer.close(load);
            if (res.state="ok"){
                $("#verifyCode").attr("src",res.data);

            }
        }
    })
}

var form;
$(document).ready(function (){
    getVerifyCode();
    form=layui.form;
    form.on("submit(login)",function (obj){
        var load=layer.load(2);
        $.ajax({
            url: config.fUrl+"/front/user/login",
            data:obj.field,
            success:function (res) {
                if (res.state=='ok'){
                    layer.msg(res.msg,{
                        time:1500
                    },function (){
                        location.href="room/html/list.html"
                    })
                }else if (res.state=="fail"){
                    layer.msg(res.msg);
                    layer.close(load);
                }

            }
        });
        return false;
    })
    $('.regirest').click(function (){
        layer.open({
            type:2,
            content:"regirest.html",
            area:["500px","600px"],
            maxmin:true,
            title:"注册用户"
        })
    })
})