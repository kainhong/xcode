package cn.mercury.xcode.ui;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.service.impl.ClipboardExportImportSettingsServiceImpl;
import cn.mercury.xcode.service.impl.LocalFileExportImportSettingsServiceImpl;
import cn.mercury.xcode.service.impl.NetworkExportImportSettingsServiceImpl;
import cn.mercury.xcode.ui.component.ExportImportComponent;
import cn.mercury.xcode.utils.MessageDialogUtils;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**

 * @version 1.0.0
 * @date 2021/08/07 09:22
 */
public class MainSettingForm implements Configurable, Configurable.Composite, BaseSettings {
    private JLabel versionLabel;
    private JButton resetBtn;
    private JButton pushBtn;
    private JButton pullBtn;
    private JButton exportByNetBtn;
    private JButton importByNetBtn;
    private JButton exportByFileBtn;
    private JButton importByFileBtn;
    private JButton exportByClipboardBtn;
    private JButton importByClipboardBtn;
    private JPanel mainPanel;
    private JTextField userSecureEditor;
    private JTextField authorEditor;
    private JLabel userSecureLabel;
    private JLabel userSecureTitle;

    /**
     * 子配置
     */
    private Configurable[] childConfigurableArray;

    public MainSettingForm() {
    }

    private void initLocalExportEvent() {
        new ExportImportComponent(this.exportByFileBtn, this.importByFileBtn, new LocalFileExportImportSettingsServiceImpl(), this::loadChildSettingsStore);
        new ExportImportComponent(this.exportByNetBtn, this.importByNetBtn, new NetworkExportImportSettingsServiceImpl(), this::loadChildSettingsStore);
        new ExportImportComponent(this.exportByClipboardBtn, this.importByClipboardBtn, new ClipboardExportImportSettingsServiceImpl(), this::loadChildSettingsStore);

        exportByNetBtn.setEnabled(false);
    }

    private void initEvent() {
        this.resetBtn.addActionListener(e -> {
            boolean result = MessageDialogUtils.yesNo("确认恢复默认设置，所有Default分组配置将被重置，并且已删除的默认分组将被还原。确认继续？");
            if (result) {
                // 重置默认值后重新加载配置
                getSettingsStorage().resetDefaultVal();
                this.loadSettingsStore();
                this.loadChildSettingsStore();
            }
        });

        this.userSecureEditor.addCaretListener(e -> {
            String userSecure = this.userSecureEditor.getText();
            if (StringUtils.isEmpty(userSecure)) {
                this.pullBtn.setEnabled(false);
                this.pushBtn.setEnabled(false);
            } else {
                this.pullBtn.setEnabled(true);
                this.pushBtn.setEnabled(true);
            }
        });
    }

    @Override
    public String getDisplayName() {
        return "xCode";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getDisplayName();
    }

    @Override
    public @NotNull Configurable[] getConfigurables() {
        this.childConfigurableArray = new Configurable[]{
                new TypeMapperSettingForm(),
                new TemplateSettingForm(),
                new ColumnConfigSettingForm(),
                new GlobalConfigSettingForm(),
        };
        this.loadChildSettingsStore();
        return this.childConfigurableArray;
    }

    private void loadChildSettingsStore() {
        // 初始装置配置信息
        for (Configurable configurable : this.childConfigurableArray) {
            if (configurable instanceof BaseSettings) {
                ((BaseSettings) configurable).loadSettingsStore();
            }
        }
    }

    @Override
    public @Nullable JComponent createComponent() {
        // TODO 临时隐藏未开发完毕的UI组件
        this.pushBtn.setVisible(false);
        this.pullBtn.setVisible(false);
        this.userSecureEditor.setVisible(false);
        this.userSecureTitle.setVisible(false);
        this.userSecureLabel.setVisible(false);
        // 加载储存数据
        this.loadSettingsStore();
        // 初始化事件
        this.initEvent();
        this.initLocalExportEvent();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(this.authorEditor.getText(), getSettingsStorage().getAuthor())) {
            return true;
        }
        if (!Objects.equals(this.userSecureEditor.getText(), getSettingsStorage().getUserSecure())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        String author = this.authorEditor.getText();
        if (StringUtils.isEmpty(author)) {
            throw new ConfigurationException("作者名称不能为空");
        }
        getSettingsStorage().setAuthor(author);
        String userSecure = this.userSecureEditor.getText();
        getSettingsStorage().setUserSecure(userSecure);
    }

    /**
     * 加载配置信息
     *
     * @param settingsStorage 配置信息
     */
    @Override
    public void loadSettingsStore(SettingsStorage settingsStorage) {
        this.versionLabel.setText(GlobalDict.VERSION);
        this.authorEditor.setText(settingsStorage.getAuthor());
        this.userSecureEditor.setText(settingsStorage.getUserSecure());
        if (StringUtils.isEmpty(settingsStorage.getUserSecure())) {
            this.pullBtn.setEnabled(false);
            this.pushBtn.setEnabled(false);
        } else {
            this.pullBtn.setEnabled(true);
            this.pushBtn.setEnabled(true);
        }
    }
}
