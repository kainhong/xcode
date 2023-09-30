//package cn.mercury.xcode.mybatis.settings;
//
//import cn.com.pism.batslog.constants.BatsLogConstant;
//import cn.com.pism.batslog.ui.SettingForm;
//import com.intellij.openapi.options.SearchableConfigurable;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.Nls;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//
//
//@Slf4j
//public class LogConfigurable implements SearchableConfigurable {
//
//    private SettingForm settingForm;
//
//    @NotNull
//    @Override
//    public String getId() {
//        return BatsLogConstant.BATS_LOG_NAME;
//    }
//
//    @Nls(capitalization = Nls.Capitalization.Title)
//    @Override
//    public String getDisplayName() {
//        return getId();
//    }
//
//    @Nullable
//    @Override
//    public JComponent createComponent() {
//        if (settingForm == null) {
//            settingForm = new SettingForm(null);
//        }
//        return settingForm.getRoot();
//    }
//
//    @Override
//    public boolean isModified() {
//        LogGlobalConfigState service = LogGlobalConfigState.getInstance().getState();
//        //修改后的配置
//        LogConfig tmpConfig = settingForm.getTmpConfig();
//        if (service == null) {
//            return false;
//        }
//        return !service.hash().equals(tmpConfig.hash());
//    }
//
//    @SuppressWarnings("exception")
//    @Override
//    public void apply() {
//        LogGlobalConfigState service = LogGlobalConfigState.getInstance();
//        LogConfig tmpConfig = settingForm.getTmpConfig();
//        service.setSqlPrefix(tmpConfig.getSqlPrefix());
//        service.setParamsPrefix(tmpConfig.getParamsPrefix());
//        service.setTimeFormat(tmpConfig.getTimeFormat());
//        service.setDesensitize(tmpConfig.getDesensitize());
//        service.setPrettyFormat(tmpConfig.getPrettyFormat());
//        service.setParameterized(tmpConfig.getParameterized());
//        service.setToUpperCase(tmpConfig.getToUpperCase());
//        service.setAddTimestamp(tmpConfig.getAddTimestamp());
//        service.setStartWithProject(tmpConfig.getStartWithProject());
//        service.setEnableMixedPrefix(tmpConfig.getEnableMixedPrefix());
//        service.setEncoding(tmpConfig.getEncoding());
//        service.setDbType(tmpConfig.getDbType());
//        service.setKeyWordDefCol(tmpConfig.getKeyWordDefCol());
//        service.setEnabledKeyWordDefCol(tmpConfig.isEnabledKeyWordDefCol());
//        service.setColorConfigs(tmpConfig.getColorConfigs());
//    }
//
//    @Override
//    public void reset() {
//        if (this.settingForm != null) {
//            settingForm.reset();
//        }
//
//    }
//}
