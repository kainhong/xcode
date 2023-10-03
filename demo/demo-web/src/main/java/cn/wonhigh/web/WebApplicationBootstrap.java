
package cn.wonhigh.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import topmall.framework.core.ApplicationBootstrap;
import topmall.framework.web.inspect.SecurityInterceptor;

@EnableApolloConfig
@SpringBootApplication
@ComponentScan(basePackages = { "topmall.framework.web", "cn.wonhigh.web", "cn.wonhigh.web.controller" })
@ServletComponentScan(basePackageClasses = { SecurityInterceptor.class })
public class WebApplicationBootstrap extends ApplicationBootstrap {
	public static void main(String[] args) {
		new WebApplicationBootstrap().run(args);
	}
}