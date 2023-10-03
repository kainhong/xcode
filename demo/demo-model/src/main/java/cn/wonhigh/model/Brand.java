/** Kain **/
package cn.wonhigh.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import cn.mercury.annotation.Label;
import cn.mercury.basic.query.Query;
import cn.mercury.domain.AbstractEntryBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/** auto generate start ,don't modify */

/**
* 品牌信息表
**/

@ApiModel(description= "品牌信息表")
public class Brand  extends cn.mercury.domain.BaseEntity<Integer>  
{

    private static final long serialVersionUID = 1642986891476L;
    
    /**
    *品牌编码
    **/ 
    @ApiModelProperty(value = "品牌编码",name="brandNo")
    @Label("品牌编码") 
    private String brandNo;
    

    public String getBrandNo(){
        return  brandNo;
    }
    public void setBrandNo(String val ){
        brandNo = val;
    }
    /**
    *品牌中文名称
    **/ 
    @ApiModelProperty(value = "品牌中文名称",name="name")
    @Label("品牌中文名称") 
    private String name;
    

    public String getName(){
        return  name;
    }
    public void setName(String val ){
        name = val;
    }
    /**
    *品牌英文名称
    **/ 
    @ApiModelProperty(value = "品牌英文名称",name="enName")
    @Label("品牌英文名称") 
    private String enName;
    

    public String getEnName(){
        return  enName;
    }
    public void setEnName(String val ){
        enName = val;
    }
    /**
    *品牌英文简称
    **/ 
    @ApiModelProperty(value = "品牌英文简称",name="enShortName")
    @Label("品牌英文简称") 
    private String enShortName;
    

    public String getEnShortName(){
        return  enShortName;
    }
    public void setEnShortName(String val ){
        enShortName = val;
    }
    /**
    *商品编码特征码(用于鞋类商品编码的首位字母,必须输入且只能输入一位字

符)
    **/ 
    @ApiModelProperty(value = "商品编码特征码",name="opcode")
    @Label("商品编码特征码") 
    private String opcode;
    

    public String getOpcode(){
        return  opcode;
    }
    public void setOpcode(String val ){
        opcode = val;
    }
    /**
    *品牌类别(D:代理品牌，S:自有品牌)
    **/ 
    @ApiModelProperty(value = "品牌类别",name="category")
    @Label("品牌类别") 
    private String category;
    

    public String getCategory(){
        return  category;
    }
    public void setCategory(String val ){
        category = val;
    }
    /**
    *品牌归属(X:鞋，S:体)
    **/ 
    @ApiModelProperty(value = "品牌归属",name="belonger")
    @Label("品牌归属") 
    private String belonger;
    

    public String getBelonger(){
        return  belonger;
    }
    public void setBelonger(String val ){
        belonger = val;
    }
    /**
    *品牌状态(0 = 撤消 1 = 正常)
    **/ 
    @ApiModelProperty(value = "品牌状态",name="status")
    @Label("品牌状态") 
    private Integer status;
    

    public Integer getStatus(){
        return  status;
    }
    public void setStatus(Integer val ){
        status = val;
    }
    /**
    *所属业务单元(关联品牌部编码)
    **/ 
    @ApiModelProperty(value = "所属业务单元",name="sysNo")
    @Label("所属业务单元") 
    private String sysNo;
    

    public String getSysNo(){
        return  sysNo;
    }
    public void setSysNo(String val ){
        sysNo = val;
    }
    /**
    *检索码
    **/ 
    @ApiModelProperty(value = "检索码",name="searchCode")
    @Label("检索码") 
    private String searchCode;
    

    public String getSearchCode(){
        return  searchCode;
    }
    public void setSearchCode(String val ){
        searchCode = val;
    }
    /**
    *父品牌
    **/ 
    @ApiModelProperty(value = "父品牌",name="parentBrandId")
    @Label("父品牌") 
    private Integer parentBrandId;
    

    public Integer getParentBrandId(){
        return  parentBrandId;
    }
    public void setParentBrandId(Integer val ){
        parentBrandId = val;
    }
    /**
    *LOGO链接地址
    **/ 
    @ApiModelProperty(value = "链接地址",name="logoUrl")
    @Label("链接地址") 
    private String logoUrl;
    

