//package cn.mercury.xcode.setting.ui;
//
//import cn.mercury.xcode.GlobalDict;
//import cn.mercury.xcode.code.service.storage.GenerateStateStorage;
//import cn.mercury.xcode.code.setting.Template;
//import cn.mercury.xcode.code.setting.TemplateGroup;
//import cn.mercury.xcode.setting.ui.component.*;
//import cn.mercury.xcode.utils.CloneUtils;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.cfg.BaseSettings;
//import com.intellij.ide.fileTemplates.impl.UrlUtil;
//import com.intellij.openapi.options.Configurable;
//import com.intellij.util.ExceptionUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.IOException;
//import java.util.Map;
//import java.util.function.Consumer;
//
///**
//
// * @version 1.0.0
// * @date 2021/08/10 16:14
// */
//public class TemplateSettingForm implements Configurable, BaseSettingsFrom<GenerateStateStorage> {
//    /**
//     * 模板描述信息，说明文档
//     */
//    private static final String TEMPLATE_DESCRIPTION_INFO;
//
//    static {
//        String descriptionInfo = "";
//        try {
//            descriptionInfo = UrlUtil.loadText(TemplateSettingForm.class.getResource("/description/templateDescription.html"));
//        } catch (IOException e) {
//            ExceptionUtil.rethrow(e);
//        } finally {
//            TEMPLATE_DESCRIPTION_INFO = descriptionInfo;
//        }
//    }
//
//    private JPanel mainPanel;
//    /**
//     * 类型映射配置
//     */
//    private Map<String, TemplateGroup> templateGroupMap;
//    /**
//     * 当前分组名
//     */
//    private TemplateGroup currTemplateGroup;
//    /**
//     * 编辑框组件
//     */
//    private EditorComponent<Template> editorComponent;
//    /**
//     * 分组操作组件
//     */
//    private GroupNameComponent<Template, TemplateGroup> groupNameComponent;
//    /**
//     * 编辑列表框
//     */
//    private EditListComponent<Template> editListComponent;
//
//
//    public TemplateSettingForm() {
//        this.mainPanel = new JPanel(new BorderLayout());
//    }
//
//
//    private void initGroupName() {
//        Consumer<TemplateGroup> switchGroupOperator = templateGroup -> {
//            this.currTemplateGroup = templateGroup;
//            refreshUiVal();
//            // 切换分组情况编辑框
//            this.editorComponent.setFile(null);
//        };
//
//        this.groupNameComponent = new GroupNameComponent<>(switchGroupOperator, this.templateGroupMap);
//        this.mainPanel.add(groupNameComponent.getPanel(), BorderLayout.NORTH);
//    }
//
//    private void initEditList() {
//        Consumer<Template> switchItemFun = template -> {
//            refreshUiVal();
//            if (template != null) {
//                this.editListComponent.setCurrentItem(template.getName());
//            }
//            editorComponent.setFile(template);
//        };
//        this.editListComponent = new EditListComponent<>(switchItemFun, "Template Name:", Template.class, this.currTemplateGroup.getElementList());
//    }
//
//    private void initEditor() {
//        this.editorComponent = new EditorComponent<>(null, TEMPLATE_DESCRIPTION_INFO);
//    }
//
//    private void initPanel() {
//        this.loadSettingsStore(getGenerateStateStorage());
//        // 初始化表格
//        this.initGroupName();
//        // 初始化编辑列表组件
//        this.initEditList();
//        // 初始化编辑框组件
//        this.initEditor();
//        // 初始化实时调试
//        this.initRealtimeDebug();
//        // 左右组件
//        LeftRightComponent leftRightComponent = new LeftRightComponent(editListComponent.getMainPanel(), this.editorComponent.getMainPanel());
//        this.mainPanel.add(leftRightComponent.getMainPanel(), BorderLayout.CENTER);
//    }
//
//    private void initRealtimeDebug() {
//        RealtimeDebugComponent realtimeDebugComponent = new RealtimeDebugComponent(editorComponent);
//        groupNameComponent.getPanel().add(realtimeDebugComponent.getMainPanel());
//    }
//
//    @Override
//    public String getDisplayName() {
//        return "Template";
//    }
//
//    @Nullable
//    @Override
//    public String getHelpTopic() {
//        return getDisplayName();
//    }
//
//    @Override
//    public void loadSettingsStore(GenerateStateStorage GenerateStateStorage) {
//        // 复制配置，防止篡改
//        this.templateGroupMap = CloneUtils.cloneByJson(GenerateStateStorage.getTemplateGroupMap(), new TypeReference<Map<String, TemplateGroup>>() {
//        });
//
//        if(this.templateGroupMap != null && StringUtils.isNotBlank(GenerateStateStorage.getCurrTemplateGroupName())) {
//            this.currTemplateGroup = this.templateGroupMap.get(GenerateStateStorage.getCurrTemplateGroupName());
//            if (this.currTemplateGroup == null) {
//                this.currTemplateGroup = this.templateGroupMap.get(GlobalDict.DEFAULT_TEMPLATE_NAME);
//            }
//            // 解决reset后编辑框未清空BUG
//            if (this.editorComponent != null) {
//                this.editorComponent.setFile(null);
//            }
//        }
//        this.refreshUiVal();
//    }
//
//    @Override
//    public @Nullable JComponent createComponent() {
//        this.initPanel();
//        return mainPanel;
//    }
//
//    @Override
//    public boolean isModified() {
//        return !this.templateGroupMap.equals(getGenerateStateStorage().getTemplateGroupMap())
//                || !getGenerateStateStorage().getCurrTemplateGroupName().equals(this.currTemplateGroup.getName());
//    }
//
//    @Override
//    public void apply() {
//        getGenerateStateStorage().setTemplateGroupMap(this.templateGroupMap);
//        getGenerateStateStorage().setCurrTemplateGroupName(this.currTemplateGroup.getName());
//        // 保存包后重新加载配置
//        this.loadSettingsStore(getGenerateStateStorage());
//    }
//
//    private void refreshUiVal() {
//        if (this.groupNameComponent != null) {
//            this.groupNameComponent.setGroupMap(this.templateGroupMap);
//            this.groupNameComponent.setCurrGroupName(this.currTemplateGroup.getName());
//        }
//        if (this.editListComponent != null) {
//            this.editListComponent.setElementList(this.currTemplateGroup.getElementList());
//        }
//    }
//}
