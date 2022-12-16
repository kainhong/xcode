package cn.mercury.xcode.model;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectTree {
    private String name;

    private String path;

    private Project project;

    private List<ModuleNode> children = new ArrayList<>();

    public ModuleNode find(String path) {
        for (ModuleNode node : this.children) {
            ModuleNode item = find(node, path);
            if (item != null)
                return item;
        }
        return null;
    }

    public ModuleNode findParent(String path) {
        String[] vals = path.split("/");

        String parent = StringUtils.join(vals, "/", 0, vals.length - 1);

        return find(parent);
    }

    private ModuleNode find(ModuleNode node, String path) {
        if (node.getPath().equals(path))
            return node;
        for (ModuleNode child : node.children) {
            ModuleNode item = find(child, path);
            if (item != null)
                return item;
        }
        return null;
    }

    @Data
    public static class ModuleNode {

        private String name;

        private String path;

        private Module module;

        private List<ModuleNode> children = new ArrayList<>();

    }
}
