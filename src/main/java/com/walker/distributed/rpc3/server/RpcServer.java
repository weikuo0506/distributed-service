package com.walker.distributed.rpc3.server;

import com.walker.distributed.CloseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;


public class RpcServer {

    private static final Logger LOG = LoggerFactory.getLogger(RpcServer.class);

    private int port;

    private ServerSocket server;

    private Object service;

    public RpcServer(int port) {
        this.port = port;

    }

    public ServerSocket getServer() {
        return server;
    }

    public void start() {
        try {
            server = new ServerSocket(port);
            LOG.info("Simple RPC Server Start! [port:{}]",port);
            for (; ; ) {
                final Socket socket = server.accept();
                LOG.info("a client connecting to server now!");
                new Thread(new Runnable() {
                    public void run() {
                        ObjectInputStream input = null;
                        ObjectOutputStream output = null;
                        try {
                            input = new ObjectInputStream(socket.getInputStream());
                            String methodName = input.readUTF();
                            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                            Object[] arguments = (Object[]) input.readObject();
                            Method method = service.getClass().getMethod(methodName, parameterTypes);
                            //反射调用
                            Object result = method.invoke(service, arguments);
                            //回写结果
                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeObject(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            CloseUtil.close(input,output,socket);
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            CloseUtil.close(server);
        }
    }

    public void registry(Object service) {
        LOG.info("service Regsitry ,serviceName = {}",service.getClass().getName());
        this.service = service;
    }

    public int getPort() {
        return port;
    }
}
