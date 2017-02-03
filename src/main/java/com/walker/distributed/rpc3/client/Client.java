package com.walker.distributed.rpc3.client;


import com.walker.distributed.rpc3.api.HeyService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class Client {

    private String host;

    private int port;

    private Socket socket;

    public Client(String host,int port){
        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client("localhost", 2537); //建立socket连接
        HeyService proxy = client.getProxy(HeyService.class);  //获取指定接口的代理
        for(int i=0;i<20;i++) {
            String result = proxy.say("world "+i);  //在代理上调用
            System.out.println(result);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public <T> T getProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeUTF(method.getName()); //写方法名
                        output.writeObject(method.getParameterTypes()); //写参数类型
                        output.writeObject(arguments); //写参数
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream()); //同步等待结果
                        return input.readObject();
                    }
                });
    }
}
