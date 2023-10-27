package cn.mercury.xcode.code.ui;

import cn.hutool.core.io.FileUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.Notifier;
import cn.mercury.xcode.code.generate.GenerateOptions;
import cn.mercury.xcode.code.service.CodeGenerateService;
import cn.mercury.xcode.code.service.storage.IGenerateStorageService;
import cn.mercury.xcode.code.setting.Template;
import cn.mercury.xcode.code.setting.TemplateConfiguration;
import cn.mercury.xcode.code.setting.TemplateGroup;
import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.mybatis.language.dom.model.Configuration;
import cn.mercury.xcode.mybatis.utils.MapperUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateCodeForm extends DialogWrapper {

    static final Logger logger = Logger.getInstance(GenerateCodeForm.class);

    private final List<DbTable> tables;
    private JPanel panel1;
    private JTextField txtPrefix;
    private JTextField txtModelParentClass;
    private JTextField txtModelImplClass;
    private JCheckBox chkModel;
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
    private JButton btnSelectModule;
    private JTextField txtModule;
    private JCheckBox chkRepository;
    private JCheckBox chkService;
    private JCheckBox chekManager;
    private JCheckBox chekController;
    private JComboBox cmbConfiguration;
    private JCheckBox chkAllNo;
    private JButton btnReset;

    private Project project;

    private List<Module> modules;

    final List<String> moduleNames = Arrays.asList("model", "repository", "service", "manager", "controller");

    private Map<String, JTextField> packageTextFields = new HashMap<>();

    private Map<String, JTextField> moduleTextFields = new HashMap<>();

    private Map<String, JCheckBox> moduleCheckboxes = new HashMap<>();

    private String rootOutputPath;

    private boolean isOldVersion = false;

    TemplateGroup templateGroup;

    Collection<Configuration> configurations;

    private int returnFlag = 0;

    private boolean isOracleDataSource = false;

    public int getReturnFlag() {
        return returnFlag;
    }

    public GenerateCodeForm(@Nullable Project project,List<DbTable> tables) {
        super(project);

        this.tables =  tables;

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

        loadState();

        this.panel1.setPreferredSize(new Dimension(800, 600));
    }

    private void initData() {
        packageTextFields.put("model", txtModelPackage);
        packageTextFields.put("repository", txtRepositoryPackge);
        packageTextFields.put("service", txtServicePackage);
        packageTextFields.put("manager", txtManagerPackage);
        packageTextFields.put("controller", txtControllerPackage);

        moduleTextFields.put("model", txtModelModule);
        moduleTextFields.put("repository", txtRepositoryModule);
        moduleTextFields.put("service", txtServiceModule);
        moduleTextFields.put("manager", txtManagerModule);
        moduleTextFields.put("controller", txtControllerModule);


        moduleCheckboxes.put("model", chkModel);
        moduleCheckboxes.put("repository", chkRepository);
        moduleCheckboxes.put("service", chkService);
        moduleCheckboxes.put("manager", chekManager);
        moduleCheckboxes.put("controller", chekController);

        var temps = TemplateConfiguration.instance().getTemplateList();

        for (var temp : temps) {
            cmbTemplate.addItem(temp.getName());
        }

        Runnable action = () -> {
            String templateGroupName = (String) cmbTemplate.getSelectedItem();
            this.isOldVersion = "spring".equalsIgnoreCase(templateGroupName);
            this.templateGroup = TemplateConfiguration.instance()
                    .getTemplateList().stream()
                    .filter(item -> item.getName().equals(templateGroupName))
                    .findFirst().orElse(null);
        };

        if (temps.size() > 0) {
            cmbTemplate.setSelectedIndex(0);
            action.run();
        }

        cmbTemplate.addActionListener(e -> {
            action.run();
            String prePackage = txtBaiscPackage.getText();

            if (StringUtils.isNotEmpty(prePackage))
                reSetPackageName(prePackage, prePackage);
        });

        this.modules = Arrays.stream(ModuleManager.getInstance(project).getModules())
                .collect(Collectors.toList());

        configurations = MapperUtils.getMybatisConfigurations(this.project);

        if (this.tables != null && this.tables.size() > 0) {
            this.isOracleDataSource = DatasourceHelper.isOracle(this.tables.get(0));
        }

    }

    private void resetState() {
        IGenerateStorageService storageService = IGenerateStorageService.getInstance();

        @Nullable var setting = IGenerateStorageService.getGenerateSetting();

        if (setting == null)
            return;

        setting.setTemplateGroup("");
        setting.setPrefix("");
        setting.setBasePackage("");
        setting.setBaseModule("");
        setting.setModelModule("");
        setting.setModelPackage("");
        setting.setModelEnable(false);
        setting.setRepositoryModule("");
        setting.setRepositoryPackage("");
        setting.setRepositoryEnable(false);
        setting.setServiceModule("");
        setting.setServicePackage("");
        setting.setServiceEnable(false);
        setting.setManagerModule("");
        setting.setManagerPackage("");
        setting.setManagerEnable(false);
        setting.setControllerModule("");
        setting.setControllerPackage("");
        setting.setControllerEnable(false);
        setting.setMybatisConfigurationFile("");
        setting.setMybatisMapperFile("");
    }

    private void loadState() {

        @Nullable var state = IGenerateStorageService.getGenerateSetting();

        if (state == null)
            return;
        if (StringUtils.isNotEmpty(state.getTemplateGroup()))
            cmbTemplate.setSelectedItem(state.getTemplateGroup());

        if (StringUtils.isEmpty(state.getBasePackage()))
            return;
        txtBaiscPackage.setText(state.getBasePackage());

        txtModule.setText(state.getBaseModule());
        Module basicModule = getModule(state.getBaseModule());

        loadMybatisConfigurationFiles(basicModule);

        if (StringUtils.isNotEmpty(state.getPrefix()))
            this.txtPrefix.setText(state.getPrefix());

        try {
            Map<String, Object> params = JsonUtils.fromJson(JsonUtils.toJson(state), Map.class);
            for (String name : moduleNames) {
                loadState(name, params);
            }

        } catch (IOException e) {

        }
    }

    private void loadState(String name, Map<String, Object> state) {
        String moduleName = (String) state.get(name + "Module");
        if (moduleName == null)
            return;

        Module module = getModule(moduleName);
        if (module == null)
            return;

        var chk = moduleCheckboxes.get(name);
        chk.setSelected((Boolean) state.get(name + "Enable"));

        var txtModule = moduleTextFields.get(name);
        txtModule.setText(module.getName());

        cachedValue.put(txtModule, module);

        var txtPackage = packageTextFields.get(name);
        txtPackage.setText((String) state.get(name + "Package"));


    }

    private void saveState() {

        @Nullable var state = IGenerateStorageService.getGenerateSetting();

        if (state == null)
            return;

        state.setTemplateGroup((String) cmbTemplate.getSelectedItem());
        state.setPrefix(txtPrefix.getText());
        state.setBasePackage(txtBaiscPackage.getText());
        state.setBaseModule(txtModule.getText());
        state.setModelModule(txtModelModule.getText());
        state.setModelPackage(txtModelPackage.getText());
        state.setModelEnable(chkModel.isSelected());
        state.setRepositoryModule(txtRepositoryModule.getText());
        state.setRepositoryPackage(txtRepositoryPackge.getText());
        state.setRepositoryEnable(chkRepository.isSelected());
        state.setServiceModule(txtServiceModule.getText());
        state.setServicePackage(txtServicePackage.getText());
        state.setServiceEnable(chkService.isSelected());
        state.setManagerModule(txtManagerModule.getText());
        state.setManagerPackage(txtManagerPackage.getText());
        state.setManagerEnable(chekManager.isSelected());
        state.setControllerModule(txtControllerModule.getText());
        state.setControllerPackage(txtControllerPackage.getText());
        state.setControllerEnable(chekController.isSelected());
        state.setMybatisConfigurationFile((String) cmbConfiguration.getSelectedItem());
        state.setMybatisMapperFile(txtMapperFolder.getText());

    }

    Map<Object, Object> cachedValue = new HashMap<>();

    private void initEvent() {
        btnBasicPackage.addActionListener(e -> {
            String prePackage = txtBaiscPackage.getText();
            try {
                PackageChooserDialog dialog = new PackageChooserDialog("选择基础包", project);

                dialog.show();

                var pk = dialog.getSelectedPackage();
                if (pk == null)
                    return;

                this.txtBaiscPackage.setText(pk.getQualifiedName());

                reSetPackageName(prePackage, pk.getQualifiedName());
            } catch (Exception ex) {

            }
        });


        btnSelectModule.addActionListener(e -> {
            Module module = selectModule(txtModule, "");
            if (module == null)
                return;

            this.rootOutputPath = getModulePath(module, null);

            setModule(module.getName());

            loadMybatisConfigurationFiles(module);
        });

        btnModelPackage.addActionListener(e -> {
            selectModule(txtModelModule, "model");
        });
        btnRepsitoryPackage.addActionListener(e -> {
            selectModule(txtRepositoryModule, "repository");
        });
        btnServicePackage.addActionListener(e -> {
            selectModule(txtServiceModule, "service");
        });

        btnManagerPackage.addActionListener(e -> selectModule(txtManagerModule, "manager"));

        btnControllerPackage.addActionListener(e -> {
            selectModule(txtControllerModule, "controller");
        });

        this.btnReset.addActionListener((e) -> {
            int flag = Messages.showYesNoDialog("Do you want to continue?", "Confirmation", Messages.getQuestionIcon());
            if (flag == Messages.NO) {
                return;
            }

            this.resetState();

            this.returnFlag = 9;
            this.close(0);
        });

        txtRepositoryModule.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                //System.out.println(txtRepositoryModule.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void loadMybatisConfigurationFiles(final Module module) {
        if (module == null)
            return;

        List<Configuration> items = configurations.stream().filter(r -> {
            String filePath = r.getXmlTag().getContainingFile().getVirtualFile().getPath();
            String modulePath = getModulePath(module, null);
            filePath = filePath.replace("\\", "/");
            modulePath = modulePath.replace("\\", "/");

            return filePath.contains(modulePath);
        }).collect(Collectors.toList());

        cmbConfiguration.removeAllItems();

        for (Configuration configuration : items) {
            PsiFile vsfile = configuration.getXmlTag().getContainingFile();
            String name = vsfile.getVirtualFile().getName();
            //Module psModule = ModuleUtilCore.findModuleForPsiElement(vsfile);

            cmbConfiguration.addItem(name);
        }
    }


    @Override
    protected void doOKAction() {
        if (!this.validateSettings())
            return;

        try {
            Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            // 设置光标
            this.panel1.setCursor(waitCursor);

            generateCode();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // 设置光标
            this.panel1.setCursor(Cursor.getDefaultCursor());

            this.close(0);
        }
    }

    private void generateCode() {
        List<Template> templates = getAllSelectedTemplate();
//        TableInfo tableInfo = tableInfoService.getTableInfo(cacheDataUtils.getDbTableList().get(0));
//
//        tableInfo.setSavePath(this.rootOutputPath);
//
//        tableInfo.setSavePackageName(txtBaiscPackage.getText());
//
//        tableInfo.setPreName(txtPrefix.getText());
//
//        tableInfo.setTemplateGroupName((String) this.cmbTemplate.getSelectedItem());
//
//        tableInfoService.saveTableInfo(tableInfo);

        String mapperFolder = txtMapperFolder.getText();
        if (StringUtils.isNotEmpty(mapperFolder)) {
            mapperFolder = mapperFolder.trim();
            if (!mapperFolder.endsWith("/") && !mapperFolder.endsWith("\\")) {
                mapperFolder = mapperFolder + "/";
            }
        }

        GenerateOptions options = GenerateOptions.builder()
                .reFormat(chkFormat.isSelected())
                .titleSure(chkAllYes.isSelected())
                .titleRefuse(chkAllNo.isSelected())
                .unifiedConfig(true)
                .moduleName(txtModule.getText())
                .savePath(this.rootOutputPath)
                .mapperFolder(mapperFolder)
                .packageName(txtBaiscPackage.getText())
                .mixed(true)
                .templateGroupName((String) cmbTemplate.getSelectedItem())
                .mybatisConfiguration((String) cmbConfiguration.getSelectedItem())
                .build();


        CodeGenerateService service = new CodeGenerateService(project, templates, options, tables);

        try {
            saveState();

            service.generate(null);

            notify(this.project, false);

            notifyUI();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            notify(this.project, true);
        }

    }

    private void notifyUI() {
        Messages.showInfoMessage("Generate finished.", GlobalDict.TITLE_INFO);

        VirtualFileManager.getInstance().syncRefresh();
    }

    private void reSetPackageName(String oldPackageName, String packageName) {

        if (cmbTemplate.getSelectedItem() == null)
            return;

        TemplateGroup templateGroup = TemplateConfiguration.instance().getTemplateGroup((String) cmbTemplate.getSelectedItem());

        if (templateGroup == null)
            return;

        for (String name : packageTextFields.keySet()) {
            JTextField txtField = packageTextFields.get(name);
            Template template = templateGroup.getTemplate(name);

            reSetPackageName(template, oldPackageName, packageName, name, txtField);
        }
    }


    private void setModule(String moduleName) {
        for (String name : moduleNames) {
            JTextField txtField = moduleTextFields.get(name);
            setModule(moduleName, name, txtField);
        }
    }

    private void setModule(String moduleName, String name, JTextField textField) {
        Module module = (Module) cachedValue.get(textField);

        if (module != null)
            return;

        String fullName = null;

        if (this.isOldVersion) {
            if ("model".equals(name)) {
                fullName = moduleName + "-" + name;
                module = getModule(fullName);
                if (module == null)
                    fullName = moduleName + "-common";
            } else if ("repository".equalsIgnoreCase(name)) {
                fullName = moduleName + "-dal";
            } else {
                fullName = moduleName + "-" + name;
            }

        } else {
            if ("model".equals(name)) {
                fullName = moduleName + "-" + name;
            } else {
                fullName = moduleName + "-domain";
            }
        }

        if ("controller".equalsIgnoreCase(name))
            fullName = moduleName + "-web";

        module = getModule(fullName);

        if (module != null) {
            textField.setText(module.getName());
            cachedValue.put(textField, module);
        }
    }

    private String getModulePath(Module module, String defaultValue) {
        if (module == null)
            return defaultValue;
        File file = module.getModuleNioFile().getParent().toFile();
        if (file.getName().equals(".idea")) {
            file = file.getParentFile();
        }
        return file.getPath();
        //return module.getModuleNioFile().getParent().toFile().getPath();
    }

    private void reSetPackageName(Template template, String oldPackageName, String packageName, String module, JTextField textField) {

        if (StringUtils.isEmpty(packageName))
            return;

        if (StringUtils.isEmpty(module))
            return;

        if (textField == null)
            return;


        if ("repository".equals(module) && this.isOldVersion) {
            module = "dal";
        }

        if ("controller".equalsIgnoreCase(module)) {
            module = "web.controller";
        }
        String format = template.getPackageFormat();

        if (StringUtils.isEmpty(format))
            format = "%s" + "." + module;

        String newValue = String.format(format, packageName);

        if (StringUtils.isEmpty(oldPackageName)) {
            textField.setText(newValue);
            return;
        }

        String oldValue = oldPackageName + "." + module;
        if (StringUtils.isEmpty(textField.getText()) || oldValue.equals(textField.getText())) {
            textField.setText(newValue);
        }

    }

    private Module getModule(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return ModuleManager.getInstance(this.project)
                .findModuleByName(name);
    }

    private Module selectModule(JTextField textField, String name) {
        ChooseModulesDialog dialog = new ChooseModulesDialog(this.project, this.modules, "选择模块", "请选择一个模块");
        dialog.setSingleSelectionMode();
        dialog.show();

        Module module = dialog.getChosenElements().stream().findFirst().orElse(null);
        Module before = (Module) cachedValue.get(textField);
        //module.getModuleNioFile().getParent().toFile().getPath()
        if (module != null && module != before) {
            textField.setText(module.getName());

            if (!StringUtils.isEmpty(name)) {
                JTextField txt = packageTextFields.get(name);
                if (StringUtils.isEmpty(txt.getText()) && StringUtils.isNotEmpty(txtBaiscPackage.getText())) {
                    TemplateGroup templateGroup = TemplateConfiguration.instance().getTemplateGroup((String) cmbTemplate.getSelectedItem());
                    Template template = templateGroup.getTemplate(name);
                    if (template != null)
                        txt.setText(String.format(template.getPackageFormat(), txtBaiscPackage.getText()));
                }
            }

            cachedValue.put(textField, module);
        }
        return module;
    }

    private boolean validateSettings() {
        String template = (String) cmbTemplate.getSelectedItem();
        if (StringUtils.isEmpty(template) || templateGroup == null) {
            Messages.showWarningDialog("Can't Select Template!", GlobalDict.TITLE_INFO);
            return false;
        }

        this.isOldVersion = "spring".equalsIgnoreCase(template);

        String basicPackageName = txtBaiscPackage.getText();
        if (StringUtils.isEmpty(basicPackageName)) {
            Messages.showWarningDialog("Can't Select Basic Package!", GlobalDict.TITLE_INFO);
            return false;
        }

        if (chkModel.isSelected() && StringUtils.isEmpty(txtModelModule.getText())) {
            Messages.showWarningDialog("Can't Select Model Module!", GlobalDict.TITLE_INFO);
            return false;
        }

        if (chkRepository.isSelected() && StringUtils.isEmpty(txtRepositoryModule.getText())) {
            Messages.showWarningDialog("Can't Select Repository Module!", GlobalDict.TITLE_INFO);
            return false;
        }
        if (chkService.isSelected() && StringUtils.isEmpty(txtServiceModule.getText())) {
            Messages.showWarningDialog("Can't Select Service Module!", GlobalDict.TITLE_INFO);
            return false;
        }
        if (chekManager.isSelected() && StringUtils.isEmpty(txtManagerModule.getText())) {
            Messages.showWarningDialog("Can't Select Manager Module!", GlobalDict.TITLE_INFO);
            return false;
        }
        if (chekController.isSelected() && StringUtils.isEmpty(txtControllerModule.getText())) {
            Messages.showWarningDialog("Can't Select Controller Module!", GlobalDict.TITLE_INFO);
            return false;
        }


        return true;
    }

    List<Template> getAllSelectedTemplate() {
        if (templateGroup == null)
            return Collections.emptyList();

        List<Template> ary = new ArrayList<>();

        for (String module : this.moduleNames) {

            var items = getTemplate(module,
                    moduleTextFields.get(module),
                    packageTextFields.get(module),
                    moduleCheckboxes.get(module));

            ary.addAll(items);
        }

        return ary;
    }

    private List<Template> getTemplate(String moduleGroup, JTextField moduleField, JTextField packageField, JCheckBox chk) {


        var module = (Module) cachedValue.get(moduleField);
        String modulePath = getModulePath(module, null);
        if (StringUtils.isEmpty(modulePath))
            return Collections.emptyList();

        String packageName = packageField.getText();
        String templateRootPath = TemplateConfiguration.getTemplatePath().getPath();

        List<Template> ary = new ArrayList<>();

        List<Template> items = this.templateGroup.getTemplates()
                .stream()
                .filter(r -> r.getGroup().equals(moduleGroup))
                .collect(Collectors.toList());

        for (var item : items) {
            Template template = item.clone();
            String content = getTemplateContent(templateRootPath, template);
            template.setContent(content);
            template.setPath(packageName);
            template.setPackageName(packageName);
            template.setModulePath(modulePath);
            template.setEnable(chk.isSelected());
            ary.add(template);
        }

        return ary;
    }

    private String getTemplateContent(String resourcePath, Template template) {

        String templateFile = resourcePath + "/" + templateGroup.getName() + "/";
        if (template.getName().equals("mapper") && this.isOracleDataSource) {
            templateFile += "oracle." + template.getFile();
            if (!new File(templateFile).exists())
                templateFile += template.getFile();
        } else {
            templateFile += template.getFile();
        }
        return FileUtil.readUtf8String(templateFile);
    }

    private void notify(Project project, boolean hasError) {
        if (!hasError) {
            Notifier.notifyInformation(project,
                    GlobalDict.TITLE_INFO, " generate finished");
        } else {
            Notifier.notifyInformation(project,
                    GlobalDict.TITLE_INFO, " generate failed");
        }
    }
}
