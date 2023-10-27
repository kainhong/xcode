package cn.mercury.xcode.code.setting;

import lombok.Data;

@Data
public class Template implements Cloneable {

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

    public Template clone()  {
        try {
            return (Template)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
