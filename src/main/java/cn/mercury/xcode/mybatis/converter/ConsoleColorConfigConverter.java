package cn.mercury.xcode.mybatis.converter;


import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.mybatis.model.ConsoleColorConfig;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class ConsoleColorConfigConverter extends Converter<List<ConsoleColorConfig>> {
    @Nullable
    @Override
    public List<ConsoleColorConfig> fromString(@NotNull String value) {
        try {
            return JsonUtils.fromListJson(value, ConsoleColorConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public String toString(@NotNull List<ConsoleColorConfig> value) {
        return JsonUtils.toJson(value);
    }
}
