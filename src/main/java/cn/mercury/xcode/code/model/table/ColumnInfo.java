package cn.mercury.xcode.code.model.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.database.model.DasColumn;

import java.util.Map;

/**
 * 列信息
 *
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class ColumnInfo {
    /**
     * 原始对象
     */
    @JsonIgnore
    private DasColumn obj;
    /**
     * 名称
     */
    private String name;
    /**
     * 注释
     */
    private String comment;
    /**
     * 全类型
     */
    private String type;

    private String jdbcType;

    /**
     * 短类型
     */
    private String dbType;
    /**
     * 标记是否为自定义附加列
     */
    private Boolean custom;

    private String defaultValue;

    private boolean unique = false;

    private boolean nullable = true;

    private boolean primaryKey = false;

    private Integer length;

    private Integer precision;

    private Integer scale;

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }



    /**
     * 扩展数据
     */
    private Map<String, Object> ext;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public DasColumn getObj() {
        return obj;
    }

    public void setObj(DasColumn obj) {
        this.obj = obj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
