package cn.mercury.xcode.mybatis.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;

public interface ISqlParameterStorageService extends PersistentStateComponent<SqlParameterStorage> {

    static ISqlParameterStorageService getInstance() {
        return ApplicationManager.getApplication().getService(SqlParameterStorageService.class);
    }

    static SqlParameterStorage getStorage() {
        return ApplicationManager.getApplication().getService(SqlParameterStorageService.class).getState();
    }
}
