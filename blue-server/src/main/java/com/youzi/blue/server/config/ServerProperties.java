package com.youzi.blue.server.config;

import com.youzi.blue.common.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ServerProperties {

    private static ServerProperties instance=null;
    private static Config config = null;

    @Getter private String bind; //当前服务器ip或域名
    @Getter private int port;    //工作端口
    @Getter private boolean sslEnable;   //是否启用ssl
    @Getter private String sslJksPath;   //证书路径
    @Getter private String sslKeyStorePassword;  //ssl密码
    @Getter private String sslKeyManagerPassword;
    @Getter private boolean sslNeedsClientAuth;

    public static ServerProperties getInstance() {
        if (config == null) {
            synchronized (Config.class) {
                if (config == null) {
                    config = Config.getInstance();
                }
            }
        }
        if (instance == null) {
            synchronized (ServerProperties.class) {
                if (instance == null) {
                    instance = new ServerProperties();
                    instance.bind = config.getStringValue("server.bind","0.0.0.0");
                    instance.port = config.getIntValue("server.port");
                    instance.sslEnable = config.getBooleanValue("server.ssl.enable");
                    instance.sslJksPath = config.getStringValue("server.ssl.jksPath");
                    instance.sslKeyStorePassword = config.getStringValue("server.ssl.keyStorePassword");
                    instance.sslKeyManagerPassword = config.getStringValue("server.ssl.keyManagerPassword");
                    instance.sslNeedsClientAuth = config.getBooleanValue("server.ssl.needsClientAuth",false);

                }
            }
        }

        return instance;
    }
}
