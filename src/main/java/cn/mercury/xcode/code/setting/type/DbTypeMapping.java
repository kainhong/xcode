package cn.mercury.xcode.code.setting.type;

import cn.mercury.xcode.code.setting.IEntry;
import lombok.Data;

import java.util.regex.Pattern;

/**
 * 类型隐射信息
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class DbTypeMapping implements IEntry<DbTypeMapping> {
    /**
     * 匹配类型
     */
    private MatchType matchType;
    /**
     * 列类型
     */
    private String columnType;
    /**
     * java类型
     */
    private String javaType;

    public DbTypeMapping() {

    }

    public DbTypeMapping(String columnType, String javaType) {
        this.matchType = MatchType.REGEX;
        this.columnType = columnType;
        this.javaType = javaType;
    }

    @Override
    public DbTypeMapping defaultVal() {
        return new DbTypeMapping("demo", "java.lang.String");
    }

    private Pattern regex;

    private Pattern getRegex() {
        if (regex != null)
            return regex;
        regex = Pattern.compile(columnType, Pattern.CASE_INSENSITIVE);

        return regex;
    }

    public boolean match(String value) {
        if (matchType == MatchType.ORDINARY) {
            if (value.equalsIgnoreCase(columnType)) {
                return true;
            }
        } else {
            // 不区分大小写的正则匹配模式
            if (getRegex().matcher(value).find()) {
                return true;
            }
        }
        return false;
    }

}
