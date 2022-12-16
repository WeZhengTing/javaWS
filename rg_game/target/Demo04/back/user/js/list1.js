var table;
var userStatusList;
var form;
$(document).ready(function (){
    table=layui.table;
    form=layui.form;
    // 获取用户状态的信息
    $.ajax({
        url:config.url+'/data/getDataByKey',
        async:false,//同步
        data:{
            key:"userStatus"
        },
        success:function (res){
            if (res.state=='ok'){
                userStatusList=res.data;
                //遍历显示到下拉框
                $("[name='userStatus']").html("<option value=''>全部</option>")
                $.each(userStatusList,function (index,item){
                    $("[name='userStatus']").append("<option value='"+item.valueId+"'>"+item.value+"</option>");
                });
                form.render("select")
            }
        }
    });
    form.on("submit(search)",function (obj){
        table.reload("list",{
            where:obj.field,
        })
        return false;
    })
    table.render({
        elem: '#list'
        ,url: config.url+'/back/user/list' //数据接口
        ,page: true //开启分页
        ,limit:5
        ,limits:[5,10,15,20,100]
        ,done: function(res, curr, count){
            //如果是异步请求数据方式，res即为你接口返回的信息。
            //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
            console.log(res);
            if (res.data.length==0 && count!=0){//当前页面是空的，但是总数还存在
                table.reload("list",{
                    page: {
                        curr:curr-1
                    }
                })
            }
            //得到当前页码
            console.log(curr);

            //得到数据总量
            console.log(count);
            }
        ,cols: [[ //表头
            {field: 'username', title: '用户名'},
            {field: 'userStatus', title: '用户状态',templet:function(d){
                var value="未找到";
                    $.each(userStatusList,function (index,item){
                        if (item.valueId==d.userStatus){
                            value=item.value;
                            return;
                        }
                    })
                    return value;
                }},
            {field: 'headImg', title: '头像',templet:function (d){
                return "<img src='"+d.headImg+"'>";
                }},
            {field: 'createTime', title: '创建时间'},
            {field: 'loginTime', title: '上传登入时间'},
            {
                title:"操作",toolbar:"#tool"
            }
        ]]
    })
    table.on("tool()",function (obj){
        if (obj.event=="del"){
            var id=obj.data.id;//当前删除的id
            layer.confirm('是否删除用户['+obj.data.username+"]", {
                btn: ['是','否'] //按钮
            }, function() {
                var load=layer.load(2);
                $.ajax({
                    url:config.url+"/back/user/del",
                    data: {
                        id:id
                    },
                    success:function (res){
                        layer.close(load);
                        if (res.state=='ok'){
                            layer.msg(res.msg);
                            table.reload("list");
                        }else if(res.state=="fail"){
                            layer.msg(res.msg);
                            //
                        }
                    }
                })
            }
           );
        }else if (obj.event=='edit'){
            layer.open({
                type:2,
                content:"edit.html?id="+obj.data.id,
                area:["500px","600px"],
                maxmin:true,
                title:"编辑用户"
            })
        }
    })
    $("#add").click(function (){
        layer.open({
            type:2,
            content:"add.html",
            area:["500px","600px"],
            maxmin:true,
            title:"添加用户"
        })
    })
})