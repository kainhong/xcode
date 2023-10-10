package cn.mercury.xcode.sql.ui;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.sql.setting.TemplateConfiguration;
import cn.mercury.xcode.ui.Notifier;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.openapi.ui.DialogWrapper;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateCodeForm extends DialogWrapper {

    private JPanel panel1;
    private JTextField txtPrefix;
    private JTextField txtModelParentClass;
    private JTextField txtModelImplClass;
    private JCheckBox generateCheckBox;
    private JTextField txtModelModule;
    private JButton btnModelPackage;
    private JTextField txtModelPackage;
    private JTextField txtBaiscPackage;
    private JTextField txtRepositoryModule;
    private JTextField txtServiceModule;
    private JTextField txtManagerModule;
    private JTextField txtControllerModule;
    private JButton btnBasicPackage;
    private JButton btnRepsitoryPackage;
    private JButton btnServicePackage;
    private JButton btnManagerPackage;
    private JButton btnControllerPackage;
    private JTextField txtRepositoryPackge;
    private JTextField txtServicePackage;
    private JTextField txtManagerPackage;
    private JTextField txtControllerPackage;
    private JComboBox cmbTemplate;
    private JCheckBox chkFormat;
    private JCheckBox chkAllYes;
    private JTextField txtMapperFolder;
    private JTextField txtMybatisConfigFile;

    private Project project;

    private List<Module> modules;

    public GenerateCodeForm(@Nullable Project project) {
        super(project);

        this.project = project;

        this.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }

    protected void init() {
        super.init();

        initEvent();

        initData();
    }

    private void initData() {
        var temps = TemplateConfiguration.instance().getTemplateList();

        for (var temp : temps) {
            cmbTemplate.addItem(temp);
        }

        this.modules = Arrays.stream(ModuleManager.getInstance(project).getModules()).collect(Collectors.toList());
    }

    Map<Object, Object> cachedValue = new HashMap<>();

    private void initEvent() {
        btnBasicPackage.addActionListener(e ->
                {
                    String prePackage = txtBaiscPackage.getText();

                    PackageChooserDialog dialog = new PackageChooserDialog("选择基础包", project);

                    dialog.show();

                    var pk = dialog.getSelectedPackage();
                    if( pk == null )
                        return;

                    this.txtBaiscPackage.setText(pk.getQualifiedName());

                    reSetPackageName(prePackage,pk.getQualifiedName());

//                    Module before = (Module) cachedValue.get(txtBaiscPackage);
//                    selectModule(txtBaiscPackage);
//                    Module module = (Module) cachedValue.get(txtBaiscPackage);
//                    if( module != null && module != before){
//
//                    }
                });
    }

    private void reSetPackageName(String oldPackageName,String packageName){

    }

    private Module getSelectModule(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return ModuleManager.getInstance(this.project).findModuleByName(name);
    }

    private void selectModule(JTextField textField) {
        ChooseModulesDialog dialog = new ChooseModulesDialog(this.project, this.modules, "选择模块", "请选择一个模块");
        dialog.setSingleSelectionMode();
        dialog.show();

        Module module = dialog.getChosenElements().stream().findFirst().orElse(null);

        if (module != null) {
            textField.setText(module.getName());
            cachedValue.put(textField, module);
        }
    }

    private void notify(Project project) {
        Notifier.notifyInformation(project,
                GlobalDict.TITLE_INFO, " generate finished");
    }
}
