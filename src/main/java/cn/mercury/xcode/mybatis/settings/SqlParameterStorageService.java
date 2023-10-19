package cn.mercury.xcode.mybatis.settings;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "xCodeSetting", storages = @Storage("x-code-params.xml"))
public class SqlParameterStorageService implements ISqlParameterStorageService {
    SqlParameterStorage storage = new SqlParameterStorage();

    @Override
    public @Nullable SqlParameterStorage getState() {
        return storage;
    }

    @Override
    public void loadState(@NotNull SqlParameterStorage state) {
        if (state == null)
            this.storage = new SqlParameterStorage();
        else {
            this.storage = state;
        }
    }
}
