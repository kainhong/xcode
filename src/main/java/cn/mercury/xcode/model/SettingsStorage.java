package cn.mercury.xcode.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.model.table.ColumnConfig;
import cn.mercury.xcode.model.table.ColumnConfigGroup;
import cn.mercury.xcode.model.table.ColumnConfigType;
import cn.mercury.xcode.model.template.TemplateGroup;
import cn.mercury.xcode.model.type.TypeMapper;
import cn.mercury.xcode.model.type.TypeMapperGroup;
import cn.mercury.xcode.utils.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.ide.fileTemplates.impl.UrlUtil;
import com.intellij.util.ExceptionUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsStorage {
    /**
     * 返回默认值，不使用静态常量，防止默认值别篡改
     *
     * @return 储存对象
     */
    public static SettingsStorage defaultVal() {
        try {

            // 从配置文件中加载配置
            String json = UrlUtil.loadText(SettingsStorage.class.getResource("/defaultConfig.json"));
            return JSON.parse(json, SettingsStorage.class);
        } catch (Exception e) {
            ExceptionUtil.rethrow(e);
        }
        // 配置文件加载失败，直接创建配置
        SettingsStorage storage = new SettingsStorage();
        storage.author = GlobalDict.AUTHOR;
        storage.version = GlobalDict.VERSION;
        storage.userSecure = "";
        // 默认分组名称
        storage.currTypeMapperGroupName = GlobalDict.DEFAULT_GROUP_NAME;
        storage.currTemplateGroupName = GlobalDict.DEFAULT_TEMPLATE_NAME;
        storage.currColumnConfigGroupName = GlobalDict.DEFAULT_GROUP_NAME;
        storage.currGlobalConfigGroupName = GlobalDict.DEFAULT_GROUP_NAME;
        // 默认配置信息
        storage.typeMapperGroupMap = new HashMap<>(16);
        TypeMapperGroup typeMapperGroup = new TypeMapperGroup();
        typeMapperGroup.setName(GlobalDict.DEFAULT_GROUP_NAME);
        typeMapperGroup.setElementList(Arrays.asList(new TypeMapper("varchar", "java.lang.String"), new TypeMapper("varchar\\(\\)", "java.lang.String")));
        storage.typeMapperGroupMap.put(GlobalDict.DEFAULT_GROUP_NAME, typeMapperGroup);

        ColumnConfigGroup columnConfigGroup = new ColumnConfigGroup();
        columnConfigGroup.setName(GlobalDict.DEFAULT_GROUP_NAME);
        columnConfigGroup.setElementList(Arrays.asList(new ColumnConfig("disable", ColumnConfigType.BOOLEAN), new ColumnConfig("operator", ColumnConfigType.SELECT, "insert,update,delete,select")));
        storage.columnConfigGroupMap = new HashMap<>(16);
        storage.columnConfigGroupMap.put(GlobalDict.DEFAULT_GROUP_NAME, columnConfigGroup);

//        TemplateGroup templateGroup = new TemplateGroup();
//        templateGroup.setName(GlobalDict.DEFAULT_TEMPLATE_NAME);
//        templateGroup.setElementList(Arrays.asList(new Template("demo", "template"), new Template("entity.java", "public")));
        storage.templateGroupMap = new HashMap<>(16);
//        storage.templateGroupMap.put(GlobalDict.DEFAULT_TEMPLATE_NAME, templateGroup);

        GlobalConfigGroup globalConfigGroup = new GlobalConfigGroup();
        globalConfigGroup.setName(GlobalDict.DEFAULT_GROUP_NAME);
        globalConfigGroup.setElementList(Arrays.asList(new GlobalConfig("test", "abc"), new GlobalConfig("demo", "value")));
        storage.globalConfigGroupMap = new HashMap<>(16);
        storage.globalConfigGroupMap.put(GlobalDict.DEFAULT_GROUP_NAME, globalConfigGroup);
        return storage;
    }

    /**
     * 重置为默认值
     */
    public void resetDefaultVal() {
        SettingsStorage defaultVal = defaultVal();
        this.setAuthor(defaultVal.getAuthor());
        this.setVersion(defaultVal.getVersion());
        this.setCurrColumnConfigGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        this.getColumnConfigGroupMap().put(GlobalDict.DEFAULT_GROUP_NAME, defaultVal.getColumnConfigGroupMap().get(GlobalDict.DEFAULT_GROUP_NAME));
        this.setCurrTemplateGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        this.getTemplateGroupMap().put(GlobalDict.DEFAULT_TEMPLATE_NAME, defaultVal.getTemplateGroupMap().get(GlobalDict.DEFAULT_TEMPLATE_NAME));
        this.setCurrGlobalConfigGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        this.getGlobalConfigGroupMap().put(GlobalDict.DEFAULT_GROUP_NAME, defaultVal.getGlobalConfigGroupMap().get(GlobalDict.DEFAULT_GROUP_NAME));
        this.setCurrTypeMapperGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        this.getTypeMapperGroupMap().put(GlobalDict.DEFAULT_GROUP_NAME, defaultVal.getTypeMapperGroupMap().get(GlobalDict.DEFAULT_GROUP_NAME));
        // 恢复已被删除的分组
        defaultVal.getTemplateGroupMap().forEach((k, v) -> {
            getTemplateGroupMap().put(k, v);
        });
        defaultVal.getGlobalConfigGroupMap().forEach((k, v) -> {
            getGlobalConfigGroupMap().put(k, v);
        });
        defaultVal.getColumnConfigGroupMap().forEach((k, v) -> {
            getColumnConfigGroupMap().put(k, v);
        });
        defaultVal.getTypeMapperGroupMap().forEach((k, v) -> {
            getTypeMapperGroupMap().put(k, v);
        });

    }

    /**
     * 作者
     */
    private String author;
    /**
     * 版本号
     */
    private String version;
    /**
     * 用户密钥
     */
    private String userSecure;
    /**
     * 当前类型映射组名
     */
    private String currTypeMapperGroupName;
    /**
     * 类型映射组
     */
    @JsonProperty("typeMapper")
    private Map<String, TypeMapperGroup> typeMapperGroupMap;
    /**
     * 当前模板组名
     */
    private String currTemplateGroupName;
    /**
     * 模板组
     */
    @JsonProperty("template__")
    private Map<String, TemplateGroup> templateGroupMap;
    /**
     * 当前配置表组名
     */
    private String currColumnConfigGroupName;
    /**
     * 配置表组
     */
    @JsonProperty("columnConfig")
    private Map<String, ColumnConfigGroup> columnConfigGroupMap;
    /**
     * 当前全局配置组名
     */
    private String currGlobalConfigGroupName;
    /**
     * 全局配置组
     */
    @JsonProperty("globalConfig")
    private Map<String, GlobalConfigGroup> globalConfigGroupMap;

    /**
     * 扩展模板文件路径
     */
    private String extendTemplateFile;

    public void fillDefaultVal() {
        SettingsStorage defaultVal = defaultVal();
        if (CollectionUtil.isEmpty(this.typeMapperGroupMap)) {
            this.typeMapperGroupMap = defaultVal.getTypeMapperGroupMap();
        }
        if (!this.typeMapperGroupMap.containsKey(GlobalDict.DEFAULT_GROUP_NAME)) {
            this.typeMapperGroupMap.put(GlobalDict.DEFAULT_GROUP_NAME, defaultVal.getTypeMapperGroupMap().get(GlobalDict.DEFAULT_GROUP_NAME));
        }
        if (StringUtils.isEmpty(this.currTypeMapperGroupName)) {
            this.setCurrTypeMapperGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        }


        if (CollectionUtil.isEmpty(this.templateGroupMap)) {
            this.templateGroupMap = defaultVal.getTemplateGroupMap();
        }
        if (this.templateGroupMap == null)
            this.templateGroupMap = new HashMap<>();

        if (!this.templateGroupMap.containsKey(GlobalDict.DEFAULT_TEMPLATE_NAME)) {
            this.templateGroupMap.put(GlobalDict.DEFAULT_TEMPLATE_NAME, defaultVal.getTemplateGroupMap().get(GlobalDict.DEFAULT_TEMPLATE_NAME));
        }
        if (StringUtils.isEmpty(this.currTemplateGroupName)) {
            this.setCurrTemplateGroupName(GlobalDict.DEFAULT_TEMPLATE_NAME);
        }


        if (CollectionUtil.isEmpty(this.columnConfigGroupMap)) {
            this.columnConfigGroupMap = defaultVal.getColumnConfigGroupMap();
        }
        if (!this.columnConfigGroupMap.containsKey(GlobalDict.DEFAULT_GROUP_NAME)) {
            this.columnConfigGroupMap.put(GlobalDict.DEFAULT_GROUP_NAME, defaultVal.getColumnConfigGroupMap().get(GlobalDict.DEFAULT_GROUP_NAME));
        }
        if (StringUtils.isEmpty(this.currColumnConfigGroupName)) {
            this.setCurrColumnConfigGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        }


        if (CollectionUtil.isEmpty(this.globalConfigGroupMap)) {
            this.globalConfigGroupMap = defaultVal.getGlobalConfigGroupMap();
        }
        if (!this.globalConfigGroupMap.containsKey(GlobalDict.DEFAULT_GROUP_NAME)) {
            this.globalConfigGroupMap.put(GlobalDict.DEFAULT_GROUP_NAME, defaultVal.getGlobalConfigGroupMap().get(GlobalDict.DEFAULT_GROUP_NAME));
        }
        if (StringUtils.isEmpty(this.currGlobalConfigGroupName)) {
            this.setCurrGlobalConfigGroupName(GlobalDict.DEFAULT_GROUP_NAME);
        }

        if (StringUtils.isEmpty(this.version)) {
            this.setVersion(defaultVal.getVersion());
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserSecure() {
        return userSecure;
    }

    public void setUserSecure(String userSecure) {
        this.userSecure = userSecure;
    }

    public String getCurrTypeMapperGroupName() {
        return currTypeMapperGroupName;
    }

    public void setCurrTypeMapperGroupName(String currTypeMapperGroupName) {
        this.currTypeMapperGroupName = currTypeMapperGroupName;
    }

    public Map<String, TypeMapperGroup> getTypeMapperGroupMap() {
        return typeMapperGroupMap;
    }

    public void setTypeMapperGroupMap(Map<String, TypeMapperGroup> typeMapperGroupMap) {
        this.typeMapperGroupMap = typeMapperGroupMap;
    }

    public String getCurrTemplateGroupName() {
        return currTemplateGroupName;
    }

    public void setCurrTemplateGroupName(String currTemplateGroupName) {
        this.currTemplateGroupName = currTemplateGroupName;
    }

    public Map<String, TemplateGroup> getTemplateGroupMap() {
        if (templateGroupMap == null)
            templateGroupMap = new HashMap<>();

        return templateGroupMap;
    }

    public void setTemplateGroupMap(Map<String, TemplateGroup> templateGroupMap) {
        this.templateGroupMap = templateGroupMap;
    }

    public String getCurrColumnConfigGroupName() {
        return currColumnConfigGroupName;
    }

    public void setCurrColumnConfigGroupName(String currColumnConfigGroupName) {
        this.currColumnConfigGroupName = currColumnConfigGroupName;
    }

    public Map<String, ColumnConfigGroup> getColumnConfigGroupMap() {
        return columnConfigGroupMap;
    }

    public void setColumnConfigGroupMap(Map<String, ColumnConfigGroup> columnConfigGroupMap) {
        this.columnConfigGroupMap = columnConfigGroupMap;
    }

    public String getCurrGlobalConfigGroupName() {
        return currGlobalConfigGroupName;
    }

    public void setCurrGlobalConfigGroupName(String currGlobalConfigGroupName) {
        this.currGlobalConfigGroupName = currGlobalConfigGroupName;
    }

    public Map<String, GlobalConfigGroup> getGlobalConfigGroupMap() {
        return globalConfigGroupMap;
    }

    public void setGlobalConfigGroupMap(Map<String, GlobalConfigGroup> globalConfigGroupMap) {
        this.globalConfigGroupMap = globalConfigGroupMap;
    }

    public String getExtendTemplateFile() {
        return extendTemplateFile;
    }

    public void setExtendTemplateFile(String extendTemplateFile) {
        this.extendTemplateFile = extendTemplateFile;
    }
}
