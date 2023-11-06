package cn.mercury.xcode.code.model.table;

import cn.mercury.xcode.code.setting.IEntryGroup;

import java.util.List;

/**
 * 列配置分组
 *

 * @version 1.0.0
 * @since 2018/07/18 09:33
 */

public class ColumnConfigEntryGroup implements IEntryGroup<ColumnConfigEntryGroup, ColumnConfig> {
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
    public List<ColumnConfig> getItems() {
        return items;
    }

    @Override
    public void setItems(List<ColumnConfig> values) {
        this.items = values;
    }

    /**
     * 元素对象
     */
    private List<ColumnConfig> items;



}
