/** by Kain **/

package cn.wonhigh.domain.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan(basePackages={"cn.wonhigh.repository"})
@ComponentScan(basePackages = {"cn.wonhigh.manager.impl","cn.wonhigh.service.impl"})
public class AppDomainAutoConfiguration {

	public AppDomainAutoConfiguration(){
		System.err.println("init domain ...");
	}
}
