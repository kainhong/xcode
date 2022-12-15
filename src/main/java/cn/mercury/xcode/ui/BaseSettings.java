package cn.mercury.xcode.ui;

import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.service.SettingsStorageService;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

/**
 * @author makejava
 * @version 1.0.0
 * @since 2021/08/07 19:42
 */
public interface BaseSettings extends Configurable {
    /**
     * 帮助提示信息
     *
     * @return 提示信息
     */
    @Nullable
    @Override
    default String getHelpTopic() {
        return getDisplayName();
    }

    /**
     * 重置设置
     */
    @Override
    default void reset() {
        loadSettingsStore();
    }

    /**
     * 获取设置信息
     *
     * @return 获取设置信息
     */
    default SettingsStorage getSettingsStorage() {
        return SettingsStorageService.getSettingsStorage();
    }

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
    void loadSettingsStore(SettingsStorage settingsStorage);

}
