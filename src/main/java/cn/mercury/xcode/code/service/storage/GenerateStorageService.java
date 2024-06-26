package cn.mercury.xcode.code.service.storage;

import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.code.setting.type.DbTypeMappingEntryGroup;
import cn.mercury.xcode.model.GlobalConfigEntryGroup;
import com.intellij.ide.fileTemplates.impl.UrlUtil;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(name = "xCodeGenerateSetting", storages = @Storage("x-code-generate.xml"))
public class GenerateStorageService implements IGenerateStorageService {

    GenerateStateStorage stateStorage;

    @Override
    public @Nullable GenerateStateStorage getState() {
        if (stateStorage == null) {
            stateStorage = new GenerateStateStorage();
            load(stateStorage);
        }
        return stateStorage;
    }

    @Override
    public void loadState(@NotNull GenerateStateStorage state) {
        this.stateStorage = state;

        load(state);
    }

    private void load(@NotNull GenerateStateStorage state) {
        try {
            if (state.getGenerateSetting() == null)
                state.setGenerateSetting(new GenerateSetting());

            if (state.getGlobalConfigGroup() == null || CollectionUtils.isEmpty(state.getGlobalConfigGroup().getItems())) {
                String json = UrlUtil.loadText(GenerateStateStorage.class.getResource("/Default/vmConfig.json"));
                var group = JsonUtils.fromJson(json, GlobalConfigEntryGroup.class);
                state.setGlobalConfigGroup(group);
            }

            if (state.getDbTypeMappingGroup() == null || CollectionUtils.isEmpty(state.getDbTypeMappingGroup().getItems())) {
                String json = UrlUtil.loadText(GenerateStateStorage.class.getResource("/Default/dbTypeMapping.json"));
                var group = JsonUtils.fromJson(json, DbTypeMappingEntryGroup.class);
                state.setDbTypeMappingGroup(group);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
