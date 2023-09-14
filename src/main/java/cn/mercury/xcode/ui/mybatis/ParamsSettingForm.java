package cn.mercury.xcode.ui.mybatis;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.idea.DatasourceHelper;
import com.intellij.database.console.JdbcConsole;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ParamsSettingForm extends DialogWrapper {
    private final Project project;
    private JPanel mainPanel;
    private JButton btnCancel;
    private JButton btnOk;
    private JTable table1;



    private void initParamsPanel() {


    }


    @Override
    protected JComponent createCenterPanel() {
         return this.mainPanel;
    }

    static final String panel_name_prefix = "paramPanel-";

    private JPanel createField(int index) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel();
        label.getPreferredSize().setSize(120, -1);
        label.setText("Left" + index + ":");
        panel.add(label, BorderLayout.WEST);

        JTextField txt = new JTextField();
        txt.setName("params." + index);

        panel.add(txt, BorderLayout.CENTER);
        panel.setName(panel_name_prefix + index);

        return panel;
    }

    private Map<String, Object> getValues() {
//        Map<String, Object> values = new HashMap<>();
//
//        for (Component component : paramsPanel.getComponents()) {
//            if (component.getName() != null && !component.getName().startsWith(panel_name_prefix))
//                continue;
//
//            JPanel panel = (JPanel) component;
//
//            JTextField txt = (JTextField) panel.getComponent(1);
//
//            values.put(txt.getName(), txt.getText());
//        }
//
//        return values;
        return null;
    }


    protected void createSqlFile() {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            // 获取 LocalFileSystem 实例
            VirtualFileSystem fileSystem = LocalFileSystem.getInstance();

            // 在项目根目录下创建一个新的虚拟文件
            VirtualFile baseDir = project.getBaseDir();
            String fileName = "example.sql";
            VirtualFile newFile = null;
            try {
                newFile = baseDir.createChildData(this, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 写入文件内容
            if (newFile != null) {
                final VirtualFile file = newFile;
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    try {
                        VfsUtil.saveText(file, "select * from brand limit 10;");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            // 刷新文件系统
            fileSystem.refresh(false);

            // 打开保存的文件
//            if (newFile != null) {
//                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
//                FileEditor[] fileEditors = fileEditorManager.openFile(newFile, true);
//            }

            @NotNull List<JdbcConsole> consoles = JdbcConsole.getActiveConsoles(project);

            LocalDataSource ds = DatasourceHelper.listDatasource(project).get(0);
            JdbcConsole.newConsole(project)
                    .forFile(newFile)
                    .fromDataSource(ds)
                    .build();


        });
    }

    private void initEvent() {
        btnCancel.addActionListener(e -> dispose());
    }

    public static final int TABLE_ROW_HEIGHT = 20;

    public ParamsSettingForm(Project project) {
        super(project);
        setTitle(GlobalDict.TITLE_INFO);
        this.project = project;

        //setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(btnOk);
        table1.setRowHeight(TABLE_ROW_HEIGHT);
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                dispose();
//            }
//        });

        this.initEvent();

        this.initParamsPanel();

        Vector<Vector<String>> dataVector = new Vector<>();
        for (int i = 0; i < 10; i++) {
            Vector<String> row = new Vector<>();
            row.add("Name - " + i);
            row.add("Content - " + i);
            dataVector.add(row);
        }

        Vector<String> columnIdentifiers = new Vector<>();
        columnIdentifiers.add("name");
        columnIdentifiers.add("content");

        DefaultTableModel model = new DefaultTableModel(dataVector, columnIdentifiers) {
            public boolean isCellEditable(int row, int column) {
                return column ==  1;
            }
        };

        table1.setModel(model);

        this.init();
    }
}
