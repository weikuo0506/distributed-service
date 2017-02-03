package com.walker.distributed.rpc1.server.provider;


import com.walker.distributed.rpc1.server.export.RpcExporter;
import com.walker.distributed.rpc1.service.HelloService;
import com.walker.distributed.rpc1.service.impl.HelloServiceImpl;

public class RpcProvider {
	 public static void main(String[] args) throws Exception {  
		 HelloService service = new HelloServiceImpl();
		 //在指定端口上暴露服务
	     RpcExporter.export(service, 1234);
	    }  
}
