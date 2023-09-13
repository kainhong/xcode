package cn.mercury.xcode.idea;

import com.intellij.database.dataSource.AbstractDataSource;
import com.intellij.database.dataSource.DatabaseConnection;
import com.intellij.database.dataSource.DatabaseConnectionManager;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.model.basic.BasicNode;
import com.intellij.database.remote.jdbc.RemoteConnection;
import com.intellij.database.remote.jdbc.RemoteResultSet;
import com.intellij.database.remote.jdbc.RemoteStatement;
import com.intellij.database.util.GuardedRef;
import com.intellij.database.view.DataSourceNode;
import com.intellij.database.view.DatabaseView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import net.sf.cglib.core.ReflectUtils;


import javax.swing.tree.TreeModel;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.TreeSet;

public class DatasourceHelper {


    public void execute(AnActionEvent e)   {
        DatabaseView databaseView = DatabaseView.getDatabaseView(e.getProject());

        TreeModel model = databaseView.getTree().getModel();

//        TreeSet<BasicNode> children = ReflectUtils.getValue(model.getRoot(), "children");
//// 这里做演示就只使用最后一个数据库连接
//        AbstractDataSource realDataSource = ((DataSourceNode) children.last()).realDataSource;
//        LocalDataSource localDataSource = (LocalDataSource) realDataSource;
////通过数据库连接管理创建连接
//        GuardedRef<DatabaseConnection> connectionGuardedRef  =  DatabaseConnectionManager.getInstance().build(e.getProject(), localDataSource).create();
//// 获取数据库连接
//        DatabaseConnection databaseConnection = connectionGuardedRef.get();
//        RemoteConnection remoteConnection = databaseConnection.getRemoteConnection();
//// 开始执行sql
//        RemoteStatement statement = remoteConnection.createStatement();
//        RemoteResultSet remoteResultSet = statement.executeQuery("select * from example.table");
//        boolean next = remoteResultSet.next();
//        String string = remoteResultSet.getString(1);

    }
}
