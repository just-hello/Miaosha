
create database seckill;

use seckill;

CREATE TABLE seckill.`goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `goods_name` varchar(32) NOT NULL COMMENT '商品名称',
  `goods_price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `goods_count` int(11) NOT NULL COMMENT '剩余数量',
  `total_count` int(11) NOT NULL COMMENT '总数量',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_user` varchar(32) NOT NULL COMMENT '创建用户',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user` varchar(32) NOT NULL COMMENT '更新用户',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='秒杀商品表';


CREATE TABLE seckill.`secorder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `goods_name` varchar(32) NOT NULL COMMENT '商品名称',
  `goods_num` int(11) NOT NULL COMMENT '商品数量',
  `amount` decimal(10,2) NOT NULL COMMENT '订单总价',
  `pay_seq` varchar(32) NOT NULL COMMENT '支付流水号',
  `order_status` varchar(2) NOT NULL COMMENT '订单状态',
  `goods_snapshots` text NOT NULL COMMENT '商品快照',
  `user_id` varchar(32) NOT NULL COMMENT '购买用户',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user` varchar(32) NOT NULL COMMENT '更新用户',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='秒杀订单表'