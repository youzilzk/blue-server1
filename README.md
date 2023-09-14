基于netty的网络隧道, 端口映射

拉取代码

```
https://gitee.com/youzilzk/blue-server.git
```

 **服务端** 

1.修改blue-server的config.properties

2.在blue-server的config_client.json中添加客户端信息

3.在blue-server的config_blue.json中添加隧道信息 

务必根据模板配置

1).支持客户端端口p1映射到服务器端口p2 

2).支持客户端A端口p1映射到客户端B端口p2

4.打包blue-server项目, 运行在服务器上

 **客户端** 

1.修改blue-client的config.properties

2.打包blue-client项目, 运行在客户电脑上


交流qq群: 7048217441

![输入图片说明](blue-server/src/main/resources/static/assets/qrcode_1681568765803.jpg)