package cn.mercury.xcode.service;

import cn.mercury.xcode.model.SettingsStorage;

/**
 * 导出导入设置服务
 *
 * @author makejava
 * @version 1.0.0
 * @date 2021/08/11 17:24
 */
public interface ExportImportSettingsService {

    /**
     * 导出设置
     *
     * @param settingsStorage 要导出的设置
     */
    void exportConfig(SettingsStorage settingsStorage);

    /**
     * 导入设置
     *
     * @return 设置信息
     */
    SettingsStorage importConfig();

}
