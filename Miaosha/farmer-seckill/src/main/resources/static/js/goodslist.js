$(function(){
    var $listDiv = $('#list');
    var str = "<dl>\n" +
        "            <dt>{name}</dt>\n" +
        "            <dd>\n" +
        "                <span >倒计时:</span>\n" +
        "                <span id='time-{id}' class=\"time-cont\">{time}</span>\n" +
        "                <button onclick='jumpTo({jid})' style='background: {backColor}'>{buttonText}</button>\n" +
        "            </dd>\n" +
        "        </dl>"
    $.ajax({
        url:'/seckill/listGoods',
        type:'post',
        dataType:'json',
    }).success(function(data){
        if(data.respCode === "0000"){
            var list = data.data.list;
            for (var i = 0; i < list.length; i++) {
                var startTimeLong = list[i].startTimeLong;
                var id = list[i].id
                var html = $(str.replace("{id}",id)
                    .replace("{jid}",list[i].goodsCount >0?id:0)
                    .replace("{name}",list[i].goodsName)
                    .replace("{time}",calcuTimeStr(startTimeLong))
                    .replace("{buttonText}",list[i].goodsCount >0?"去抢购":"已抢完")
                    .replace("{backColor}",list[i].goodsCount >0?"red":"grey")
                );

                $listDiv.append(html)
            }
            updateTime(list)
        }
    }).error(function(){

    });
})


var addtime = 0;
function updateTime(list){
    addtime += 1000
    setTimeout(function(){
        for (var i = 0; i < list.length; i++) {
            $("#time-" + list[i].id).text(calcuTimeStr(list[i].startTimeLong - addtime))
        }
        updateTime(list)
    },1000)
}

function calcuTimeStr(time){
    if(time <= 0){
       return "00:00:00"
    }
    var startTimeLong = parseInt(time/1000,0);
    var sec = parseInt(startTimeLong % 60,0)
    var min = parseInt(startTimeLong/60 % 60,0)
    var hour = parseInt(startTimeLong/60/60 % 60,0)
    var day = parseInt(startTimeLong/60/60/60 % 24,0)
    return day + "天 " + (hour >= 10 ? ""+ hour:"0" + hour) + ":" + (min >= 10 ? ""+ min:"0" + min) + ":" + (sec >= 10 ? ""+ sec:"0" + sec);
}

function jumpTo(id){
    if(id === 0){
        return ;
    }
    window.location = "goodsdetail.html?id=" + id;
}