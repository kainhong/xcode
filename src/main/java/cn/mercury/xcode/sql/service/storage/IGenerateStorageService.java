package cn.mercury.xcode.sql.service.storage;

import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.service.SettingsStorageService;
import cn.mercury.xcode.service.impl.SettingsStorageServiceImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;

public interface IGenerateStorageService extends PersistentStateComponent<GenerateStateStorage> {


    static IGenerateStorageService getInstance() {
        return ApplicationManager.getApplication().getService(GenerateStorageService.class);
    }

}
