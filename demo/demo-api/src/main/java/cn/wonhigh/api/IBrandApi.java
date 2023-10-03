/** by Kain **/

package cn.wonhigh.api;

import cn.wonhigh.model.Brand;
import cn.mercury.service.api.IApiService;

public interface IBrandApi extends IApiService<Brand,Integer>{

    
    public Brand findByUnique(String brandNo);
    
}