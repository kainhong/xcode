package cn.mercury.xcode.model;

import cn.mercury.xcode.code.setting.IEntryGroup;
import lombok.Data;

import java.util.List;

/**
 * 全局配置分组
 *

 * @version 1.0.0
 * @since 2018/07/27 13:10
 */
@Data
public class GlobalConfigEntryGroup implements IEntryGroup<GlobalConfigEntryGroup, GlobalConfig> {
    /**
     * 分组名称
     */
    private String name;
    /**
     * 元素对象集合
     */
    private List<GlobalConfig> elementList;
}
