package cn.mercury.xcode.idea;

import cn.hutool.core.util.ReflectUtil;
import com.intellij.database.dataSource.AbstractDataSource;
import com.intellij.database.dataSource.DatabaseConnection;
import com.intellij.database.dataSource.DatabaseConnectionManager;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.model.basic.BasicNode;
import com.intellij.database.remote.jdbc.RemoteConnection;
import com.intellij.database.remote.jdbc.RemoteResultSet;
import com.intellij.database.remote.jdbc.RemoteResultSetMetaData;
import com.intellij.database.remote.jdbc.RemoteStatement;
import com.intellij.database.util.GuardedRef;
import com.intellij.database.view.DataSourceNode;
import com.intellij.database.view.DatabaseView;
import com.intellij.database.view.structure.DvRootDsGroup;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreeModel;
import java.util.*;

public class DatasourceHelper {

    //private static final Logger logger = LoggerFactory.getLogger(DatasourceHelper.class);

    public static List<LocalDataSource> listDatasource(Project project) {
        DatabaseView databaseView = DatabaseView.getDatabaseView(project);

        TreeModel model = databaseView.getTree().getModel();

        DvRootDsGroup group = (DvRootDsGroup) model.getRoot();

        TreeSet<BasicNode> children = (TreeSet<BasicNode>) ReflectUtil.getFieldValue(group, "children");

        List<LocalDataSource> lst = new ArrayList<>();

        for (BasicNode node : children) {
            String name = node.getDisplayName();
            //System.out.println(name);
            DataSourceNode dataSourceNode = (DataSourceNode) node;

            LocalDataSource ds = dataSourceNode.getLocalDataSource();

            lst.add(ds);
        }

        return lst;
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
