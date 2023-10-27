package cn.mercury.xcode.code.setting;

import cn.hutool.core.io.FileUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.setting.GlobalSetting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateConfiguration {
    public static final String TEMPLATES = "templates";

    public static File getTemplatePath() {
        try {
            return GlobalSetting.getScratchPath(TEMPLATES);
        } catch (IOException e) {
            return null;
        }
    }
    List<TemplateGroup> templateList;

    public TemplateGroup getTemplateGroup(String name ){
        return getTemplateList().stream().filter(item->item.getName().equals(name)).findFirst().orElse(null);
    }


    public List<TemplateGroup> getTemplateList() {
        File resourceDirectory = null;

        try {
            resourceDirectory = GlobalSetting.getScratchPath(TEMPLATES);
        } catch (IOException e) {
            return Collections.emptyList();
        }

        if( templateList != null ) {
            return templateList;
        }

        if (!resourceDirectory.exists()) {
            return Collections.emptyList();
        }

        List<TemplateGroup> list = new ArrayList<>();
        //File metaFile = new File(resourceDirectory, ".meta.xml");

        for (File file : resourceDirectory.listFiles()) {
            File metaFile = new File(file, ".meta.json");
            if (!metaFile.exists())
                continue;

            TemplateGroup meta = null;
            try {
                meta = JsonUtils.fromJson(FileUtil.readUtf8String(metaFile), TemplateGroup.class);
            } catch (IOException e) {
                return list;
            }

            meta.setPath(file.getParent());

            list.add(meta);
        }

        //templateList = list;

        return list;
    }


    static TemplateConfiguration inc = new TemplateConfiguration();

    public static TemplateConfiguration instance() {
        return inc;
    }

}
