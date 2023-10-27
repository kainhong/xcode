package cn.mercury.xcode.code.service.storage;

import cn.mercury.xcode.code.setting.type.DbTypeMappingGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;

public interface IGenerateStorageService extends PersistentStateComponent<GenerateStateStorage> {


    static IGenerateStorageService getInstance() {
        return ApplicationManager.getApplication().getService(GenerateStorageService.class);
    }

    static GenerateSetting getGenerateSetting() {
        if (getInstance().getState() == null)
            return null;

        return getInstance().getState().getGenerateSetting();
    }


    static DbTypeMappingGroup getDbTypeMappingGroup() {
        if (getInstance().getState() == null)
            return null;

        return getInstance().getState().getDbTypeMappingGroup();
    }

}
