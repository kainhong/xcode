package cn.mercury.xcode.ui.mybatis;

import cn.hutool.core.util.ReflectUtil;
import cn.mercury.mybatis.analyzer.MybatisAnalyzer;
import cn.mercury.mybatis.analyzer.MybatisMapperAnalyzer;
import cn.mercury.mybatis.analyzer.MybatisMapperStatementAnalyzer;
import cn.mercury.mybatis.analyzer.ParameterSqlNode;
import cn.mercury.mybatis.analyzer.mock.ParameterMocker;
import cn.mercury.mybatis.analyzer.model.Tree;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.generate.ParamsSettingConfig;
import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.mybatis.SqlHelper;
import cn.mercury.xcode.utils.FileUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.editor.DatabaseEditorHelper;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.basic.BasicNode;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.view.DataSourceNode;
import com.intellij.database.view.DatabaseView;
import com.intellij.database.view.structure.DvRootDsGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ParamsSettingForm extends DialogWrapper {
    private final Project project;
    private String namespace;
    private String statementId;
    private JPanel mainPanel;
    private JButton btnMock;
    private JTable table1;
    private JComboBox cmbStatement;
    private JComboBox cmbDataSource;
    private JComboBox cmbMappers;

    private MybatisMapperStatementAnalyzer statement;

    @Override
    protected JComponent createCenterPanel() {
        return this.mainPanel;
    }


    @Override
    protected void doOKAction() {
        if (this.createSqlFile())
            this.close(0);
    }


    private Map<String, Object> getValues() {
        if (this.statement == null)
            return null;

        DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();
        int rowCount = tableModel.getRowCount();

        Map<String, Object> values = new HashMap<>();

        for (int r = 0; r < rowCount; r++) {
            String v = (String) tableModel.getValueAt(r, 2);
            if (StringUtils.isEmpty(v))
                continue;

            String p = (String) tableModel.getValueAt(r, 0);

            values.put(p, v);
        }

        return values;
    }

    private VirtualFile getParentFolder() {
        return FileUtils.getInstance().getParentFolder(this.project, "output");
    }

    private String generateSql() {

        Map<String, Object> values = getValues();

        values.put("params.queryCondition", "");

        return this.statement.parse(values, false).getSql();
    }

    protected boolean createSqlFile() {
        if (this.statement == null)
            return true;

        String dsName = (String) cmbDataSource.getSelectedItem();

//        Optional<LocalDataSource> opSource = DatasourceHelper.listDatasource(project)
//                .stream()
//                .filter(v -> v.getName().equals(dsName))
//                .findFirst();

//        if (opSource.isEmpty()) {
//            Messages.showWarningDialog("请选择一个数据源", GlobalDict.TITLE_INFO);
//            return false;
//        }

        String fileName = statement.getStatement().getId().replace("\\.", "_") + ".sql";

        String sql = generateSql();

        SqlHelper.createSqlConsole(project, dsName, "output", fileName, sql);

//        ApplicationManager.getApplication().invokeAndWait(() -> {
//            // 获取 LocalFileSystem 实例
//            VirtualFileSystem fileSystem = LocalFileSystem.getInstance();
//            // 在项目根目录下创建一个新的虚拟文件
//            VirtualFile baseDir = getParentFolder();
//
//            VirtualFile newFile = baseDir.findChild(fileName);
//            try {
//                if (newFile == null)
//                    newFile = FileUtils.getInstance().createChildFile(project, baseDir, fileName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            // 写入文件内容
//            if (newFile == null)
//                return;
//
//            final VirtualFile file = newFile;
//            WriteCommandAction.runWriteCommandAction(project, () -> {
//                try {
//                    String sql = generateSql();
//
//                    sql = SQLUtils.formatMySql(sql);
//
//                    VfsUtil.saveText(file, sql);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // 刷新文件系统
//            fileSystem.refresh(false);
//
//            DatasourceHelper.openSqlConsole(project, dsName, newFile);
//
//        });

        return true;
    }


    private void initEvent() {
        btnMock.addActionListener(e -> this.mockParamsValue());

        cmbMappers.addActionListener(e -> {
            onMapperSelected();
        });

        cmbStatement.addActionListener((e) -> {
            loadParamsInfo();
        });
    }

    public static final int TABLE_ROW_HEIGHT = 20;

    private void loadParamsInfo() {
        String id = (String) cmbStatement.getSelectedItem();

        if (StringUtils.isEmpty(id) || mybatisMapperAnalyzer == null)
            return;

        Vector<Vector<String>> dataVector = new Vector<>();

        statement = mybatisMapperAnalyzer.getStatement(id);

        if (StringUtils.isNotEmpty(id)) {
            Tree<ParameterSqlNode> ps = statement.getStatement().getConditions();
            List<ParameterSqlNode> items = ps.toList(v -> v);
            Map<String, Vector<String>> names = new HashMap<>();
            items.stream().forEach(v -> {
                if (v == null)
                    return;
                if (v.getVariables() != null && v.getVariables().size() > 0) {
                    v.getVariables().stream().forEach(v2 -> {
                        Vector<String> row;
                        if (handleParamsTypes(names, v, v2)) return;
                        row = new Vector<>();
                        row.add(v2);
                        row.add(v.getType());
                        dataVector.add(row);
                        names.put(v2, row);
                    });
                } else {
                    Vector<String> row;
                    if (names.containsKey(v.getExpress())) {
                        if (handleParamsTypes(names, v, v.getExpress())) return;
                        return;
                    }

                    row = new Vector<>();
                    row.add(v.getExpress());
                    row.add(v.getType());
                    names.put(v.getExpress(), row);
                    dataVector.add(row);
                }
            });
        }
        dataVector.sort((o1, o2) -> {
            String s1 = o1.get(0);
            String s2 = o2.get(0);
            return s1.compareTo(s2);
        });

        Vector<String> columnIdentifiers = new Vector<>();
        columnIdentifiers.add("参数");
        columnIdentifiers.add("类型");
        columnIdentifiers.add("值");

        DefaultTableModel model = new DefaultTableModel(dataVector, columnIdentifiers) {
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        table1.setModel(model);
    }

    private boolean handleParamsTypes(Map<String, Vector<String>> names, ParameterSqlNode v, String paramName) {
        Vector<String> row;
        if (names.containsKey(paramName)) {
            row = names.get(paramName);
            String type = row.get(1);
            if (StringUtils.isEmpty(type))
                row.set(1, v.getType());
            else if (!type.contains(v.getType()))
                row.set(1, type + "," + v.getType());
            return true;
        }
        return false;
    }

    private void mockParamsValue() {
        if (this.statement == null)
            return;

        DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();
        int rowCount = tableModel.getRowCount();

        Map<String, Object> values = new HashMap<>();

        ParameterMocker mocker = ParameterMocker.builder().values(values)
                .conditions(this.statement.getStatement().getConditions()).build();

        for (int r = 0; r < rowCount; r++) {
            String p = (String) tableModel.getValueAt(r, 0);
            //String t = (String) tableModel.getValueAt(r, 1);
            String v = (String) tableModel.getValueAt(r, 2);

//            if (StringUtils.isNotEmpty(v))
//                continue;

            Object d = ParamsSettingConfig.getDefaultValues().get(p);

            if (d == null)
                d = mocker.make(p, values);

            String value = "";
            if (d != null)
                value = d.toString();

            tableModel.setValueAt(value, r, 2);
        }
    }

    //private MybatisMapperAnalyzer analyzer;
    MybatisMapperAnalyzer mybatisMapperAnalyzer;
    MybatisAnalyzer analyzer;

    private void load(VirtualFile file) throws Exception {
        String txt = FileUtils.readFile(file);
        if (StringUtils.isEmpty(txt))
            return;

        InputStream stream = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
        if (txt.contains("<configuration>") && txt.contains("</configuration>")) {
            this.analyzer = MybatisAnalyzer.fromLocalFile(file.getPath());
            Set<String> mappers = analyzer.getMappers();

            cmbMappers.addItem("");

            for (String mapper : mappers) {
                cmbMappers.addItem(mapper);
            }

            cmbMappers.setEnabled(true);

            onMapperSelected();

        } else {
            this.mybatisMapperAnalyzer = new MybatisMapperAnalyzer(stream, file.getName());
            cmbMappers.addItem(mybatisMapperAnalyzer.getNamespace());
            cmbMappers.setEnabled(false);

            loadMapper();
        }


        //analyzer = new MybatisMapperAnalyzer(virtualFile);
        //analyzer.analyze();
    }


    private void onMapperSelected() {
        if (analyzer == null)
            return;

        String id = (String) cmbMappers.getSelectedItem();
        if (StringUtils.isEmpty(id))
            return;

        String namespace = analyzer.getMapperNamespace(id);

        if (namespace == null)
            return;

        this.mybatisMapperAnalyzer = analyzer.getAnalyzer(namespace);

        loadMapper();
    }

    private void loadMapper() {
        if (mybatisMapperAnalyzer == null)
            return;

        cmbStatement.removeAllItems();
        cmbDataSource.removeAllItems();

        List<String> lst = this.mybatisMapperAnalyzer.listStatementIds();

        for (String id : lst) {
            cmbStatement.addItem(id);
        }
        if (this.statement != null) {
            int index = lst.indexOf(this.statementId);
            if (index > 0)
                cmbStatement.setSelectedIndex(index);
        }

        List<LocalDataSource> ds = DatasourceHelper.listDatasource(project);
        for (LocalDataSource item : ds) {
            cmbDataSource.addItem(item.getName());
        }

        loadParamsInfo();
    }

    public ParamsSettingForm(Project project, VirtualFile file, String ns, String id) {
        super(project);
        this.project = project;
        this.init();
        this.namespace = ns;
        this.statementId = id;

        setTitle(GlobalDict.TITLE_INFO);

        table1.setRowHeight(TABLE_ROW_HEIGHT);

        this.initEvent();

        try {
            this.load(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ParamsSettingForm(Project project, VirtualFile file) {
        this(project, file, null, null);
    }
}
