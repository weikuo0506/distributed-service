package com.walker.distributed.rpc2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author walkerwei
 * @version 2017/2/3
 */
public class RpcImporter<S> {
    //这里服务引入的思路是：1）在指定addr上引入指定类的服务 2）再在服务类对应的接口上加代理
    // 代理的思路是：1）一旦有请求进来，如serviceInstance.echo(args)，则将 服务类名、方法名、参数类型、参数 发送给server，然后等待接收结果；
    //为什么一定要传入服务类名过去，而不是接口名呢？因为服务端暴露时候没有指定服务，需要根据客户端传过去的类名反射new出实例，然后再调用！
    public S importer(final Class<?> serviceClass, final InetSocketAddress addr) {
        //根据传入的服务类名，生成动态代理
        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass.getInterfaces()[0]}, new InvocationHandler() {
            //下面是生成动态代理部分代码，匿名内部类
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ObjectInputStream ois = null;
                ObjectOutputStream oos = null;
                Socket socket = null;
                try {
                    socket = new Socket();
                    socket.connect(addr); //Connects this socket to the server.
                    oos = new ObjectOutputStream(socket.getOutputStream());  //写请求参数
                    //写服务类名
                    oos.writeUTF(serviceClass.getName());   //为什么不能writeObject？
                    //写方法名
                    oos.writeUTF(method.getName());
                    //写方法参数类型
                    oos.writeObject(method.getGenericParameterTypes());
                    //写参数
                    oos.writeObject(args);
                    ois = new ObjectInputStream(socket.getInputStream());//后读结果，同步等待
                    Object result = ois.readObject();
                    return result;
                } finally {
                    CloseUtil.close(ois,oos,socket);
                }
            }
        });
    };
}
