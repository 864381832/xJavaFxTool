package com.xwintop.xJavaFxTool.apache.WebService;

import javax.jws.WebService;

@WebService
public class WebServiceImpl implements WebServiceI {
	@Override
	public String sayHello(String name) {
		System.out.println("WebService sayHello " + name);
		return "sayHello " + name;
	}

	@Override
	public String save(String name, String pwd) {
		System.out.println("WebService save " + name + "， " + pwd);
		return "save Success";
	}
}
