package cn.mercury.xcode.mybatis.settings;


import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.mybatis.converter.ColorConverter;
import cn.mercury.xcode.mybatis.converter.ConsoleColorConfigConverter;
import cn.mercury.xcode.mybatis.converter.DbTypeConverter;
import cn.mercury.xcode.mybatis.model.LogConstant;
import cn.mercury.xcode.mybatis.model.ConsoleColorConfig;
import cn.mercury.xcode.mybatis.model.DbType;
import cn.mercury.xcode.mybatis.model.RgbColor;
import com.intellij.util.xmlb.annotations.OptionTag;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.mercury.xcode.mybatis.logger.GlobalVar.getDefaultColorConfigs;

@Data
public class LogConfig {

    /**
     * 如果使用全局配置，除了数据库配置，都会以全局配置优先
     */
    private Boolean useGlobalConfig = Boolean.TRUE;


    /**
     * 日志SQL行截取前缀
     */
    private String sqlPrefix = LogConstant.SQL_PREFIX;

    /**
     * 日志参数行前缀
     */
    private String paramsPrefix = LogConstant.PARAMS_PREFIX;

    /**
     * 时间格式化
     */
    private String timeFormat = LogConstant.TIME_FORMAT;

    /**
     * 脱敏
     */
    private Boolean desensitize = Boolean.FALSE;

    /**
     * 美化
     */
    private Boolean prettyFormat = Boolean.TRUE;

    /**
     * 参数化
     */
    private Boolean parameterized = Boolean.FALSE;

    /**
     * 关键字转大写
     */
    private Boolean toUpperCase = Boolean.FALSE;

    /**
     * 日志开启时间戳
     */
    private Boolean addTimestamp = Boolean.FALSE;

    private Boolean startWithProject = Boolean.FALSE;

    private Boolean enableMixedPrefix = Boolean.FALSE;

    private String encoding = LogConstant.DEFAULT_ENCODING;

    /**
     * 数据库类型
     */
    @OptionTag(converter = DbTypeConverter.class)
    public DbType dbType = DbType.MYSQL;

    @OptionTag(converter = ColorConverter.class)
    private RgbColor keyWordDefCol = new RgbColor(204, 120, 50);

    /**
     * 是否启用关键字颜色
     */
    private boolean enabledKeyWordDefCol = true;

    @OptionTag(converter = ConsoleColorConfigConverter.class)
    private List<ConsoleColorConfig> colorConfigs = new ArrayList<>();


    public String hash() {
        return sqlPrefix + paramsPrefix + timeFormat + encoding
                + BooleanUtils.toStringYesNo(desensitize)
                + BooleanUtils.toStringYesNo(prettyFormat)
                + BooleanUtils.toStringYesNo(parameterized)
                + BooleanUtils.toStringYesNo(toUpperCase)
                + BooleanUtils.toStringYesNo(addTimestamp)
                + BooleanUtils.toStringYesNo(startWithProject)
                + BooleanUtils.toStringYesNo(enableMixedPrefix)
                + BooleanUtils.toStringYesNo(enabledKeyWordDefCol)
                + dbType.getName()
                + JsonUtils.toJson(keyWordDefCol)
                + JsonUtils.toJson(hashColorConfigs(CollectionUtils.isEmpty(colorConfigs) ? getDefaultColorConfigs() : colorConfigs));
    }

    private String hashColorConfigs(List<ConsoleColorConfig> consoleColorConfigs) {
        ArrayList<ConsoleColorConfig> configs = new ArrayList<>(consoleColorConfigs);
        configs.forEach(item -> item.setId(""));
        return JsonUtils.toJson(configs);
    }
}
