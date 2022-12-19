package cn.mercury.xcode.model.template;

import lombok.Data;

/**
 * 模板信息类
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class Template implements AbstractEditorItem<Template> {
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板代码
     */
    private String code;

    private String uri;

    private String type;

    private String path;

    private String packageSuffix;

    private String value;

    public Template(){

    }

    public Template(String name, String code) {
        this.name = name;
        this.code = code;
    }


    @Override
    public Template defaultVal() {
        return new Template("demo", "template");
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
        this.code = content;
    }

    @Override
    public String fileContent() {
        return this.code;
    }
}
