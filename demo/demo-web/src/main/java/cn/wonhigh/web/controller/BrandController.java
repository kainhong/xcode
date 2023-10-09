/** by Kain **/
package cn.wonhigh.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.wonhigh.manager.IBrandManager;
import cn.wonhigh.model.Brand;
import cn.mercury.manager.IManager;
import topmall.framework.web.controller.ApiController;

@RestController

@RequestMapping("/brand")
public class BrandController extends ApiController<Brand,Integer> {
    @Autowired
    private IBrandManager manager;

    protected IManager<Brand,Integer> getManager(){
        return manager;
    }
    

    
    @RequestMapping("/find/")
    public Brand findByUnique(String brandNo) {
         return manager.findByUnique(brandNo);
    }

    @RequestMapping("/delete/unique/")
    public Integer deleteByUnique(String brandNo){
        return manager.deleteByUnique(brandNo);
    }
    
}
