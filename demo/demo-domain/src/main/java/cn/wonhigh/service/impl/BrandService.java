/** Kain **/
package cn.wonhigh.service.impl;

import org.springframework.stereotype.Service;
import cn.wonhigh.model.Brand;
import cn.wonhigh.repository.BrandRepository;

import cn.wonhigh.service.IBrandService;
import topmall.framework.repository.IRepository;
import topmall.framework.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BrandService extends BaseService<Brand,Integer> implements  IBrandService{
    @Autowired
    private BrandRepository repository;

    protected IRepository<Brand,Integer> getRepository(){
        return repository;
    }

    
    public Brand findByUnique(String brandNo){
        return repository.findByUnique(brandNo);
    }

    public Integer deleteByUnique(String brandNo){
        return repository.deleteByUnique(brandNo);
    }

    public Integer insertForUpdate(Brand entry){
       return repository.insertForUpdate(entry);
    }
    
}
