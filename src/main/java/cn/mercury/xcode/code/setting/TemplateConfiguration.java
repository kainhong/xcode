package cn.mercury.xcode.code.setting;

import cn.hutool.core.io.FileUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.code.service.storage.IGenerateStorageService;
import cn.mercury.xcode.mybatis.utils.StringUtil;
import cn.mercury.xcode.setting.GlobalSetting;
import org.jetbrains.annotations.VisibleForTesting;

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

    public TemplateGroup getTemplateGroup(String name) {
        return getTemplateGrops().stream().filter(item -> item.getName().equals(name)).findFirst().orElse(null);
    }


    public List<TemplateGroup> getTemplateGrops() {
        File resourceDirectory = null;

        try {
            resourceDirectory = GlobalSetting.getScratchPath(TEMPLATES);
        } catch (IOException e) {
            return Collections.emptyList();
        }

        if (templateList != null) {
            return templateList;
        }

        templateList = loadTemplate(resourceDirectory);

        templateList.addAll(loadExtendFolder());

        return templateList;
    }

    private List<TemplateGroup> loadExtendFolder() {
        String fileName = IGenerateStorageService.getInstance().getState().getExtendTemplateGroup();

        if (StringUtil.isEmpty(fileName))
            return Collections.emptyList();

        return loadTemplate(new File(fileName));
    }

    @VisibleForTesting
    List<TemplateGroup> loadTemplate(File folder) {
        if (!folder.exists()) {
            return Collections.emptyList();
        }

        List<TemplateGroup> list = new ArrayList<>();
        //File metaFile = new File(resourceDirectory, ".meta.xml");

        for (File file : folder.listFiles()) {
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
