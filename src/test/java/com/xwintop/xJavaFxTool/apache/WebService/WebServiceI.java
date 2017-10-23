package com.xwintop.xJavaFxTool.apache.WebService;

import javax.jws.WebService;

@WebService
public interface WebServiceI {
	public String sayHello(String name);

	public String save(String name, String pwd);
}
