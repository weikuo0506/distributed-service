package com.walker.distributed.rpc2;

/**
 * @author walkerwei
 * @version 2017/2/3
 */
public class EchoServiceImpl implements EchoService {
    public String echo(String ping) {
        return ping + " --> I am OK!";
    }
}
