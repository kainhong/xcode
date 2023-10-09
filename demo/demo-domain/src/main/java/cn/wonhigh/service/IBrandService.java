/** Kain **/
package cn.wonhigh.service;

import cn.wonhigh.model.Brand;
import topmall.framework.service.IService;

public interface IBrandService extends IService<Brand,Integer>{

    
    public Brand findByUnique(String brandNo);

    public Integer deleteByUnique(String brandNo);

    public Integer insertForUpdate(Brand entry);
    
}