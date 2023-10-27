package cn.mercury.xcode.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.mercury.xcode.code.service.storage.IGenerateStorageService;
import cn.mercury.xcode.model.GlobalConfig;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * 模板工具，主要用于对模板进行预处理
 *
 * @version 1.0.0
 * @since 2018/09/01 15:07
 */
public final class TemplateUtils {
    /**
     * 不允许创建实例对象
     */
    private TemplateUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 向模板中注入全局变量
     *
     * @param template      模板
     * @param globalConfigs 全局变量
     * @return 处理好的模板
     */
    public static String addGlobalConfig(String template, Collection<GlobalConfig> globalConfigs) {
        if (CollectionUtil.isEmpty(globalConfigs)) {
            return template;
        }
        for (GlobalConfig globalConfig : globalConfigs) {
            String name = globalConfig.getName();
            String value = null;
            if (StringUtils.isNotEmpty(globalConfig.getValue())) {
                // 正则被替换字符转义处理
                value = globalConfig.getValue();
            } else {
                value = ResourcesUtils.readText(globalConfig.getUri());
            }

            value = value.replace("\\", "\\\\").replace("$", "\\$");
            // 将不带{}的变量加上{}
            template = template.replaceAll("\\$!?" + name + "(\\W)", "\\$!{" + name + "}$1");
            // 统一替换
            template = template.replaceAll("\\$!?\\{" + name + "}", value);

        }
        return template;
    }


    public static String parseTemplate(String template) {
        return addGlobalConfig(template, IGenerateStorageService.getInstance().getState().getGlobalConfigGroup().getElementList());
    }
}
