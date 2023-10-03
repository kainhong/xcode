package cn.wonhigh.deployer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import topmall.framework.servicemodel.Bootstrap;

@EnableApolloConfig
@SpringBootApplication
public class ApplicationBootstrap //extends Bootstrap
{

	public static void main(String[] args)   {
		Bootstrap.run(args, ApplicationBootstrap.class);
	}
}
