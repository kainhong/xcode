package cn.mercury.xcode.code.setting;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TemplateGroup implements IEntryGroup<TemplateGroup,Template>{
    private String name;
    private String description;
    private String version;
    private String author;
    private String path;

    @JsonProperty("templates")
    private List<Template> items;

    public Template getTemplate(String name) {
        return items.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);
    }

}
