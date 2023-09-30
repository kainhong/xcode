package cn.mercury.xcode.mybatis.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static cn.mercury.xcode.mybatis.utils.IntellijServiceUtil.getService;

/**
 * @author PerccyKing
 * @version 0.0.1
 * @since 2021/01/06 下午 08:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "xCodeMybatisLogSettingState", storages = {
        @Storage(value = "xCodeMybatisLogSettingState.xml", roamingType = RoamingType.DISABLED)
})
public class LogSettingState extends LogConfig implements PersistentStateComponent<LogSettingState>, Serializable {

    @Nullable
    @Override
    public LogSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LogSettingState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static LogConfig getInstance(Project project) {
        LogConfig config = getService(project, LogSettingState.class);
        if (Boolean.TRUE.equals(config.getUseGlobalConfig())) {
            config = LogGlobalConfigState.getInstance();
        }
        return config;
    }

    public static LogSettingState getDefaultInstance(Project project) {
        return getService(project, LogSettingState.class);
    }
}
