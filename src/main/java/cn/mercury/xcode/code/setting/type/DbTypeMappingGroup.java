package cn.mercury.xcode.code.setting.type;

import cn.mercury.xcode.code.setting.AbstractGroup;

import java.util.List;

/**
 * 类型映射分组
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */

public class DbTypeMappingGroup implements AbstractGroup<DbTypeMappingGroup, DbTypeMapping> {
    /**
     * 分组名称
     */
    private String name;
    /**
     * 元素对象
     */
    private List<DbTypeMapping> elementList;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<DbTypeMapping> getElementList() {
        return elementList;
    }

    @Override
    public void setElementList(List<DbTypeMapping> elementList) {
        this.elementList = elementList;
    }
}
