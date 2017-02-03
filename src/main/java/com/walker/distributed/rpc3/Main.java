package com.walker.distributed.rpc3;


import com.walker.distributed.rpc3.api.HeyService;
import com.walker.distributed.rpc3.server.RpcServer;
import com.walker.distributed.rpc3.service.HeyServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        RpcServer server = new RpcServer(9527);
        HeyService heyService = new HeyServiceImpl();
        server.registry(heyService);
        server.start();
    }
}
