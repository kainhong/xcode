package cn.mercury.xcode.code.model.table;

import cn.mercury.xcode.code.setting.IEntry;

/**
 * 列配置信息
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class ColumnConfig implements IEntry<ColumnConfig> {
    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    private ColumnConfigType type;
    /**
     * 可选值，逗号分割
     */
    private String selectValue;

    public ColumnConfig(){

    }

    public ColumnConfig(String title, ColumnConfigType type) {
        this.title = title;
        this.type = type;
    }

    public ColumnConfig(String title, ColumnConfigType type, String selectValue) {
        this.title = title;
        this.type = type;
        this.selectValue = selectValue;
    }

    @Override
    public ColumnConfig defaultVal() {
        return new ColumnConfig("demo", ColumnConfigType.TEXT);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ColumnConfigType getType() {
        return type;
    }

    public void setType(ColumnConfigType type) {
        this.type = type;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }
}
