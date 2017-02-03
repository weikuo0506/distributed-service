package com.walker.distributed.rpc1.client.consumer;

import com.walker.distributed.rpc1.client.refer.RpcRefer;
import com.walker.distributed.rpc1.service.HelloService;

/**
 * @author walkerwei
 * @version 2017/2/3
 */
public class RpcConsumer {
    public static void main(String[] args) throws Exception {
        HelloService service = RpcRefer.refer(HelloService.class, "127.0.0.1", 1234);
        for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            String result = service.hello("World " + i);
            System.out.println(result);
            Thread.sleep(1000);
        }
    }
}