package com.walker.distributed.rpc2;

import java.net.InetSocketAddress;

/**
 * @author walkerwei
 * @version 2017/2/3
 */
public class RpcTest {
    public static void main(String[] args) {
        //单独起线程，发布服务端服务
        new Thread(new Runnable() {
            public void run() {
                try {
                    RpcExporter.exporter("localhost", 8080); //服务端暴露，不断接收客户端请求，并交由线程池处理
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //在main线程中启动consumer，进行测试
        RpcImporter<EchoService> importer = new RpcImporter();
        EchoService echoService = importer.importer(EchoServiceImpl.class,new InetSocketAddress("localhost",8080));
        String pong = echoService.echo("hello");
        System.out.println(pong);
    }
}
