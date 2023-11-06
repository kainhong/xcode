package cn.mercury.xcode.code.actions;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.code.service.storage.IGenerateStorageService;
import cn.mercury.xcode.code.setting.type.MatchType;
import cn.mercury.xcode.code.setting.type.DbTypeMapping;
import cn.mercury.xcode.code.ui.GenerateCodeForm;
import cn.mercury.xcode.utils.CacheDataUtils;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

/**
 * 代码生成菜单
 *
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class GenerateAction extends AnAction {

    public GenerateAction() {
        this("Generate Code");
    }

    /**
     * 构造方法
     *
     * @param text 菜单名称
     */
    GenerateAction(@Nullable String text) {
        super(text, text, Icons.load("icons/run.svg"));
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = getDbTables(e.getProject(), e) != null;

        e.getPresentation().setEnabled(enable);
    }


    /**
     * 处理方法
     *
     * @param event 事件对象
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();

        var tables = CacheDataUtils.getInstance().getDbTableList();

        // 校验类型映射
        if (!typeValidator(project, tables)) {
            // 没通过不打开窗口
            return;
        }
        //new DatasourceHelper().execute(event);
        //开始处理

        GenerateCodeForm form = new GenerateCodeForm(event.getProject(), tables);
        form.show();

        int flag = form.getReturnFlag();

        //重启
        if (flag == 9) {
            form = new GenerateCodeForm(event.getProject(), tables);
            form.show();
        }

        CacheDataUtils.getInstance().setDbTableList(null);
    }

    private List<DbTable> getDbTables(Project project, AnActionEvent event) {
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return null;
        }
        List<DbTable> dbTableList = new ArrayList<>();
        for (PsiElement element : psiElements) {
            if (!(element instanceof DbTable)) {
                continue;
            }
            DbTable dbTable = (DbTable) element;
            dbTableList.add(dbTable);
        }
        if (dbTableList.isEmpty()) {
            return null;
        }

        //保存数据到缓存
        CacheDataUtils.getInstance().setDbTableList(dbTableList);

        return dbTableList;
    }

    private Set<String> getAllColumnsDataType(List<DbTable> dbTables) {
        Set<String> types = new HashSet<>();
        for (DbTable d : dbTables) {
            JBIterable<? extends DasColumn> columns = DasUtil.getColumns(d);
            for (DasColumn column : columns) {
                String typeName = column.getDataType().getSpecification();
                types.add(typeName);
            }
        }
        return types;
    }

    /**
     * 类型校验，如果存在未知类型则引导用于去条件类型
     *
     * @param dbTables 原始表对象
     * @return 是否验证通过
     */
    private boolean typeValidator(Project project, List<DbTable> dbTables) {
        if (dbTables == null)
            return false;

        // 处理所有列
        Set<String> allColumnTypes = getAllColumnsDataType(dbTables);

        List<DbTypeMapping> typeMapperList = IGenerateStorageService.getDbTypeMappingGroup().getItems();

        // 简单的记录报错弹窗次数，避免重复报错
        Set<String> errorCount = new HashSet<>();

        allColumnTypes.forEach(t -> {
            for (DbTypeMapping typeMapper : typeMapperList) {
                try {
                    if (typeMapper.match(t))
                        return;
                } catch (PatternSyntaxException e) {
                    if (!errorCount.contains(typeMapper.getColumnType())) {
                        Messages.showWarningDialog(
                                "类型映射《" + typeMapper.getColumnType() + "》存在语法错误，请及时修正。报错信息:" + e.getMessage(),
                                GlobalDict.TITLE_INFO);
                        errorCount.add(typeMapper.getColumnType());
                    }
                }
            }
            new Dialog(project, t).showAndGet();
        });
        return true;
    }

    public static class Dialog extends DialogWrapper {

        private String typeName;

        private JPanel mainPanel;

        private ComboBox<String> comboBox;

        protected Dialog(@Nullable Project project, String typeName) {
            super(project);
            this.typeName = typeName;
            this.initPanel();
        }

        private void initPanel() {
            setTitle(GlobalDict.TITLE_INFO);
            String msg = String.format("数据库类型%s，没有找到映射关系，请输入想转换的类型？", typeName);
            var label = new JLabel(msg);
            this.mainPanel = new JPanel(new BorderLayout());
            this.mainPanel.setBorder(JBUI.Borders.empty(5, 10, 7, 10));
            mainPanel.add(label, BorderLayout.NORTH);
            this.comboBox = new ComboBox<>(GlobalDict.DEFAULT_JAVA_TYPE_LIST);
            this.comboBox.setEditable(true);
            this.mainPanel.add(this.comboBox, BorderLayout.CENTER);
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            return this.mainPanel;
        }

        @Override
        protected void doOKAction() {
            super.doOKAction();
            String selectedItem = (String) this.comboBox.getSelectedItem();
            if (StringUtils.isEmpty(selectedItem)) {
                return;
            }
            DbTypeMapping typeMapper = new DbTypeMapping();
            typeMapper.setMatchType(MatchType.ORDINARY);
            typeMapper.setJavaType(selectedItem);
            typeMapper.setColumnType(typeName);

            IGenerateStorageService.getDbTypeMappingGroup().getItems().add(typeMapper);
        }
    }
}
