package cn.mercury.xcode.code.service.storage;

import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.code.setting.type.DbTypeMappingGroup;
import cn.mercury.xcode.model.GlobalConfigGroup;
import com.intellij.ide.fileTemplates.impl.UrlUtil;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(name = "xCodeGenerateSetting", storages = @Storage("x-code-generate.xml"))
public class GenerateStorageService implements IGenerateStorageService {

    GenerateStateStorage stateStorage = new GenerateStateStorage();

    @Override
    public @Nullable GenerateStateStorage getState() {

        return stateStorage;
    }

    @Override
    public void loadState(@NotNull GenerateStateStorage state) {
        this.stateStorage = state;

        try {
            if (state.getGlobalConfigGroup() == null) {
                String json  = UrlUtil.loadText(GenerateStateStorage.class.getResource("/Default/vmConfig.json"));
                var group = JsonUtils.fromJson(json, GlobalConfigGroup.class);
                state.setGlobalConfigGroup(group);
            }
            if (state.getDbTypeMappingGroup() == null) {
                String json  = UrlUtil.loadText(GenerateStateStorage.class.getResource("/Default/dbTypeMapping.json"));
                var group = JsonUtils.fromJson(json, DbTypeMappingGroup.class);
                state.setDbTypeMappingGroup(group);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
