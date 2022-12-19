package cn.mercury.xcode.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.mercury.xcode.model.GlobalConfig;
import cn.mercury.xcode.model.template.Template;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * 模板工具，主要用于对模板进行预处理
 *
 * @author makejava
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
            }
            else{
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

    /**
     * 向模板中注入全局变量
     *
     * @param template      模板对象
     * @param globalConfigs 全局变量
     */
    public static String addGlobalConfig(Template template, Collection<GlobalConfig> globalConfigs) {
        if (template == null || StringUtils.isEmpty(template.getUri())) {
            return null;
        }

        String val = getTemplateContent(template);

        // 模板后面添加换行符号，防止在模板末尾添加全局变量导致无法匹配问题
        return addGlobalConfig(val + "\n", globalConfigs);
    }

    public static String getTemplateContent(Template template){
        if(StringUtils.isNotEmpty(template.getValue()))
            return template.getValue();
        return  ResourcesUtils.readText(template.getUri());
    }
    /**
     * 向模板中注入全局变量
     *
     * @param template 单个模板
     */
    public static String addGlobalConfig(Template template) {
        return addGlobalConfig(template, CurrGroupUtils.getCurrGlobalConfigGroup().getElementList());
    }
}
