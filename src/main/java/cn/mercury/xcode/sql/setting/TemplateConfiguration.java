package cn.mercury.xcode.sql.setting;

import cn.hutool.core.io.FileUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.setting.GlobalSetting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateConfiguration {
    public static final String TEMPLATES = "templates";

    public List<TemplateDesc> getTemplateList() {
        List<TemplateDesc> list = new ArrayList<>();

        File resourceDirectory = null;
        try {
            resourceDirectory = GlobalSetting.getScratchPath(TEMPLATES);
        } catch (IOException e) {
            return list;
        }

        if (!resourceDirectory.exists()) {
            return list;
        }
        //File metaFile = new File(resourceDirectory, ".meta.xml");

        for (File file : resourceDirectory.listFiles()) {
            File metaFile = new File(file, ".meta.xml");
            if (!metaFile.exists())
                continue;

            TemplateDesc meta = null;
            try {
                meta = JsonUtils.fromJson(FileUtil.readUtf8String(metaFile), TemplateDesc.class);
            } catch (IOException e) {
                return list;
            }

            meta.setPath(file.getParent());

            list.add(meta);
        }

        return list;
    }



    static TemplateConfiguration inc = new TemplateConfiguration();

    public static TemplateConfiguration instance() {
        return inc;
    }

}
