//悔棋功能-->1.当前下棋人 换回上一个 2.上一步棋 需要撤销 删除
//1.记录图像 悔棋时 覆盖图像[图像内存损耗较大]
//2.记录位置 页面根据现有数值进行数据填充 [执行损耗较大]
var historyArr = [];//历史记录数组
var c = document.getElementById("game");
var span = 41.25; //两条线之间的间隔
var line = 16;//几条线16条线 15个格子
c.width = span * line;
c.height = span * line;
var ctx = c.getContext("2d");
var isBlack = true;//默认黑色
var maxTime = 15;//不动
var time = maxTime;//动
var arr=[];
var flag=false;//锁
// init();

function init() {
    flag=false;//初始化
    historyArr = [];//历史记录数组
    c = document.getElementById("game");
    // span = 45; //两条线之间的间隔
    // line = 16;//几条线16条线 15个格子
    // c.width = span * line;
    // c.height = span * line;
    ctx = c.getContext("2d");
    isBlack = true;//默认黑色
    document.getElementById("color").innerText = isBlack ? "黑子" : "白子";
    time = maxTime;//动
    // document.getElementById("time").innerText = time;
    drawCheckerBoard();
    // maxTime = 15;//不动
    arr = new Array(line);
    for (var i = 0; i < arr.length; i++) {
        arr[i] = new Array(line);
    }

}

// document.getElementById("back").onclick = function () {
//     if (historyArr.length > 0) {
//         var back = historyArr.pop();
//         isBlack = !isBlack;
//         arr[back.y][back.x] = undefined;//上一个步骤清理掉
//         time = maxTime;
//         document.getElementById("color").innerText = isBlack ? "黑子" : "白子";
//         document.getElementById("time").innerText = time;
//         //清理图像，重新绘制界面
//         ctx.clearRect(0, 0, c.width, c.height);
//         drawCheckerBoard();
//         for (var i = 0; i < arr.length; i++) {
//             for (var j = 0; j < arr[i].length; j++) {
//                 if (arr[i][j]) {
//                     var color = arr[i][j];
//                     ctx.beginPath();
//                     var x = j * span + span / 2;
//                     var y = i * span + span / 2;
//                     ctx.arc(x, y, span / 2 - 1, 0, 2 * Math.PI);
//                     if (color == 'black') {
//                         ctx.fillStyle = "black";
//                         ctx.fill();
//                     } else {
//                         ctx.strokeStyle = "black";
//                         ctx.fillStyle = "white";
//                         ctx.fill();
//                         ctx.stroke();
//                     }
//                 }
//             }
//         }
//     } else {
//         alert("没有上一步了！");
//     }
// }
// var tD = setInterval(function () {
//     time--;
//
//     if (time <= 0) {
//         //自动下棋
//         var x = 0;
//         var y = 0;
//         //棋盘布满或找到位置
//         var flag = false;
//         for (var i = 0; i < arr.length; i++) {
//             for (var j = 0; j < arr[i].length; j++) {
//                 if (!arr[i][j]) {
//                     flag = true;
//                     break;
//                 }
//             }
//         }
//         if (!flag) {
//             alert("位置全满了！，游戏结束，平局！");
//             clearInterval(tD);
//             return;
//         }
//         do {
//             var x = Math.round(Math.random() * (line - 1));
//             var y = Math.round(Math.random() * (line - 1));
//         } while (arr[y][x]);
//         //下棋
//         play(x, y);
//
//     }
//     document.getElementById("time").innerText = time;
// }, 1000)
function drawCheckerBoard() {
    ctx.beginPath();
    for (var i = 0; i < line; i++) {
        ctx.moveTo(span / 2, span / 2 + span * i);
        ctx.lineTo(span * line - span / 2, span / 2 + span * i);
        ctx.stroke();
        ctx.moveTo(span / 2 + span * i, span / 2,);
        ctx.lineTo(span / 2 + span * i, span * line - span / 2);
        ctx.stroke();
    }
}
c.onclick = function (e) {
    if (flag){
        var x = e.offsetX;
        var y = e.offsetY;
        var iconX = parseInt(x / span);
        var iconY = parseInt(y / span);
        play(iconX, iconY);
        //发送消息给对方
        var obj={
            x:iconX,
            y:iconY,
            method:"down"
        }
        ws.send(JSON.stringify(obj));
        flag=false;//自己不能下棋了
    }
}
function play(iconX, iconY) {
    var x = iconX * span + span / 2;
    var y = iconY * span + span / 2;
    if (arr[iconY][iconX]) {
        alert("此处已有棋子，请换一个位置！");
        return;
    }
    ctx.beginPath();
    ctx.arc(x, y, span / 2 - 1, 0, 2 * Math.PI);
    var color = isBlack ? "black" : "white";
    var win;
    if (isBlack) {
        ctx.fillStyle = "black";
        ctx.fill();
        arr[iconY][iconX] = color;

    } else {
        ctx.strokeStyle = "black";
        ctx.fillStyle = "white";
        ctx.fill();
        ctx.stroke();
        arr[iconY][iconX] = color;

    }
    if (isWin(iconX, iconY, color)) {
        //输出xx胜利
        alert(color == "black" ? "黑子胜利" : "白子胜利");
        //TODO 游戏关闭
    }
    historyArr.push({
        x: iconX,
        y: iconY,
        color: color
    })
    isBlack = !isBlack;
    document.getElementById("color").innerText = isBlack ? "黑子" : "白子";
    time = maxTime;
}
function isWin(x, y, color) {
    var count = 0;
    for (var i = y - 4; i <=y + 4; i++) {
        if (i >= 0 && i < line) {
            if (arr[i][x] == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
    }
    count = 0;
    for (var i = x - 4; i <=x + 4; i++) {
        if (i >= 0 && i < line) {
            if (arr[y][i] == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
    }
    count = 0;
    for (var i = x - 4, j = y - 4; i <=x + 4, j <=y + 4; i++, j++) {
        if (i >= 0 && i < line && j >= 0 && j < line) {
            if (arr[j][i] == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
    }
    count = 0;
    for (var i = x - 4, j = y + 4; i <=x + 4, j >= y - 4; i++, j--) {
        if (i >= 0 && i < line && j >= 0 && j < line) {
            if (arr[j][i] == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
    }
    return false;
}