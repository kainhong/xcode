package cn.mercury.xcode.model.type;

import cn.mercury.xcode.model.AbstractItem;
import lombok.Data;

/**
 * 类型隐射信息
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class TypeMapper implements AbstractItem<TypeMapper> {
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

    public TypeMapper(){

    }
    public TypeMapper(String columnType, String javaType) {
        this.matchType = MatchType.REGEX;
        this.columnType = columnType;
        this.javaType = javaType;
    }

    @Override
    public TypeMapper defaultVal() {
        return new TypeMapper("demo", "java.lang.String");
    }
}
