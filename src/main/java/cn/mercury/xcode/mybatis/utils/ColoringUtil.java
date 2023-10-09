package cn.mercury.xcode.mybatis.utils;

import cn.mercury.xcode.mybatis.model.ConsoleColorConfig;
import cn.mercury.xcode.mybatis.model.RgbColor;
import cn.mercury.xcode.mybatis.model.ShowColorConfig;
import cn.mercury.xcode.mybatis.settings.LogConfig;
import cn.mercury.xcode.mybatis.settings.LogSettingState;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class ColoringUtil {

    private ColoringUtil() {
    }


    public static ConsoleViewContentType getKeyWordConsoleViewContentTypeFromConfig(Project project) {
        LogConfig service = LogSettingState.getInstance(project);
        Color value = LogUtil.toColor(service.getKeyWordDefCol());
        TextAttributes textAttributes = new TextAttributes(value, null, null, EffectType.BOXED, Font.PLAIN);
        return new ConsoleViewContentType(project.getName() + "BatsLogKeywords", textAttributes);
    }

    public static ConsoleViewContentType getNoteColor(Project project) {
        Color value = new JBColor(Gray._140, Gray._128);
        TextAttributes textAttributes = new TextAttributes(value, null, null, EffectType.BOXED, Font.PLAIN);
        return new ConsoleViewContentType(project.getName() + "BatsLogNotes", textAttributes);
    }

    /**
     * <p>
     * 将配置对象转换为可读json数据
     * </p>
     *
     * @param colorConfigs : 配置列表
     * @return {@link List<ShowColorConfig>}
     * @author PerccyKing
     * @since 2021/08/02 下午 06:45
     */
    public static List<ShowColorConfig> toShowConfig(List<ConsoleColorConfig> colorConfigs) {
        List<ShowColorConfig> configs = new ArrayList<>();
        colorConfigs.forEach(c -> {
            RgbColor bgC = c.getBackgroundColor();
            RgbColor fgC = c.getForegroundColor();
            Color bgColor = new JBColor(new Color(bgC.getR(), bgC.getG(), bgC.getB()), new Color(bgC.getR(), bgC.getG(), bgC.getB()));
            Color fgColor = new JBColor(new Color(fgC.getR(), fgC.getG(), fgC.getB()), new Color(fgC.getR(), fgC.getG(), fgC.getB()));
            ShowColorConfig showColorConfig = new ShowColorConfig(
                    c.getKeyWord(),
                    c.isEnabled(),
                    ColorUtil.toHtmlColor(bgColor),
                    c.isEnableBgColor(),
                    ColorUtil.toHtmlColor(fgColor),
                    c.isEnabledFgColor());
            configs.add(showColorConfig);
        });
        return configs;
    }

    public static List<ConsoleColorConfig> toColorConfig(List<ShowColorConfig> showConfigs) {
        List<ConsoleColorConfig> configs = new ArrayList<>();
        AtomicInteger sort = new AtomicInteger(1);
        showConfigs.forEach(c -> {
            Color bg = ColorUtil.fromHex(c.getBg());
            Color fg = ColorUtil.fromHex(c.getFg());
            ConsoleColorConfig colorConfig = new ConsoleColorConfig(
                    UUID.randomUUID().toString(),
                    sort.get(),
                    c.getKeyword(),
                    new RgbColor(bg.getRed(), bg.getGreen(), bg.getBlue()),
                    c.isBgEnabled(),
                    new RgbColor(fg.getRed(), fg.getGreen(), fg.getBlue()),
                    c.isFgEnabled(),
                    c.isEnabled()
            );
            configs.add(colorConfig);
            sort.getAndIncrement();
        });
        return configs;
    }
}
