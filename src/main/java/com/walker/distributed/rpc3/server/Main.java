package com.walker.distributed.rpc3.server;

import com.walker.distributed.rpc3.api.HeyService;
import com.walker.distributed.rpc3.server.RpcServer;
import com.walker.distributed.rpc3.service.HeyServiceImpl;


public class Main {


    public static void main(String[] args) {
        RpcServer server = new RpcServer(2537);
        HeyService heyService = new HeyServiceImpl();
        server.registry(heyService); //注册和暴露的是服务instance，这样客户端只需传过来 方法名、参数类型、参数 即可；
        server.start();
    }
}
