package com.farmer.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.farmer.seckill.constants.TotalConstants;
import com.farmer.seckill.entity.Goods;
import com.farmer.seckill.inout.CommonJsonResponse;
import com.farmer.seckill.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("/goods")
@RequiredArgsConstructor
@Slf4j
public class PageController {

    private final GoodsService goodsService;

    private final RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/listPage")
    public String listPage(){
        return "list";
    }

    @PostMapping("/listData")
    @ResponseBody
    public Object listData(Integer page,Integer rows){
        return goodsService.list4Page(page,rows);
    }

    @GetMapping("addPage")
    public String addPage(){
        return "add";
    }

    @PostMapping("add")
    @ResponseBody
    public CommonJsonResponse add(Goods goods, HttpSession session){
        if(goods.getGoodsName() == null){
            return new CommonJsonResponse("9999","商品名称不能为空");
        }
        if(goods.getGoodsPrice() == null || goods.getGoodsPrice().compareTo(BigDecimal.ZERO) <= 0 ){
            return new CommonJsonResponse("9999","商品价格不合法");
        }
        if(goods.getGoodsCount() == null || goods.getGoodsCount() <= 0){
            return new CommonJsonResponse("9999","商品数量不合法");
        }
        if(goods.getStartTime() == null || goods.getStartTime().before(new Date())){
            return new CommonJsonResponse("9999","开始时间不合法");
        }
        if(goods.getEndTime() == null || goods.getEndTime().before(new Date())){
            return new CommonJsonResponse("9999","结束时间不合法");
        }

        if(goods.getStartTime().after(goods.getEndTime())){
            return new CommonJsonResponse("9999","开始时时间不能再结束时间之后");
        }
        final Integer i = goodsService.addGoods(goods, session);
        if(i > 0){
            JSONObject goodsJson = (JSONObject)JSONObject.toJSON(goods);
            redisTemplate.opsForHash().putAll(TotalConstants.SECKILL_GOODS_DETAIL_PREFIX + goods.getId(), goodsJson.getInnerMap());
            return CommonJsonResponse.ok();
        }
        return new CommonJsonResponse("9999","添加商品失败");
    }


    @InitBinder
    public void InitBinder(WebDataBinder binder) {
        //前端传入的时间格式必须是"yyyy-MM-dd"效果!
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomDateEditor dateEditor = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }


}
