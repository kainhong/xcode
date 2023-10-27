package cn.mercury.xcode.setting.ui;

import cn.mercury.xcode.GlobalDict;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingForm implements Configurable, Configurable.Composite {

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return GlobalDict.TITLE_INFO;
    }

    @Override
    public Configurable @NotNull [] getConfigurables() {
        return new Configurable[0];
    }

    @Override
    public @Nullable JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
