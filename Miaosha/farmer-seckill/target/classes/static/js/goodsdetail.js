var queryParam = {}
$(function(){
    var search = location.search;
    var keyValues = search.replace("?","").split("&");
    for (var i = 0; i < keyValues.length; i++) {
        var strings = keyValues[i].split("=");
        if(strings.length === 2){
            queryParam[strings[0]] = strings[1]
        }
    }

    $.ajax({
        url:'/seckill/goodsDetail',
        data:JSON.stringify({reqTime:111,sign:'111',data:{id:queryParam.id}}),
        type:'post',
        contentType: 'application/json',
        dataType:'json',
    }).success(function(data){
        if(data.respCode != "0000"){
            $('span').remove()
            showMsg(data.respMsg)
        }
        $('dt').text(data.data.goodsName)
        if(data.data.goodsCount <= 0){
            showMsg("秒杀商品已经抢完")
            $('span').remove()
            return
        }
        if(data.data.startTimeLong <= 0){
            $('button').css({background:'red'})
            $('span').remove()
            canBuy = true
        }else{
            $('#time').text(calcuTimeStr(data.data.startTimeLong))
            updateTime(data.data.startTimeLong)
        }
    }).error(function(){

    });

})

var canBuy = false;

function doBuy(){
    if(!canBuy){
        return
    }
    canBuy = false;
    $('button').css({background:'grey'})
    $.ajax({
        url:'/seckill/goodsOrderUrl',
        data:JSON.stringify({reqTime:111,sign:'111',data:{id:queryParam.id}}),
        type:'post',
        contentType: 'application/json',
        dataType:'json',
    }).success(function(data){
        if(data.respCode === "0000"){
            var url = data.data
            doOrder(queryParam.id,url)
        }else{
            showMsg(data.respMsg)
        }
    }).error(function(){

    });
}

function doOrder(id, url) {
    $.ajax({
        url:url,
        data:JSON.stringify({reqTime:111,sign:'111',data:{id:id}}),
        type:'post',
        contentType: 'application/json',
        dataType:'json',
    }).success(function(data){
        if(data.respCode === "0000"){
            showMsg("秒杀成功，立即支付?",function(result){
                if(result){
                    payCallBack(data.data);
                }
            })
        }else{
            showMsg(data.respMsg)
        }
    }).error(function(){

    });
}


function payCallBack(data){
    $.ajax({
        url:'/seckill/order/payCallback',
        data:JSON.stringify({reqTime:111,sign:'111',data:{orderNo:data.orderNo,paySeq:data.paySeq,payStatus:'02'}}),
        type:'post',
        contentType: 'application/json',
        dataType:'json',
    }).success(function(data){
        if(data.respCode === "0000"){
            showMsg("订单支付成功，等待发货...")
        }
    }).error(function(){

    });
}

var addTime = 0;
function updateTime(time){
    setTimeout(function(){
        addTime += 1000;
        if(time - addTime <= 0){
            $('button').css({background:'red'})
            $('span').remove()
            canBuy = true
        }else{
            $('#time').text(calcuTimeStr(time - addTime))
            updateTime(time)
        }
    },1000)
}

function calcuTimeStr(time){
    var startTimeLong = parseInt(time/1000,0);
    var sec = parseInt(startTimeLong % 60,0)
    var min = parseInt(startTimeLong/60 % 60,0)
    var hour = parseInt(startTimeLong/60/60 % 60,0)
    var day = parseInt(startTimeLong/60/60/60 % 24,0)
    return day + "天 " + (hour >= 10 ? ""+ hour:"0" + hour) + ":" + (min >= 10 ? ""+ min:"0" + min) + ":" + (sec >= 10 ? ""+ sec:"0" + sec);
}

var callBackFun;
function msgClick(result){
    if(callBackFun){
        callBackFun(result);
    }
    $(".msg-cover").hide()
}

function showMsg(msg,callBack){
    callBackFun = callBack
    $(".msg-msg").text(msg)
    $(".msg-cover").show()
}