package cn.mercury.xcode.setting.ui;

import com.intellij.openapi.options.Configurable;

public interface BaseSettingsFrom<T> extends Configurable {

    @Override
    default String getHelpTopic() {
        return getDisplayName();
    }

    @Override
    default void reset() {
        loadSettingsStore();
    }

    /**
     * 获取设置信息
     *
     * @return 获取设置信息
     */
    T getSettingsStorage();

    /**
     * 加载配置信息
     */
    default void loadSettingsStore() {
        this.loadSettingsStore(getSettingsStorage());
    }

    /**
     * 加载配置信息
     *
     * @param settingsStorage 配置信息
     */
    void loadSettingsStore(T settingsStorage);
}
