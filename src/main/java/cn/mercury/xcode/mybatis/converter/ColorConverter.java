package cn.mercury.xcode.mybatis.converter;


import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.mybatis.model.RgbColor;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class ColorConverter extends Converter<RgbColor> {
    @Nullable
    @Override
    public RgbColor fromString(@NotNull String value) {
        try {
            return JsonUtils.fromJson(value, RgbColor.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public String toString(@NotNull RgbColor value) {
        return JsonUtils.toJson(value);
    }
}
