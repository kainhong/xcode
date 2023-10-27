package cn.mercury.xcode.code.setting;


import lombok.Data;

import java.util.List;

@Data
public class TemplateGroup {
    private String name;
    private String description;
    private String version;
    private String author;
    private String path;

    private List<Template> templates;

    public Template getTemplate(String name) {
        return templates.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);
    }
}
