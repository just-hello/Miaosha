# 基于SpringBoot的秒杀系统

#### 介绍
Java开发的秒杀系统，包含了秒杀开始前隐藏秒杀链接、页面倒计时取服务器时间、限流等。
#### 开发环境
SpringBoot+MySQL+Redis+MyBatis+Druid
#### 安装说明
1. 执行DDL.sql脚本，创建数据库
2. 将farmer-seckill作为项目名
3. 在application.yaml中修改mysql的连接配置改为自己的设置
4. 在application.yaml中修改Redis的连接配置改为自己的设置
5. 启动项目
6. [单击以添加秒杀活动](http://localhost:8082/goods/listPage) ， 注意添加活动时，活动的开始时间不能是过去的时间
7. [单击参与秒杀活动](http://localhost:8082/static/goodslist.html)




