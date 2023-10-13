package cn.mercury.xcode.sql.service.storage;

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
    }
}
