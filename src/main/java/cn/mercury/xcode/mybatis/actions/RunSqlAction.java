package cn.mercury.xcode.mybatis.actions;

import cn.hutool.core.lang.UUID;
import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.mybatis.SqlHelper;
import cn.mercury.xcode.mybatis.utils.LogUtil;
import cn.mercury.xcode.mybatis.utils.StringUtil;
import cn.mercury.xcode.mybatis.ui.SelectDataSourceForm;
import com.intellij.database.view.DataSourceNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class RunSqlAction extends AnAction {

    public RunSqlAction() {

    }

    public RunSqlAction(@Nls(capitalization = Nls.Capitalization.Title) @Nullable String text,
                        @Nls(capitalization = Nls.Capitalization.Sentence) @Nullable String description,
                        @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String sql = LogUtil.copySql(e);
        if (StringUtil.isEmpty(sql))
            return;

        List<DataSourceNode> lst = DatasourceHelper.listDataSourceNode(e.getProject());
//        if (lst == null || lst.size() == 0) {
//            Messages.showInfoMessage("请先配置数据源", GlobalDict.TITLE_INFO);
//            return;
//        }
        DataSourceNode ds = null;

        if (lst.size() > 1) {
            SelectDataSourceForm form = new SelectDataSourceForm(e.getProject());
            boolean ok = form.showAndGet();
            if (ok)
                ds = form.getDbDataSource();

        } else {
            ds = lst.get(0);
        }


        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".sql";

        SqlHelper.createSqlConsole(e.getProject(), ds, "output", fileName, sql);

    }

}
