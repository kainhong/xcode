package cn.mercury.xcode.mybatis.settings;

import cn.mercury.xcode.mybatis.model.ConsoleColorConfig;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

import static cn.mercury.xcode.mybatis.logger.GlobalVar.getDefaultColorConfigs;
import static cn.mercury.xcode.mybatis.utils.IntellijServiceUtil.getService;


@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LogGlobalConfigState", storages = @Storage(value = "$APP_CONFIG$/xCode/mybatis/LogGlobal.xml"))
public class LogGlobalConfigState extends LogConfig implements PersistentStateComponent<LogGlobalConfigState>, Serializable {
    @Nullable
    @Override
    public LogGlobalConfigState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LogGlobalConfigState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static LogGlobalConfigState getInstance() {
        return getService(null, LogGlobalConfigState.class);
    }

    @Override
    public Boolean getUseGlobalConfig() {
        return Boolean.FALSE;
    }

    @Override
    public List<ConsoleColorConfig> getColorConfigs() {
        List<ConsoleColorConfig> colorConfigs = super.getColorConfigs();
        if (colorConfigs == null || colorConfigs.isEmpty()) {
            return getDefaultColorConfigs();
        }
        return super.getColorConfigs();
    }
}
