package cn.mercury.xcode.mybatis.converter;

import cn.mercury.xcode.mybatis.model.DbType;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DbTypeConverter extends Converter<DbType> {
    @Nullable
    @Override
    public DbType fromString(@NotNull String value) {
        return DbType.getByName(value);
    }

    @Nullable
    @Override
    public String toString(@NotNull DbType value) {
        return value.getName();
    }
}