    public String getLogoUrl(){
        return  logoUrl;
    }
    public void setLogoUrl(String val ){
        logoUrl = val;
    }
    /**
    *备注
    **/ 
    @ApiModelProperty(value = "备注",name="remark")
    @Label("备注") 
    private String remark;
    

    public String getRemark(){
        return  remark;
    }
    public void setRemark(String val ){
        remark = val;
    }
    /**
    *时间序列
    **/ 
    @ApiModelProperty(value = "时间序列",name="timeSeq")
    @Label("时间序列") 
    private Long timeSeq;
    

    public Long getTimeSeq(){
        return  timeSeq;
    }
    public void setTimeSeq(Long val ){
        timeSeq = val;
    }
    /**
    *本部编码
    **/ 
    @ApiModelProperty(value = "本部编码",name="organTypeNo")
    @Label("本部编码") 
    private String organTypeNo;
    

    public String getOrganTypeNo(){
        return  organTypeNo;
    }
    public void setOrganTypeNo(String val ){
        organTypeNo = val;
    }
    
    @Override
	public String toString() {
         return ToStringBuilder.reflectionToString(this);
	}
	    
    public BrandBuilder build(){
        return new BrandBuilder(this);
    }

    public static class BrandBuilder extends AbstractEntryBuilder<Brand>{

        private BrandBuilder(Brand entry){
            this.obj = entry;
        }

       @Override
		public Brand object() {
			return this.obj;
		}

        
        public BrandBuilder id(Integer value ){
            
            this.obj.setId(value);
            
            if( query == null  )
                query = new Query();
            this.query.where("id", value);
            return this;
        }
        
        public BrandBuilder brandNo(String value ){
            
            this.obj.brandNo = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("brandNo", value);
            return this;
        }
        
        public BrandBuilder name(String value ){
            
            this.obj.name = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("name", value);
            return this;
        }
        
        public BrandBuilder enName(String value ){
            
            this.obj.enName = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("enName", value);
            return this;
        }
        
        public BrandBuilder enShortName(String value ){
            
            this.obj.enShortName = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("enShortName", value);
            return this;
        }
        
        public BrandBuilder opcode(String value ){
            
            this.obj.opcode = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("opcode", value);
            return this;
        }
        
        public BrandBuilder category(String value ){
            
            this.obj.category = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("category", value);
            return this;
        }
        
        public BrandBuilder belonger(String value ){
            
            this.obj.belonger = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("belonger", value);
            return this;
        }
        
        public BrandBuilder status(Integer value ){
            
            this.obj.status = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("status", value);
            return this;
        }
        
        public BrandBuilder sysNo(String value ){
            
            this.obj.sysNo = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("sysNo", value);
            return this;
        }
        
        public BrandBuilder searchCode(String value ){
            
            this.obj.searchCode = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("searchCode", value);
            return this;
        }
        
        public BrandBuilder parentBrandId(Integer value ){
            
            this.obj.parentBrandId = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("parentBrandId", value);
            return this;
        }
        
        public BrandBuilder logoUrl(String value ){
            
            this.obj.logoUrl = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("logoUrl", value);
            return this;
        }
        
        public BrandBuilder createUser(String value ){
            
            this.obj.setCreateUser(value);
            
            if( query == null  )
                query = new Query();
            this.query.where("createUser", value);
            return this;
        }
        
        public BrandBuilder createTime(Date value ){
            
            this.obj.setCreateTime(value);
            
            if( query == null  )
                query = new Query();
            this.query.where("createTime", value);
            return this;
        }
        
        public BrandBuilder updateUser(String value ){
            
            this.obj.setUpdateUser(value);
            
            if( query == null  )
                query = new Query();
            this.query.where("updateUser", value);
            return this;
        }
        
        public BrandBuilder updateTime(Date value ){
            
            this.obj.setUpdateTime(value);
            
            if( query == null  )
                query = new Query();
            this.query.where("updateTime", value);
            return this;
        }
        
        public BrandBuilder remark(String value ){
            
            this.obj.remark = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("remark", value);
            return this;
        }
        
        public BrandBuilder timeSeq(Long value ){
            
            this.obj.timeSeq = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("timeSeq", value);
            return this;
        }
        
        public BrandBuilder organTypeNo(String value ){
            
            this.obj.organTypeNo = value;
            
            if( query == null  )
                query = new Query();
            this.query.where("organTypeNo", value);
            return this;
        }
        
    }
     /** auto generate end,don't modify */
    }