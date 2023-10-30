package cn.mercury.xcode.code.setting;

import cn.mercury.xcode.model.template.AbstractEditorItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Template implements Cloneable, AbstractEditorItem<Template> {

    private String name;

    private String group;

    private String file;

    private String content;

    private String path;

    private String nameFormat;

    private String packageFormat;

    private String modulePath;

    private String packageName;

    private boolean enable;

    @JsonIgnore
    private String code;

    public Template(){

    }

    public Template(String name,String content){
        this.name = name;
        this.content = content;
    }

    public Template clone()  {
        try {
            return (Template)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Template defaultVal() {
        return new Template();
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
        return this.content;
    }
}
