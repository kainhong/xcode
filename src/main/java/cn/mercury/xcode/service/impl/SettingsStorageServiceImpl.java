package cn.mercury.xcode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.model.template.Template;
import cn.mercury.xcode.model.template.TemplateGroup;
import cn.mercury.xcode.service.SettingsStorageService;
import cn.mercury.xcode.utils.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * 设置储存服务实现
 *
 * @author makejava
 * @version 1.0.0
 * @date 2021/08/07 11:32
 */
@State(name = "xCodeSetting", storages = @Storage("x-code-setting.xml"))
public class SettingsStorageServiceImpl implements SettingsStorageService {
    private static final Logger logger = LoggerFactory.getLogger(SettingsStorageServiceImpl.class);

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
        //state.fillDefaultVal();
        //this.settingsStorage = state;


        this.settingsStorage = SettingsStorage.defaultVal();
        try {
            loadExtend();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private void loadExtend() {
        String path = this.settingsStorage.getExtendTemplateFile();
        if (StringUtils.isEmpty(path))
            return;

        String json = FileUtil.readUtf8String(path);
        Map<String, TemplateGroup> extGroups = JSON.parse(json, new TypeReference<Map<String, TemplateGroup>>() {

        });

        Map<String, TemplateGroup> groups = this.settingsStorage.getTemplateGroupMap();

        for (Map.Entry<String, TemplateGroup> kv : extGroups.entrySet()) {
            TemplateGroup g1 = kv.getValue();
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
                groups.put(g1.getName(), g1);
            }
        }

    }

    private boolean handleTemplatePath(String parent, TemplateGroup group) {
        var parentPath = parent.substring(0,parent.lastIndexOf("\\"));

        for (Template tpl : group.getElementList()) {
            var path = tpl.getUri();
            if (path.startsWith("./")) {
                path = parentPath + "/" + path;
            }
            File file = new File(path);

            if (!file.exists() || !file.isFile())
                return false;

            tpl.setUri("file:" + file.getAbsolutePath());
        }
        return true;
    }
}
