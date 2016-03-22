package com.home.aop;

import org.springframework.aop.framework.ProxyFactory;

public class HelloWorldAOPExample {

	public static void main(String[] args)
	{
		MessageWriter target = new MessageWriter();
		ProxyFactory factory = new ProxyFactory();
		factory.addAdvice(new MessageDecorator());
		factory.setTarget(target);
		MessageWriter proxyTarget = (MessageWriter)factory.getProxy();
		proxyTarget.writeMessage();
	}
}
