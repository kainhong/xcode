/** Kain **/
package cn.wonhigh.manager.impl;

import org.springframework.stereotype.Service;
import cn.wonhigh.model.Brand;
import cn.wonhigh.service.IBrandService;
import cn.wonhigh.manager.IBrandManager;
import topmall.framework.service.IService;

import topmall.framework.manager.BaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import cn.mercury.manager.ManagerException;


@Service
public class BrandManager extends BaseManager<Brand,Integer> implements IBrandManager{
    @Autowired
    private IBrandService service;

    protected IService<Brand,Integer> getService(){
        return service;
    }

    
    public Brand findByUnique(String brandNo) {
        try {
			return service.findByUnique(brandNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public Integer deleteByUnique(String brandNo) {
        try {
			return service.deleteByUnique(brandNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

     public Integer insertForUpdate(Brand entry){
         try {
    			return service.insertForUpdate(entry);
    	 } catch (Exception e) {
    			throw new ManagerException(e);
    	 }
     }

    
}