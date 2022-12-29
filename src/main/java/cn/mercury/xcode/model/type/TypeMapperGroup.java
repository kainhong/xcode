package cn.mercury.xcode.model.type;

import cn.mercury.xcode.model.AbstractGroup;

import java.util.List;

/**
 * 类型映射分组
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */

public class TypeMapperGroup implements AbstractGroup<TypeMapperGroup, TypeMapper> {
    /**
     * 分组名称
     */
    private String name;
    /**
     * 元素对象
     */
    private List<TypeMapper> elementList;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<TypeMapper> getElementList() {
        return elementList;
    }

    @Override
    public void setElementList(List<TypeMapper> elementList) {
        this.elementList = elementList;
    }
}
