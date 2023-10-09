package cn.mercury.xcode.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnabledRgbColor {
    private boolean enabledColor;
    private RgbColor rgbColor;
}
