package com.walker.distributed.rpc1.service.impl;


import com.walker.distributed.rpc1.service.HelloService;

public class HelloServiceImpl implements HelloService {
	public String hello(String name) {  
        return "Hello " + name;  
    }
}
