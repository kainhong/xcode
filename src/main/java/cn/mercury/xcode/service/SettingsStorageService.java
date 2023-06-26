package cn.mercury.xcode.service;

import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.service.impl.SettingsStorageServiceImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;

/**

 * @version 1.0.0
 * @date 2021/08/07 11:55
 */
public interface SettingsStorageService extends PersistentStateComponent<SettingsStorage> {
    /**
     * 获取实例
     *
     * @return {@link SettingsStorageService}
     */
    static SettingsStorageService getInstance() {
        return ApplicationManager.getApplication().getService(SettingsStorageServiceImpl.class);
    }

    /**
     * 获取设置存储
     *
     * @return {@link SettingsStorage}
     */
    static SettingsStorage getSettingsStorage() {
        return getInstance().getState();
    }

    void  reset();

    boolean reloadTemplate(String path);
}
