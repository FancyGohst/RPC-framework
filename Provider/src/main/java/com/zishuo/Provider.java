package com.zishuo;

import com.zishuo.common.URL;
import com.zishuo.protocol.HttpServer;
import com.zishuo.register.LocalRegister;
import com.zishuo.register.MapRemoteRegister;

public class Provider {
    public static void main(String[] args) {

        LocalRegister.register(HelloService.class.getName(),"1.0", HelloServiceImpl.class);

        //register center
        URL url = new URL("localhost", 8080);
        MapRemoteRegister.register(HelloService.class.getName(), url);


        //Netty, Tomcat
        HttpServer httpServer = new HttpServer();
        httpServer.start(url.getHostname(), url.getPort());
    }
}
