package cn.mercury.xcode.model.template;

import cn.mercury.xcode.model.AbstractGroup;

import java.util.List;

/**
 * 模板分组类
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/18 09:33
 */

public class TemplateGroup implements AbstractGroup<TemplateGroup, Template> {
    /**
     * 分组名称
     */
    private String name;

    private String version;
    /**
     * 元素对象
     */
    private List<Template> elementList;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Template> getElementList() {
        return elementList;
    }

    @Override
    public void setElementList(List<Template> elementList) {
        this.elementList = elementList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
