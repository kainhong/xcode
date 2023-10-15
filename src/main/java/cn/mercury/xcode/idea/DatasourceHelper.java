package cn.mercury.xcode.idea;

import cn.hutool.core.util.ReflectUtil;
import com.intellij.database.dataSource.AbstractDataSource;
import com.intellij.database.dataSource.DatabaseConnection;
import com.intellij.database.dataSource.DatabaseConnectionManager;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.editor.DatabaseEditorHelper;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.basic.BasicNode;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.database.remote.jdbc.RemoteConnection;
import com.intellij.database.remote.jdbc.RemoteResultSet;
import com.intellij.database.remote.jdbc.RemoteResultSetMetaData;
import com.intellij.database.remote.jdbc.RemoteStatement;
import com.intellij.database.util.GuardedRef;
import com.intellij.database.view.DataSourceNode;
import com.intellij.database.view.DatabaseView;
import com.intellij.database.view.structure.DvRootDsGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.tree.TreeModel;
import java.util.*;
import java.util.stream.Collectors;

public class DatasourceHelper {

    //private static final Logger logger = LoggerFactory.getLogger(DatasourceHelper.class);

    public static List<DataSourceNode> listDataSourceNode(Project project) {
        DatabaseView databaseView = DatabaseView.getDatabaseView(project);

        TreeModel model = databaseView.getTree().getModel();

        DvRootDsGroup group = (DvRootDsGroup) model.getRoot();

        TreeSet<BasicNode> children = (TreeSet<BasicNode>) ReflectUtil.getFieldValue(group, "children");

        if (children == null) {
            return Collections.emptyList();
        }

        return children.stream().map(n -> (DataSourceNode) n).collect(Collectors.toList());
    }

    public static List<LocalDataSource> listDatasource(Project project) {
        return listDataSourceNode(project).stream().map(n -> n.getLocalDataSource()).collect(Collectors.toList());

//        List<LocalDataSource> lst = new ArrayList<>();
//        if (children == null)
//            return lst;
//
//        for (BasicNode node : children) {
//            String name = node.getDisplayName();
//            //System.out.println(name);
//            DataSourceNode dataSourceNode = (DataSourceNode) node;
//
//            LocalDataSource ds = dataSourceNode.getLocalDataSource();
//
//            lst.add(ds);
//        }
//
//        return lst;
    }

    public static Optional<DataSourceNode> getDatasourceNode(Project project, String name) {
        return listDataSourceNode(project).stream().filter(n -> n.getDisplayName().equals(name)).findFirst();
    }


    public static void openSqlConsole(Project project, String dsName, VirtualFile file) {
        DatabaseView databaseView = DatabaseView.getDatabaseView(project);

        TreeModel model = databaseView.getTree().getModel();

        DvRootDsGroup group = (DvRootDsGroup) model.getRoot();

        TreeSet<BasicNode> children = (TreeSet<BasicNode>) ReflectUtil.getFieldValue(group, "children");

        DataSourceNode dataSourceNode = null;
        if (children != null && dsName != null) {
            for (BasicNode node : children) {
                String name = node.getDisplayName();
                if (dsName.equals(name)) {
                    dataSourceNode = (DataSourceNode) node;
                }
                //LocalDataSource ds = dataSourceNode.getLocalDataSource();
            }
        }

        if (dataSourceNode != null) {
            DbDataSource dbDataSource = dataSourceNode.dbDataSource;

            DasNamespace context = dbDataSource.getModel().getCurrentRootNamespace();

            DatabaseEditorHelper.openConsoleForFile(project, dataSourceNode.getLocalDataSource(), context, file);
        } else {
            FileEditorManager.getInstance(project).openFile(file, true);
        }
    }

    public static void openSqlConsole(Project project, DataSourceNode dataSourceNode, VirtualFile file) {

        if (dataSourceNode != null) {
            DasNamespace context = dataSourceNode.getModel().getCurrentRootNamespace();

            DatabaseEditorHelper.openConsoleForFile(project, dataSourceNode.getLocalDataSource(), context, file);
        } else {
            FileEditorManager.getInstance(project).openFile(file, true);
        }
    }

    public static String getDataSourceDriver(DataSourceNode dataSourceNode){
        return dataSourceNode.getLocalDataSource().getDriverClass();
    }

    public static LocalDataSource getTableDataSource(DbTable table){
         DbDataSource datasource = (DbDataSource) table.getParent().getParent();
         return (LocalDataSource) datasource.getDelegate();
    }

    public static boolean isOracle(DbTable table){
        String driver = getTableDataSource(table).getDriverClass();
        if(driver.contains("oracle"))
            return true;
        return false;
    }

    public static List<Object> execute(Project project, AbstractDataSource realDataSource, String sql) {
        RemoteResultSet rs = null;
        try {
            LocalDataSource localDataSource = (LocalDataSource) realDataSource;

            //通过数据库连接管理创建连接
            GuardedRef<DatabaseConnection> connectionGuardedRef = DatabaseConnectionManager.getInstance().build(project, localDataSource).create();

            // 获取数据库连接
            DatabaseConnection databaseConnection = connectionGuardedRef.get();
            RemoteConnection remoteConnection = databaseConnection.getRemoteConnection();

            // 开始执行sql
            RemoteStatement statement = remoteConnection.createStatement();

            rs = statement.executeQuery(sql);
            List<Object> rows = new ArrayList<>();
            RemoteResultSetMetaData meta = rs.getMetaData();

            int maxSize = 10;
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String field = meta.getColumnName(i);
                    row.put(field, rs.getObject(i));
                }
                rows.add(row);
                if (rows.size() >= maxSize)
                    break;
            }
            return rows;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
        }
    }

}
