/** Kain **/
package cn.wonhigh.manager;

import cn.wonhigh.model.Brand;

import cn.mercury.manager.IManager;

public interface IBrandManager extends IManager<Brand,Integer>{

    
    public Brand findByUnique(String brandNo) ;

    public Integer deleteByUnique(String brandNo);

    public Integer insertForUpdate(Brand entry);
    
}
