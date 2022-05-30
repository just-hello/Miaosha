package com.farmer.seckill.controller;

import com.alibaba.fastjson.JSONObject;
import com.farmer.seckill.constants.TotalConstants;
import com.farmer.seckill.controller.limit.FarmerLimiter;
import com.farmer.seckill.entity.SecOrder;
import com.farmer.seckill.inout.CommonJsonRequest;
import com.farmer.seckill.inout.CommonJsonResponse;
import com.farmer.seckill.inout.in.ReqGoodsDetailVO;
import com.farmer.seckill.inout.in.ReqPayCallbackVO;
import com.farmer.seckill.inout.out.SeckillGoodsListVO;
import com.farmer.seckill.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
@Slf4j
public class SeckillController {

    private final OrderService orderService;

    private final RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/listGoods")
    public CommonJsonResponse<Object> listGoods(){
        final List<Object> range = redisTemplate.opsForList().range(TotalConstants.SECKILL_GOODS_LIST_PREFIX, 0, -1);
        range.forEach(item->{
            JSONObject obj = (JSONObject) item;
            obj.put("startTimeLong",obj.getLong("startTime")-System.currentTimeMillis());
        });
        return CommonJsonResponse.ok(SeckillGoodsListVO.builder().list(new ArrayList<>(range)).build());
    }

    @PostMapping("/goodsDetail")
    public CommonJsonResponse<Object> goodsDetail(@Validated @RequestBody CommonJsonRequest<ReqGoodsDetailVO> request){
        final String redisKey = TotalConstants.SECKILL_GOODS_DETAIL_PREFIX + request.getData().getId();
        if(!redisTemplate.hasKey(redisKey)){
            return new CommonJsonResponse("9999","秒杀活动已经结束");
        }
        final Map<Object, Object> map = redisTemplate.opsForHash().entries(redisKey);
        final Long startTime = (Long) map.get("startTime");
        map.put("startTimeLong",startTime - System.currentTimeMillis());
        return CommonJsonResponse.ok(map);
    }

    @PostMapping("/goodsOrderUrl")
    public CommonJsonResponse goodsOrderUrl(@Validated @RequestBody CommonJsonRequest<ReqGoodsDetailVO> request){
        final Long id = request.getData().getId();
        final String redisKey = TotalConstants.SECKILL_GOODS_DETAIL_PREFIX + id;
        if(!redisTemplate.hasKey(redisKey)){
            return new CommonJsonResponse("9999","秒杀活动已经结束");
        }
        final Long startTime = (Long) redisTemplate.opsForHash().get(redisKey, "startTime");
        if(startTime > System.currentTimeMillis()){
            return new CommonJsonResponse("9999","秒杀活动未开始");
        }
        final String md5 = DigestUtils.md5DigestAsHex((id + TotalConstants.SECKILL_MD5_SALT).getBytes());
        return CommonJsonResponse.ok("/seckill/" + md5 + "/order");
    }


    @PostMapping("/{md5}/order")
    @FarmerLimiter(50)
    public CommonJsonResponse getBuyUrl(@PathVariable("md5") String md5,@Validated @RequestBody CommonJsonRequest<ReqGoodsDetailVO> request, HttpSession session){
        final Long id = request.getData().getId();
        final String newMd5 = DigestUtils.md5DigestAsHex((id + TotalConstants.SECKILL_MD5_SALT).getBytes());
        if (!newMd5.equals(md5)) {
            return  new CommonJsonResponse("9999","请求不合法");
        }
        final String redisKey = TotalConstants.SECKILL_GOODS_DETAIL_PREFIX + id;
        final Long endTime = (Long) redisTemplate.opsForHash().get(redisKey, "endTime");
        if(!redisTemplate.hasKey(redisKey) || endTime < System.currentTimeMillis()){
            return new CommonJsonResponse("9999","秒杀活动已经结束");
        }
        final String limitKey = TotalConstants.SECKILL_GOODS_LIMIT_PREFIX + id + ":" + session.getId();
        if(redisTemplate.hasKey(limitKey)){
            return new CommonJsonResponse("9999","您已经达到最大购买次数");
        }
        if(redisTemplate.opsForHash().increment(redisKey, "goodsCount", -1L) < 0){
            return new CommonJsonResponse("9999","秒杀商品已经抢完");
        }
        final SecOrder order = orderService.generateSecOrder(id, session);
        redisTemplate.opsForValue().set(limitKey,1,(endTime - System.currentTimeMillis())/1000L + (long) new Random().nextInt(3600), TimeUnit.SECONDS);
        return CommonJsonResponse.ok(order);
    }


    @PostMapping("order/payCallback")
    public CommonJsonResponse payCallBack(@Validated @RequestBody CommonJsonRequest<ReqPayCallbackVO> request){
        orderService.payCallback(request.getData());
        return CommonJsonResponse.ok();
    }

}
