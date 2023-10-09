package cn.mercury.xcode.mybatis.utils;


import cn.mercury.xcode.mybatis.model.ConsoleColorConfig;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleColorConfigUtil {
    private ConsoleColorConfigUtil() {
    }

    public static Map<String, ConsoleViewContentType> toConsoleViewContentTypeMap(Project project, List<ConsoleColorConfig> colorConfigs) {
        Map<String, ConsoleViewContentType> map = new HashMap<>(0);
        ConsoleViewContentType normalOutput = ConsoleViewContentType.NORMAL_OUTPUT;
        for (ConsoleColorConfig colorConfig : colorConfigs) {
            TextAttributes textAttributes = new TextAttributes(
                    colorConfig.isEnabledFgColor() ? LogUtil.toColor(colorConfig.getForegroundColor()) : normalOutput.getAttributes().getForegroundColor(),
                    colorConfig.isEnableBgColor() ? LogUtil.toColor(colorConfig.getBackgroundColor()) : normalOutput.getAttributes().getBackgroundColor(),
                    null, EffectType.BOXED, Font.PLAIN);
            if (StringUtils.isNotBlank(colorConfig.getKeyWord()) && colorConfig.isEnabled()) {
                String baseName = project == null ? "" : project.getName();
                map.put(colorConfig.getKeyWord(), new ConsoleViewContentType(baseName + "ConsoleColor" + colorConfig.getKeyWord(), textAttributes));
            }
        }
        return map;
    }
}
