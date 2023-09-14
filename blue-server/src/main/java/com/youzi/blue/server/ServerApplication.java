package com.youzi.blue.server;

import com.youzi.blue.server.work.ServerStarter;
import com.youzi.blue.common.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ServletComponentScan
@SpringBootApplication
@Slf4j
public class ServerApplication {
    public static void main(String[] args) {
        Config.getInstance().init(args);
        SpringApplication.run(ServerApplication.class, args);
        ServerStarter.start();
        log.warn("启动成功");
    }

}
