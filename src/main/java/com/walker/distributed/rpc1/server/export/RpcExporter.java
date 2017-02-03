package com.walker.distributed.rpc1.server.export;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcExporter {
	 /** 
     * 发布服务
     *  
     * @param service 服务实现类
     * @param port 端口
     * @throws Exception 
     */  
    public static void export(final Object service, int port) throws Exception {  
        if (service == null)  
            throw new IllegalArgumentException("service instance == null");  
        if (port <= 0 || port > 65535)  
            throw new IllegalArgumentException("Invalid port " + port);  
        System.out.println("Export service " + service.getClass().getName() + " on port " + port);  
        ServerSocket server = new ServerSocket(port);  
        for(;;) {  
            try {
                //不断接收客户端连接
                final Socket socket = server.accept();
                //一旦有新客户端连接过来，即新起线程处理
                new Thread(new Runnable() {
                    public void run() {  
                        try {  
                            try {
                                //这里服务端的处理思路是：
                                // 1）在指定端口暴露指定服务实例；
                                // 2）一旦有客户端连接进来，则另起线程处理；
                                // 3）从客户端socket中读取 方法名、参数类型、参数 ，结合暴露的服务实例，反射获取方法，再在服务实例上反射调用，获取结果
                                // 4）回写结果
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());  
                                try {  
                                    String methodName = input.readUTF();   //读方法名
                                    Class<?>[] parameterTypes = (Class<?>[])input.readObject();  //读参数类型
                                    Object[] arguments = (Object[])input.readObject();  //读参数
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());  
                                    try {  
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);  //反射获取方法
                                        Object result = method.invoke(service, arguments);  //反射调用
                                        output.writeObject(result);   //回写结果
                                    } catch (Throwable t) {  
                                        output.writeObject(t);  
                                    } finally {  
                                        output.close();  
                                    }  
                                } finally {  
                                    input.close();  
                                }  
                            } finally {  
                                socket.close();  
                            }  
                        } catch (Exception e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }).start();  //启动线程

            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
