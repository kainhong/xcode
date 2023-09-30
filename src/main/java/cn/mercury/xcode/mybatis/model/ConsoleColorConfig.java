package cn.mercury.xcode.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsoleColorConfig {
    private String id;
    private int sort;
    private String keyWord;
    private RgbColor backgroundColor;

    private boolean enableBgColor = true;

    private RgbColor foregroundColor;

    private boolean enabledFgColor = true;
    private boolean enabled;

    public Object[] toArray() {
        return new Object[]{id, sort, keyWord,
                new EnabledRgbColor(enableBgColor, backgroundColor),
                new EnabledRgbColor(enabledFgColor, foregroundColor), enabled};
    }

    public ConsoleColorConfig toConfig(Object[] objects) {
        ConsoleColorConfig consoleColorConfig = new ConsoleColorConfig();
        consoleColorConfig.setId((String) objects[0]);
        consoleColorConfig.setSort((int) objects[1]);
        consoleColorConfig.setKeyWord((String) objects[2]);
        EnabledRgbColor bgColor = (EnabledRgbColor) objects[3];
        consoleColorConfig.setBackgroundColor(bgColor.getRgbColor());
        consoleColorConfig.setEnableBgColor(bgColor.isEnabledColor());
        EnabledRgbColor fgColor = (EnabledRgbColor) objects[4];
        consoleColorConfig.setForegroundColor(fgColor.getRgbColor());
        consoleColorConfig.setEnabledFgColor(fgColor.isEnabledColor());
        consoleColorConfig.setEnabled((Boolean) objects[5]);
        return consoleColorConfig;
    }
}
