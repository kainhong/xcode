package cn.mercury.xcode.code.model.table;

import cn.mercury.xcode.code.setting.AbstractGroup;

import java.util.List;

/**
 * 列配置分组
 *

 * @version 1.0.0
 * @since 2018/07/18 09:33
 */

public class ColumnConfigGroup implements AbstractGroup<ColumnConfigGroup, ColumnConfig> {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 分组名称
     */
    private String name;

    @Override
    public List<ColumnConfig> getElementList() {
        return elementList;
    }

    @Override
    public void setElementList(List<ColumnConfig> elementList) {
        this.elementList = elementList;
    }

    /**
     * 元素对象
     */
    private List<ColumnConfig> elementList;



}
