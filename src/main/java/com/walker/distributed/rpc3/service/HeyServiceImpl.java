package com.walker.distributed.rpc3.service;

import com.walker.distributed.rpc3.api.HeyService;

public class HeyServiceImpl implements HeyService {

    public String say(String words) {
        String msg = "hello, "+words;
        System.out.println(msg);
        return msg;
    }
}
