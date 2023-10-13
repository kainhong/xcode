package cn.mercury.xcode.ui.mybatis;

import cn.mercury.mybatis.JsonUtils;
import cn.mercury.mybatis.analyzer.MybatisAnalyzer;
import cn.mercury.mybatis.analyzer.MybatisAnalyzerParser;
import cn.mercury.mybatis.analyzer.MybatisMapperAnalyzer;
import cn.mercury.mybatis.analyzer.MybatisMapperStatementAnalyzer;
import cn.mercury.mybatis.analyzer.mock.ParameterMocker;
import cn.mercury.mybatis.analyzer.model.ParameterVariable;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.sql.generate.ParamsSettingConfig;
import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.mybatis.SqlHelper;
import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import cn.mercury.xcode.mybatis.utils.MapperUtils;
import cn.mercury.xcode.utils.FileUtils;
import cn.wonhigh.ibatis.builder.SqlFragmentsLoader;
import cn.wonhigh.ibatis.builder.xml.XMLMapperBuilder;
import cn.wonhigh.ibatis.builder.xml.XNodeVisitor;
import cn.wonhigh.ibatis.parsing.XNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class ParamsSettingForm extends DialogWrapper {
    static final Logger logger = Logger.getInstance(ParamsSettingForm.class);

    private final Project project;
    private String namespace;
    private String statementId;
    private JPanel mainPanel;
    private JButton btnMock;
    private JTable table1;
    private JComboBox cmbStatement;
    private JComboBox cmbDataSource;
    private JComboBox cmbMappers;
    private JLabel lblInfo;
    private JPanel panelInfo;

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

    static final Pattern jsonRegex = Pattern.compile("\\{.*\\}|\\[.*\\]");

    private Map<String, Object> getParamsValues() {
        if (this.statement == null)
            return null;

        DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();
        int rowCount = tableModel.getRowCount();

        Map<String, Object> values = new HashMap<>();

        for (int r = 0; r < rowCount; r++) {
            Object v = tableModel.getValueAt(r, 2);
            if (v == null || StringUtils.isEmpty(v.toString()))
                continue;

            String type = (String) tableModel.getValueAt(r, 1);

            String p = (String) tableModel.getValueAt(r, 0);
            if (p != null && jsonRegex.matcher((String) v).find()) {
                try {
                    if ((String) v != null && ((String) v).startsWith("["))
                        v = JsonUtils.fromListJson((String) v, Map.class);
                    else
                        v = JsonUtils.fromJson((String) v, Map.class);
                } catch (Exception e) {

                }
            }
            values.put(p, v);
        }

        return values;
    }

    private VirtualFile getParentFolder() {
        return FileUtils.getInstance().getParentFolder(this.project, "output");
    }

    private String generateSql() {

        Map<String, Object> values = getParamsValues();

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
        try {
            String sql = generateSql();

            SqlHelper.createSqlConsole(project, dsName, "output", fileName, sql);

            return true;
        } catch (Exception ex) {
            Messages.showWarningDialog("生成失败:" + ex.getMessage(), GlobalDict.TITLE_INFO);
            return false;
        }
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

        var ps = statement.getStatement().getAllVariables();
        Multimap<String, ParameterVariable> map = HashMultimap.create();
        for (var p : ps) {
            String name = p.getName();

            if (name.contains(","))
                name = name.substring(0, name.indexOf(","));
            map.put(name, p);
        }

        for (String name : map.keySet()) {
            Vector<String> row = new Vector<>();
            var items = map.get(name);
            String type = items.stream().filter(c -> "$".equals(c.getType()) || "#".equals(c.getType())).findFirst().map(c -> c.getType()).orElse("");

            if (StringUtils.isEmpty(type)) {
                type = items.stream().filter(c -> c.getType() != null && c.getType().contains("for")).findFirst().map(c -> c.getType()).orElse("");
            }

            if (StringUtils.isEmpty(type)) {
                type = items.stream().filter(c -> c.getType() != null && c.getType().contains("if")).findFirst().map(c -> c.getType()).orElse("");
            }


            row.add(0, name);
            row.add(1, type);

            dataVector.add(row);
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
            String type = (String) tableModel.getValueAt(r, 1);
            String v = (String) tableModel.getValueAt(r, 2);

//            if (StringUtils.isNotEmpty(v))
//                continue;

            Object d = ParamsSettingConfig.getDefaultValues().get(p);

            if (d == null) {
                if ("for".equals(type))
                    d = "[]";
                else
                    d = mocker.make(p, values);
            }

            String value = "";
            if (d != null)
                value = d.toString();

            tableModel.setValueAt(value, r, 2);
        }
    }

    //private MybatisMapperAnalyzer analyzer;
    MybatisMapperAnalyzer mybatisMapperAnalyzer;
    MybatisAnalyzer analyzer;

    private XNodeVisitor<XNode> getVisitor() {
        return new XNodeVisitor<XNode>() {
            String mapper;

            @Override
            public boolean visit(XNode node) {
                String tag = node.getName();
                if ("mapper".equals(tag)) {
                    String ns = node.getStringAttribute("namespace", "");
                    if (ns != null) {
                        int index = ns.lastIndexOf(".");
                        if (index > 0)
                            this.mapper = ns.substring(0, index);
                        else
                            this.mapper = null;
                    }
                    return true;
                }
                String id = node.getStringAttribute("id", "");

                SwingUtilities.invokeLater(() -> {
                    if (mapper != null)
                        lblInfo.setText("正在加载:" + mapper + "," + id);
                });

                return true;
            }

            @Override
            public void endVisit(XNode node) {
                String tag = node.getName();
                if ("mapper".equals(tag)) {
                    mapper = null;
                }
            }
        };
    }

    private void load(VirtualFile file) throws Exception {
        String txt = FileUtils.readFile(file);
        if (StringUtils.isEmpty(txt))
            return;
        var visitor = getVisitor();

        try (InputStream stream = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8))) {
            if (txt.contains("<configuration>") && txt.contains("</configuration>")) {
                this.analyzer = MybatisAnalyzerParser.builder()
                        .configurationFile(file.getPath())
                        .ignoreError(true)
                        .visitor(visitor)
                        .build();
            } else {
                SqlFragmentsLoader loader = (configuration, refid) -> {
                    int index = refid.lastIndexOf(".");
                    String namespace = refid.substring(0, index);
                    @NonNls Optional<Mapper> mapper = MapperUtils.findFirstMapper(project, namespace);
                    if (mapper.isEmpty())
                        return null;
                    @NotNull XmlFile vsFile = DomUtil.getFile(mapper.get());
                    String filePath = vsFile.getVirtualFile().getPath();
                    try (InputStream mapperStream = new FileInputStream(filePath)) {
                        new XMLMapperBuilder(mapperStream, configuration, vsFile.getName(), configuration.getSqlFragments()).parse();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return configuration.getSqlFragments().get(refid);
                };

                this.mybatisMapperAnalyzer = new MybatisMapperAnalyzer(stream, file.getName(), loader,visitor);

            }
        }
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

        if (cmbStatement.getItemCount() > 0)
            cmbStatement.removeAllItems();

//        if (cmbDataSource.getItemCount() > 0)
//            cmbDataSource.removeAllItems();

        List<String> lst = this.mybatisMapperAnalyzer.listStatementIds();

        for (String id : lst) {
            cmbStatement.addItem(id);
        }
        if (this.statement != null) {
            int index = lst.indexOf(this.statementId);
            if (index > 0)
                cmbStatement.setSelectedIndex(index);
        }

        loadParamsInfo();
    }

    VirtualFile file;

    public ParamsSettingForm(Project project, VirtualFile file, String ns, String id) {
        super(project);
        this.project = project;
        this.init();
        this.namespace = ns;
        this.statementId = id;
        this.file = file;

        setTitle(GlobalDict.TITLE_INFO);

        table1.setRowHeight(TABLE_ROW_HEIGHT);

        this.initEvent();

        btnMock.setEnabled(false);
        panelInfo.setVisible(true);
        this.loadDataInBackground();
    }

    private void initData() {
        if (analyzer != null) {
            Set<String> mappers = analyzer.getMappers();

            cmbMappers.addItem("");
            for (String mapper : mappers) {
                cmbMappers.addItem(mapper);
            }

            cmbMappers.setEnabled(true);

            onMapperSelected();
        } else if (mybatisMapperAnalyzer != null) {
            cmbMappers.addItem(mybatisMapperAnalyzer.getNamespace());
            cmbMappers.setEnabled(false);
        }

        btnMock.setEnabled(true);
    }

    private void loadDataInBackground() {
        Runnable action = () -> {
            final boolean ok = load();
            // 数据加载完成后更新界面
            SwingUtilities.invokeLater(() -> {
                panelInfo.setVisible(false);
                if (ok) {
                    final List<LocalDataSource> ds = DatasourceHelper.listDatasource(project);
                    for (LocalDataSource item : ds) {
                        cmbDataSource.addItem(item.getName());
                    }
                    initData();
                    loadMapper();
                } else {
                    Messages.showWarningDialog("加载失败", GlobalDict.TITLE_INFO);
                }
            });
        };

//        ApplicationManager.getApplication().invokeAndWait(
//                () -> ApplicationManager.getApplication().runWriteAction(() -> action.run()),
//                NON_MODAL
//        );

        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Loading Data", false) {
            @Override
            public void run(@org.jetbrains.annotations.NotNull ProgressIndicator indicator) {

//                indicator.setFraction((double) i / 100.0);
//                indicator.setText2("Loading... " + i + "%");

                action.run();
            }
        });
    }

    private boolean load() {
        try {
            load(this.file);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void init() {
        super.init();

    }

    public ParamsSettingForm(Project project, VirtualFile file) {
        this(project, file, null, null);
    }
}
