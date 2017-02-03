package com.walker.distributed.rpc2;

import com.walker.distributed.CloseUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author walkerwei
 * @version 2017/2/3
 */
public class RpcExporter {
    //静态线程池
    private static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * 将服务端暴露到指定主机和端口
     * @param host
     * @param port
     */
    public static void exporter(String host, int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(host, port));
        try{
            //死循环，不断接收客户端请求
            while (true) {
                Socket socket = serverSocket.accept(); //没有请求会阻塞在这里
                executor.execute(new ExporterTask(socket));
            }
        }finally {
            CloseUtil.close(serverSocket);
        }
    };

    //静态内部类：处理接收到的客户端socket
    private static class ExporterTask implements Runnable {
        //客户端socket
        private Socket client = null;

        public ExporterTask(Socket socket) {
            this.client = socket;
        }

        public void run() {
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            try {
                ois = new ObjectInputStream(client.getInputStream());//先读
                String serviceClassName = ois.readUTF(); //读服务名
                Class clazz = Class.forName(serviceClassName);
                Object instance = clazz.newInstance(); //反射生成服务实例。注意，也可以改为直接将服务实例暴露出去
                String methodName = ois.readUTF(); //读方法名
                Object parameterTypes = ois.readObject(); //读参数类型
                Method method = clazz.getMethod(methodName, (Class<?>[]) parameterTypes);
                Object[] args = (Object[]) ois.readObject(); //读参数
                Object result = method.invoke(instance, args); //反射调用
                oos = new ObjectOutputStream(client.getOutputStream()); //后写结果
                oos.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                CloseUtil.close(ois,oos, client);
            }
        }
    }

}
