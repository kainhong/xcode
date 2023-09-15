package cn.mercury.xcode.ui.mybatis;

import cn.hutool.core.util.ReflectUtil;
import cn.mercury.mybatis.analyzer.MybatisMapperAnalyzer;
import cn.mercury.mybatis.analyzer.MybatisMapperStatementAnalyzer;
import cn.mercury.mybatis.analyzer.ParameterMock;
import cn.mercury.mybatis.analyzer.ParameterSqlNode;
import cn.mercury.mybatis.analyzer.model.Tree;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.utils.FileUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.intellij.database.console.DbConsoleRootType;
import com.intellij.database.console.JdbcConsole;
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

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
    private JPanel mainPanel;
    private JButton btnMock;
    private JTable table1;
    private JComboBox cmbStatement;
    private JComboBox cmbDataSource;

    private Vector<Vector<String>> dataVector;

    private MybatisMapperStatementAnalyzer statement;


    @Override
    protected JComponent createCenterPanel() {
        return this.mainPanel;
    }

    static final String panel_name_prefix = "paramPanel-";

    @Override
    protected void doOKAction() {
        this.createSqlFile();

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

        VirtualFile baseDir = project.getBaseDir();
        try {
            return FileUtils.getInstance().createChildDirectory(project, baseDir, "output");
        } catch (Exception e) {

        }
        return null;
    }

    private String generateSql() {

        Map<String, Object> values = getValues();

        return this.statement.parse(values, false).getSql();
    }

    protected void createSqlFile() {
        if (this.statement == null)
            return;

        String dsName = (String) cmbDataSource.getSelectedItem();

        Optional<LocalDataSource> opSource = DatasourceHelper.listDatasource(project)
                .stream()
                .filter(v -> v.getName().equals(dsName))
                .findFirst();

        if (opSource.isEmpty()) {
            Messages.showWarningDialog("请选择一个数据源", GlobalDict.TITLE_INFO);
            return;
        }

        String fileName = statement.getStatement().getId().replace("\\.", "_") + ".sql";

        ApplicationManager.getApplication().invokeAndWait(() -> {
            // 获取 LocalFileSystem 实例
            VirtualFileSystem fileSystem = LocalFileSystem.getInstance();
            // 在项目根目录下创建一个新的虚拟文件
            VirtualFile baseDir = getParentFolder();

            VirtualFile newFile = baseDir.findChild(fileName);
            try {
                if( newFile == null )
                    newFile = FileUtils.getInstance().createChildFile(project, baseDir, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 写入文件内容
            if (newFile == null)
                return;

            final VirtualFile file = newFile;
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    String sql = generateSql();

                    sql = SQLUtils.formatMySql(sql);

                    VfsUtil.saveText(file, sql);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 刷新文件系统
            fileSystem.refresh(false);

            openSqlConsole(dsName,newFile);

//            JdbcConsole.newConsole(project)
//                    .forFile(newFile)
//                    .fromDataSource(opSource.get())
//                    .build();

        });
    }

    private void openSqlConsole( String dsName,VirtualFile file) {
        DatabaseView databaseView = DatabaseView.getDatabaseView(project);

        TreeModel model = databaseView.getTree().getModel();

        DvRootDsGroup group = (DvRootDsGroup) model.getRoot();

        TreeSet<BasicNode> children = (TreeSet<BasicNode>) ReflectUtil.getFieldValue(group, "children");

        DataSourceNode dataSourceNode = null;

        for (BasicNode node : children) {
            String name = node.getDisplayName();
            if( dsName.equals(name)) {
                dataSourceNode = (DataSourceNode) node;
            }
            //LocalDataSource ds = dataSourceNode.getLocalDataSource();
        }

        DbDataSource dbDataSource = dataSourceNode.dbDataSource;

        DasNamespace context = dbDataSource.getModel().getCurrentRootNamespace();

        DatabaseEditorHelper.openConsoleForFile(this.project,dataSourceNode.getLocalDataSource(),context,file);
    }


    private void initEvent() {
        btnMock.addActionListener(e -> this.mockParam());

        cmbStatement.addActionListener((e) -> {
            loadParamData();
        });
    }

    public static final int TABLE_ROW_HEIGHT = 20;

    private void loadParamData() {
        String id = (String) cmbStatement.getSelectedItem();
        if (StringUtils.isEmpty(id))
            return;

        Vector<Vector<String>> dataVector = new Vector<>();

        statement = analyzer.getStatement(id);

        if (StringUtils.isNotEmpty(id)) {
            Tree<ParameterSqlNode> ps = statement.getStatement().getConditions();
            List<ParameterSqlNode> items = ps.toList(v -> v);
            Set<String> names = new HashSet<>();
            items.stream().forEach(v -> {
                if (v == null)
                    return;
                if (v.getVariables() != null && v.getVariables().size() > 0) {
                    v.getVariables().stream().forEach(v2 -> {
                        if (names.contains(v2))
                            return;
                        Vector<String> row = new Vector<>();
                        row.add(v2);
                        row.add(v.getType());
                        dataVector.add(row);
                        names.add(v2);
                    });
                } else {
                    if (names.contains(v.getExpress()))
                        return;
                    names.add(v.getExpress());
                    Vector<String> row = new Vector<>();
                    row.add(v.getExpress());
                    row.add(v.getType());
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
        this.dataVector = dataVector;
        table1.setModel(model);
    }

    private void mockParam() {
        if (this.statement == null)
            return;

        DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();
        int rowCount = tableModel.getRowCount();

        Map<String, Object> values = new HashMap<>();

        ParameterMock mocker = ParameterMock.builder().values(values)
                .conditions(this.statement.getStatement().getConditions()).build();

        for (int r = 0; r < rowCount; r++) {
            String p = (String) tableModel.getValueAt(r, 0);
            String t = (String) tableModel.getValueAt(r, 1);
            String v = (String) tableModel.getValueAt(r, 2);
            if (StringUtils.isNotEmpty(v))
                continue;

            Object d = mocker.make(p, values);
            String value = "";
            if (d != null)
                value = d.toString();

            tableModel.setValueAt(value, r, 2);
        }
    }

    //private MybatisMapperAnalyzer analyzer;
    MybatisMapperAnalyzer analyzer;

    private void load(VirtualFile file) {
        String txt = FileUtils.readFile(file);
        InputStream stream = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));

        this.analyzer = new MybatisMapperAnalyzer(stream, file.getName());

        List<String> lst = this.analyzer.listStatementIds();
        for (String id : lst) {
            cmbStatement.addItem(id);
        }

        List<LocalDataSource> ds = DatasourceHelper.listDatasource(project);
        for (LocalDataSource item : ds) {
            cmbDataSource.addItem(item.getName());
        }

        loadParamData();
        //analyzer = new MybatisMapperAnalyzer(virtualFile);
        //analyzer.analyze();
    }

    public ParamsSettingForm(Project project, VirtualFile file) {
        super(project);
        this.project = project;
        this.init();

        setTitle(GlobalDict.TITLE_INFO);

        table1.setRowHeight(TABLE_ROW_HEIGHT);

        this.initEvent();

        this.load(file);

    }
}
