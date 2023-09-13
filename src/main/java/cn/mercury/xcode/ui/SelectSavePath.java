package cn.mercury.xcode.ui;

import cn.hutool.core.io.FileUtil;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.StrState;
import cn.mercury.xcode.generate.GenerateOptions;
import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.model.table.TableInfo;
import cn.mercury.xcode.model.template.Template;
import cn.mercury.xcode.service.CodeGenerateService;
import cn.mercury.xcode.service.SettingsStorageService;
import cn.mercury.xcode.service.TableInfoSettingsService;
import cn.mercury.xcode.ui.component.TemplateSelectComponent;
import cn.mercury.xcode.utils.CacheDataUtils;
import cn.mercury.xcode.utils.ModuleUtils;
import cn.mercury.xcode.utils.ProjectUtils;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ExceptionUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 选择保存路径
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class SelectSavePath extends DialogWrapper {
    /**
     * 主面板
     */
    private JPanel contentPane;
    /**
     * 模型下拉框
     */
    private JComboBox<String> moduleComboBox;
    /**
     * 包字段
     */
    private JTextField packageField;
    /**
     * 路径字段
     */
    private JTextField pathField;
    /**
     * 前缀字段
     */
    private JTextField preField;
    /**
     * 包选择按钮
     */
    private JButton packageChooseButton;
    /**
     * 路径选择按钮
     */
    private JButton pathChooseButton;

    private JButton btnSelectExtendTemplate;

    private JTextField txtExtendTemplate;
    /**
     * 模板面板
     */
    private JPanel templatePanel;
    /**
     * 统一配置复选框
     */
    private JCheckBox unifiedConfigCheckBox;
    /**
     * 弹框选是复选框
     */
    private JCheckBox titleSureCheckBox;
    /**
     * 格式化代码复选框
     */
    private JCheckBox reFormatCheckBox;
    /**
     * 弹框全否复选框
     */
    private JCheckBox titleRefuseCheckBox;
    /**
     * 数据缓存工具类
     */
    private CacheDataUtils cacheDataUtils = CacheDataUtils.getInstance();
    /**
     * 表信息服务
     */
    private TableInfoSettingsService tableInfoService;
    /**
     * 项目对象
     */
    private Project project;
    /**
     * 代码生成服务
     */
    private CodeGenerateService codeGenerateService;
    /**
     * 当前项目中的module
     */
    private List<Module> moduleList;


    /**
     * 模板选择组件
     */
    private TemplateSelectComponent templateSelectComponent;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.contentPane;
    }

    /**
     * 构造方法
     */
    public SelectSavePath(Project project) {
        super(project);
        this.project = project;
        this.tableInfoService = TableInfoSettingsService.getInstance();
        this.codeGenerateService = CodeGenerateService.getInstance(project);
        // 初始化module，存在资源路径的排前面
        this.moduleList = new LinkedList<>();

        //ProjectTree projectTree = ModuleUtils.getProjectTree(project);

        for (Module module : ModuleManager.getInstance(project).getModules()) {
            // 存在源代码文件夹放前面，否则放后面
            if (ModuleUtils.existsSourcePath(module)) {
                this.moduleList.add(0, module);
            } else {
                this.moduleList.add(module);
            }
        }
        this.initPanel();
        this.refreshData();
        this.initEvent();
        init();
        setTitle(GlobalDict.TITLE_INFO);
        //初始化路径
        refreshPath();
    }

    private void initEvent() {
        //监听module选择事件
        moduleComboBox.addActionListener(e -> {
            // 刷新路径
            refreshPath();
        });

        try {
            Class<?> cls = Class.forName("com.intellij.ide.util.PackageChooserDialog");
            //添加包选择事件
            packageChooseButton.addActionListener(e -> {
                try {
                    Constructor<?> constructor = cls.getConstructor(String.class, Project.class);
                    Object dialog = constructor.newInstance("Package Chooser", project);
                    // 显示窗口
                    Method showMethod = cls.getMethod("show");
                    showMethod.invoke(dialog);
                    // 获取选中的包名
                    Method getSelectedPackageMethod = cls.getMethod("getSelectedPackage");
                    Object psiPackage = getSelectedPackageMethod.invoke(dialog);
                    if (psiPackage != null) {
                        Method getQualifiedNameMethod = psiPackage.getClass().getMethod("getQualifiedName");
                        String packageName = (String) getQualifiedNameMethod.invoke(psiPackage);
                        packageField.setText(packageName);
                        // 刷新路径
                        refreshPath();
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                         InvocationTargetException e1) {
                    ExceptionUtil.rethrow(e1);
                }
            });

            // 添加包编辑框失去焦点事件
            packageField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    // 刷新路径
                    refreshPath();
                }
            });
        } catch (ClassNotFoundException e) {
            // 没有PackageChooserDialog，并非支持Java的IDE，禁用相关UI组件
            packageField.setEnabled(false);
            packageChooseButton.setEnabled(false);
        }

        //选择路径
        pathChooseButton.addActionListener(e -> {
            //将当前选中的model设置为基础路径
            VirtualFile path = ProjectUtils.getBaseDir(project);
            Module module = getSelectModule();
            if (module != null) {
                path = ModuleUtils.getSourcePath(module);
            }
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, path);
            if (virtualFile != null) {
                pathField.setText(virtualFile.getPath());
            }
        });

        btnSelectExtendTemplate.addActionListener(e -> {
            //将当前选中的model设置为基础路径
            VirtualFile path = ProjectUtils.getBaseDir(project);
            Module module = getSelectModule();
            if (module != null) {
                path = ModuleUtils.getSourcePath(module);
            }
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false , true, false, false);

            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, path);
            if (virtualFile != null) {
                if(FileUtil.exist(virtualFile.getPath() + "/setting.json")){
                    Messages.showWarningDialog("配置文件不正确,请确保文件夹下面存在setting.json", GlobalDict.TITLE_INFO);
                    return;
                }
                boolean ok = SettingsStorageService.getInstance().reloadTemplate(virtualFile.getPath());
                if (ok) {
                    SettingsStorageService.getInstance().getState().setExtendTemplateFile(virtualFile.getPath());
                    txtExtendTemplate.setText(virtualFile.getPath());
                    this.templateSelectComponent.refreshData();
                } else {
                    Messages.showWarningDialog("请确保配置文件和模板文件格式是否正确", GlobalDict.TITLE_INFO);
                }

            }
        });
    }

    private void refreshData() {
        // 获取选中的表信息（鼠标右键的那张表），并提示未知类型
        TableInfo tableInfo = tableInfoService.getTableInfo(cacheDataUtils.getDbTableList().get(0));

        // 设置默认配置信息
        if (!StringUtils.isEmpty(tableInfo.getSaveModelName())) {
            moduleComboBox.setSelectedItem(tableInfo.getSaveModelName());
        }
        if (!StringUtils.isEmpty(tableInfo.getSavePackageName())) {
            packageField.setText(tableInfo.getSavePackageName());
        }
        if (!StringUtils.isEmpty(tableInfo.getPreName())) {
            preField.setText(tableInfo.getPreName());
        }
        SettingsStorage settings = SettingsStorageService.getSettingsStorage();
        String groupName = settings.getCurrTemplateGroupName();

        if (StringUtils.isNotEmpty(settings.getExtendTemplateFile())) {
            this.txtExtendTemplate.setText(settings.getExtendTemplateFile());
        }

        if (!StringUtils.isEmpty(tableInfo.getTemplateGroupName())) {
            if (settings.getTemplateGroupMap().containsKey(tableInfo.getTemplateGroupName())) {
                groupName = tableInfo.getTemplateGroupName();
            }
        }

        if (!settings.getTemplateGroupMap().containsKey(groupName)) {
            groupName = GlobalDict.DEFAULT_TEMPLATE_NAME;
        }
        templateSelectComponent.setSelectedGroupName(groupName);
        String savePath = tableInfo.getSavePath();
        if (!StringUtils.isEmpty(savePath)) {
            // 判断是否需要拼接项目路径
            if (savePath.startsWith(StrState.RELATIVE_PATH)) {
                String projectPath = project.getBasePath();
                savePath = projectPath + savePath.substring(1);
            }
            pathField.setText(savePath);
        }
    }

    @Override
    protected void doOKAction() {
        boolean result = onOK();
        //super.doOKAction();
        if (this.contentPane != null && this.contentPane instanceof DialogPanel) {
            ((DialogPanel) this.contentPane).apply();
        }
        if (result)
            this.close(0);
    }

    /**
     * 确认按钮回调事件
     */
    private boolean onOK() {
        List<Template> selectTemplateList = templateSelectComponent.getAllSelectedTemplate();
        // 如果选择的模板是空的
        if (selectTemplateList.isEmpty()) {
            Messages.showWarningDialog("Can't Select Template!", GlobalDict.TITLE_INFO);
            return false;
        }
        String savePath = pathField.getText();
        if (StringUtils.isEmpty(savePath)) {
            Messages.showWarningDialog("Can't Select Save Path!", GlobalDict.TITLE_INFO);
            return false;
        }
        // 针对Linux系统路径做处理
        savePath = savePath.replace("\\", "/");
        // 保存路径使用相对路径
        String basePath = project.getBasePath();
        if (!StringUtils.isEmpty(basePath) && savePath.startsWith(basePath)) {
            if (savePath.length() > basePath.length()) {
                if ("/".equals(savePath.substring(basePath.length(), basePath.length() + 1))) {
                    savePath = savePath.replace(basePath, ".");
                }
            } else {
                savePath = savePath.replace(basePath, ".");
            }
        }
        generate(selectTemplateList, savePath);

        notify(project);

        return true;
    }

    private void generate(List<Template> selectTemplateList, String savePath) {
        // 保存配置
        TableInfo tableInfo = tableInfoService.getTableInfo(cacheDataUtils.getDbTableList().get(0));

        tableInfo.setSavePath(savePath);

        tableInfo.setSavePackageName(packageField.getText());
        tableInfo.setPreName(preField.getText());
        tableInfo.setTemplateGroupName(templateSelectComponent.getselectedGroupName());
        Module module = getSelectModule();
        if (module != null) {
            tableInfo.setSaveModelName(module.getName());
        }
        //VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        // 保存配置
        tableInfoService.saveTableInfo(tableInfo);

        Set<String> paths = selectTemplateList.stream()
                .filter(c -> StringUtils.isNotBlank(c.getPath()))
                .map(c -> c.getPath()).collect(Collectors.toSet());

        boolean mixed = (paths.size() >= 2);

        GenerateOptions op = getGenerateOptions(savePath, mixed);

        op.setModuleName(module.getName());
        // 生成代码
        codeGenerateService.generate(selectTemplateList, op, cacheDataUtils.getDbTableList());


    }


    private void notify(Project project) {
        Notifier.notifyInformation(project,
                GlobalDict.TITLE_INFO, " generate finished");
    }

    /**
     * 初始化方法
     */
    private void initPanel() {
        // 初始化模板组
        this.templateSelectComponent = new TemplateSelectComponent();
        templatePanel.add(this.templateSelectComponent.getMainPanel(), BorderLayout.CENTER);

        //初始化Module选择
        this.moduleList.stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).forEach(m -> {
            moduleComboBox.addItem(m.getName());
        });
    }

    /**
     * 获取生成选项
     *
     * @return {@link GenerateOptions}
     */
    private GenerateOptions getGenerateOptions(String savePath, boolean mixed) {
        return GenerateOptions.builder()
                .reFormat(reFormatCheckBox.isSelected())
                .titleSure(titleSureCheckBox.isSelected())
                .titleRefuse(titleRefuseCheckBox.isSelected())
                .unifiedConfig(unifiedConfigCheckBox.isSelected())
                .savePath(savePath)
                .mixed(mixed)
                .build();
    }

    /**
     * 获取选中的Module
     *
     * @return 选中的Module
     */
    private Module getSelectModule() {
        String name = (String) moduleComboBox.getSelectedItem();
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return ModuleManager.getInstance(project).findModuleByName(name);
    }

    /**
     * 获取基本路径
     *
     * @return 基本路径
     */
    private String getBasePath() {
        Module module = getSelectModule();
        VirtualFile baseVirtualFile = ProjectUtils.getBaseDir(project);
        if (baseVirtualFile == null) {
            Messages.showWarningDialog("无法获取到项目基本路径！", GlobalDict.TITLE_INFO);
            return "";
        }
        String baseDir = baseVirtualFile.getPath();
        if (module != null) {
            VirtualFile virtualFile = ModuleUtils.getSourcePath(module);
            if (virtualFile != null) {
                baseDir = virtualFile.getPath();
            }
        }
        return baseDir;
    }

    /**
     * 刷新目录
     */
    private void refreshPath() {
        if (StringUtils.isNotEmpty(pathField.getText()))
            return;
        String packageName = packageField.getText();
        // 获取基本路径
        String path = getBasePath();
        // 兼容Linux路径
        path = path.replace("\\", "/");
        // 如果存在包路径，添加包路径
        if (!StringUtils.isEmpty(packageName)) {
            path += "/" + packageName.replace(".", "/");
        }
        pathField.setText(path);
    }
}
