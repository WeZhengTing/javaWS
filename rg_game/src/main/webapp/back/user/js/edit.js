var form;
var id;
var isChange=false;
$(document).ready(function () {
   form=layui.form;
   id=getQueryVariable("id");
   if (!id){
      layer.msg("id不存在！",{
         time:1500
      },function (){
         var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
         parent.layer.close(index); //再执行关闭
      })
      return;
   }
   //数据库层获得当前id所对应的用户信息
   var load=layer.load(2);
   // 数据加载
   $.ajax({
      url:config.url+"/data/getDataByKey",
      data: {
         key: "userStatus"
      },
      async:false,//同步
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
   var load=layer.load(2);
   $.ajax({
      url: config.url+"/back/user/show",
      data: {
         id:id
      },
      success:function (res){
         if (res.state=='ok'){
            layer.close(load);
            var d=res.data;
            $("[name='username']").val(d.username);
            $("#headImg").attr("src",d.headImg);
            $("[name='userStatus'][value='"+d.userStatus+"']").prop("checked",true);
            form.render("radio");
         }else if (res.state=='fail'){
            layer.msg(res.msg,{
               time:1500
            },function (){
               var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
               parent.layer.close(index); //再执行关闭
            })
         }
      }
   })
   form.on("submit(edit)",function (obj){
      var load=layer.load(2);
      var fd=new FormData(obj.form);
      fd.append("id",id);
      fd.append("isChange",isChange);
      $.ajax({
         url:config.url+'/back/user/edit',
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
   form.verify({
      pwd:function (value){
         if (isChange){
            if (value.length>12||value.length<4){
               return"密码不得大于12位，小于4位!";
            }
         }

      }
   })
   form.on("switch(isChange)",function (obj){
      isChange=obj.elem.checked;
      if (obj.elem.checked){
         $("[name='pwd']").prop("disabled",false);
         $("[name='pwd']").removeClass("layui-disabled");
      }else {
         $("[name='pwd']").prop("disabled",true);
         $("[name='pwd']").addClass("layui-disabled");
      }
   })


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
})