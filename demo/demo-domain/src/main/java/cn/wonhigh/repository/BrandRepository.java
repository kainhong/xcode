/** Kain **/
package cn.wonhigh.repository;

import cn.wonhigh.model.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import topmall.framework.repository.IRepository;

import java.util.List;

@Mapper
public interface BrandRepository extends IRepository<Brand,Integer> {
    
    public Brand findByUnique(@Param("brandNo") String brandNo);

    public Integer deleteByUnique(@Param("brandNo") String brandNo);

    public Integer insertForUpdate(Brand entry);

    public List<Brand> fetchItems();
}
