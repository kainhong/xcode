package cn.mercury.xcode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.mercury.xcode.model.GlobalConfig;
import cn.mercury.xcode.model.GlobalConfigGroup;
import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.model.template.Template;
import cn.mercury.xcode.model.template.TemplateGroup;
import cn.mercury.xcode.service.SettingsStorageService;
import cn.mercury.xcode.utils.JSON;
import cn.mercury.xcode.utils.ResourcesUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设置储存服务实现
 *

 * @version 1.0.0
 * @date 2021/08/07 11:32
 */
@State(name = "xCodeSetting", storages = @Storage("x-code-setting.xml"))
public class SettingsStorageServiceImpl implements SettingsStorageService {
    private static final Logger logger = Logger.getInstance(SettingsStorageServiceImpl.class);

    private SettingsStorage settingsStorage = SettingsStorage.defaultVal();

    /**
     * 获取配置
     *
     * @return 配置对象
     */
    @Nullable
    @Override
    public SettingsStorage getState() {
        return settingsStorage;
    }



    /**
     * 加载配置
     *
     * @param state 配置对象
     */
    @Override
    public void loadState(@NotNull SettingsStorage state) {
        // 加载配置后填充默认值，避免版本升级导致的配置信息不完善问题
        state.fillDefaultVal();
        this.settingsStorage = state;

        //dev
        //this.settingsStorage = SettingsStorage.defaultVal();

        try {
            String path = this.settingsStorage.getExtendTemplateFile();

            loadContext(false);

            loadExtend(path, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadContext(boolean force) {
        Map<String, TemplateGroup> groups = this.settingsStorage.getTemplateGroupMap();
//        for (TemplateGroup group : groups.values()) {
//            for (Template template : group.getElementList()) {
//                if (StringUtils.isNotEmpty(template.getValue()) && !force)
//                    continue;
//
//                String content = ResourcesUtils.readText(template.getUri());
//                template.setValue(content);
//            }
//        }
        Map<String, GlobalConfigGroup> configGroups = this.settingsStorage.getGlobalConfigGroupMap();
        for (GlobalConfigGroup value : configGroups.values()) {
            for (GlobalConfig globalConfig : value.getElementList()) {
                if (StringUtils.isNotEmpty(globalConfig.getValue()) && !force)
                    continue;
                String content = ResourcesUtils.readText(globalConfig.getUri());
                globalConfig.setValue(content);
            }
        }
    }

    private boolean loadExtend(String path, boolean reset) {

        if (StringUtils.isEmpty(path))
            return false;
        File file = new File(path + "/template.json");
        if (!file.exists() || file.isDirectory())
            return false;

        String json = FileUtil.readUtf8String(file);
        Map<String, TemplateGroup> extGroups = JSON.parse(json, new TypeReference<Map<String, TemplateGroup>>() {

        });

        Map<String, TemplateGroup> groups = this.settingsStorage.getTemplateGroupMap();

        if (groups.size() > 0 && extGroups.size() > 0) {
            reset(groups);
        }

        for (Map.Entry<String, TemplateGroup> kv : extGroups.entrySet()) {

            TemplateGroup g1 = kv.getValue();
            if (g1.getName().equals("Spring") || g1.getName().equals("SpringBoot"))
                continue;

            if (!kv.getKey().equals(g1.getName())) {
                g1.setName(kv.getKey());
            }

            if (groups.containsKey(g1.getName())) {
                TemplateGroup g2 = groups.get(g1.getName());
                if (StringUtils.isEmpty(g1.getVersion()) || StringUtils.isEmpty(g2.getVersion())) {
                    logger.info("skip load extend template file:" + g1.getName());
                    continue;
                }
                if (StringUtils.equals(g1.getVersion(), g2.getVersion())) {
                    logger.info("skip load extend template file:" + g1.getName());
                    continue;
                }
            }

            boolean ok = handleTemplatePath(path, g1);
            if (ok) {
                g1.setType("extend");
                groups.put(g1.getName(), g1);
            }
        }
        return true;

    }

    private void reset(Map<String, TemplateGroup> groups) {
        List<String> keys = groups.keySet().stream().collect(Collectors.toList());
        for (String key : keys) {
            var group = groups.get(key);
            if ("extend".equals(group.getType())) {
                groups.remove(key);
            }
        }
    }

    private boolean handleTemplatePath(String parent, TemplateGroup group) {
        for (Template tpl : group.getElementList()) {
            var path = tpl.getUri();
            if (path.startsWith("./")) {
                path = parent + "/" + path;
            }
            File file = new File(path);

            if (!file.exists() || !file.isFile()) {
                logger.error("file not exist:" + file.getAbsolutePath());
                return false;
            }
            tpl.setUri("file:" + file.getAbsolutePath());
            tpl.setValue(FileUtil.readUtf8String(file));
        }
        return true;
    }

    @Override
    public void reset() {
        @Nullable SettingsStorage state = this.getState();
        state.resetDefaultVal();
        this.loadState(state);
    }

    @Override
    public boolean reloadTemplate(String path) {
        try {
            loadContext(true);
            return this.loadExtend(path, true);
        } catch (Exception ex) {
            return false;
        }
    }
}
