package com.zishuo.proxy;

import com.zishuo.common.Invocation;
import com.zishuo.common.URL;
import com.zishuo.loadbalance.LoadBalance;
import com.zishuo.protocol.HttpClient;
import com.zishuo.register.MapRemoteRegister;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyFactory {
    public static <T>T getProxy(Class interfaceClass) {
        Object proxyInstance =
                Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                                // return if didnt go in
                                String mock = System.getProperty("mock");
                                if (mock != null && mock.startsWith("return:")) {
                                    String result = mock.replace("return:", "");
                                    return result;
                                }


                                Invocation invocation = new Invocation(interfaceClass.getName(),
                                        method.getName(), method.getParameterTypes(), args);
                                HttpClient httpClient = new HttpClient();

                                List<URL> list = MapRemoteRegister.get(interfaceClass.getName());


                                String result = null;
                                List<URL> invokedUrls = new ArrayList<>();
                                // retrying
                                int max = 3;
                                while(max> 0) {
                                    //changing urls
                                    list.remove(invokedUrls);
                                    URL url = LoadBalance.random(list);
                                    invokedUrls.add(url);
                                    try {
                                        result = httpClient.send(url.getHostname(), url.getPort(), invocation);
                                    } catch (Exception e) {
                                        if(max-- != 0) {
                                            continue;
                                        }
                                        // error - call back = com.zishuo.HelloServiceErrorCallBack
                                        return "error";
                                    }
                                }
                                return result;
                            }
                });

        return (T) proxyInstance;
    }
}
