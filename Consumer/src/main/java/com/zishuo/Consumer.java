package com.zishuo;

import com.zishuo.common.Invocation;
import com.zishuo.protocol.HttpClient;
import com.zishuo.proxy.ProxyFactory;

public class Consumer {
    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("zishuo");
        System.out.println(result);

    }
}
