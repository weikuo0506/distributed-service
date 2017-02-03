package com.walker.distributed.rpc1.client.refer;

/**
 * @author walkerwei
 * @version 2017/2/3
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RpcRefer {
    /**
     * 引用服务
     *
     * @param <T> 接口泛型
     * @param interfaceClass 接口类型
     * @param host 服务器主机名
     * @param port 服务器端口
     * @return 远程服务
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {
        if (interfaceClass == null)
            throw new IllegalArgumentException("Interface class == null");
        if (! interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        if (host == null || host.length() == 0)
            throw new IllegalArgumentException("Host == null!");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);
        //客户端的引入服务思路是：
        // 1)在指定端口引入指定接口类型的服务
        // 2）在指定接口类型的服务上加代理
        // 3）自定义invocationHandler，处理方法调用，处理思路很简单，即：
        // 一旦有请求进来，如serviceInstance.hello(args)，则将 方法名、参数类型、参数 发送给server，然后等待接收结果；
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new InvocationHandler() {
            //下面定义具体的调用代理
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                //这里的代理思路是：一旦有请求进来，如serviceInstance.hello(args)，则将 方法名、参数类型、参数 发送给server，然后等待接收结果；
                Socket socket = new Socket(host, port); //连到server上
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        output.writeUTF(method.getName()); //写方法名
                        output.writeObject(method.getParameterTypes()); //写参数类型
                        output.writeObject(arguments); //写入参
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            Object result = input.readObject();//同步调用，等待结果
                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            return result;
                        } finally {
                            input.close();
                        }
                    } finally {
                        output.close();
                    }
                } finally {
                    socket.close();
                }
            }

        });
    }

}

