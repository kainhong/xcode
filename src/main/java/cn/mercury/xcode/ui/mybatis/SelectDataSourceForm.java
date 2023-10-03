package cn.mercury.xcode.ui.mybatis;

import cn.mercury.xcode.idea.DatasourceHelper;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.view.DataSourceNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SelectDataSourceForm extends DialogWrapper {
    private JComboBox cmbDataSource;
    private JPanel mainPanel;

    private DataSourceNode dbDataSource;

    private Project project;

    public DataSourceNode getDbDataSource() {
        return dbDataSource;

    }

    public SelectDataSourceForm(@Nullable Project project) {
        super(project);
        this.project = project;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        String val = (String) this.cmbDataSource.getSelectedItem();
        if( val == null )
            return;

        this.dbDataSource = DatasourceHelper.getDatasourceNode(project,val).orElse(null);

        this.close(0);
    }
}
