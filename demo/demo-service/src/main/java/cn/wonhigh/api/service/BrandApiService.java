/** by Kain **/
package cn.wonhigh.api.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

import cn.wonhigh.api.IBrandApi;
import cn.wonhigh.manager.IBrandManager;
import cn.wonhigh.model.Brand;
import cn.mercury.manager.IManager;
import cn.mercury.service.api.AbstractApiService;

@Service
public class BrandApiService extends AbstractApiService<Brand,Integer>
                implements IBrandApi {
    @Autowired
    private IBrandManager manager;

    @Override
    protected IManager<Brand,Integer> getManager(){
        return manager;
    }

    
    public Brand findByUnique(String brandNo) {
        return manager.findByUnique(brandNo);
    }

}
