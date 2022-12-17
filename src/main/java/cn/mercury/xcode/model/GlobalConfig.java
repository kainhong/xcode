package cn.mercury.xcode.model;

import cn.mercury.xcode.model.template.AbstractEditorItem;

/**
 * 全局配置实体类
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/27 13:07
 */

public class GlobalConfig implements AbstractEditorItem<GlobalConfig> {
    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private String value;

    private String uri;

    public GlobalConfig(){

    }

    public GlobalConfig(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public GlobalConfig defaultVal() {
        return new GlobalConfig("demo", "value");
    }

    @Override
    public void changeFileName(String name) {
        this.name = name;
    }

    @Override
    public String fileName() {
        return this.name;
    }

    @Override
    public void changeFileContent(String content) {
        this.value = content;
    }

    @Override
    public String fileContent() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
