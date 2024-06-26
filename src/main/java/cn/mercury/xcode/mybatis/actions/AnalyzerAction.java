package cn.mercury.xcode.mybatis.actions;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.mybatis.ui.ParamsSettingForm;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnalyzerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();

        if (!validate(project)) {
            Messages.showInfoMessage("请先配置数据源", GlobalDict.TITLE_INFO);
            return;
        }

        //Messages.showInfoMessage("OK", GlobalDict.TITLE_INFO);

        //new ParamsSettingForm(event.getProject()).show();
        @Nullable VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());

        if( !file.getName().endsWith(".xml"))
            return;

        ParamsSettingForm dialog = new ParamsSettingForm(event.getProject(),file);

        dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        @Nullable VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());

        boolean enable = (file instanceof XmlFile == false);

        event.getPresentation().setEnabledAndVisible(enable);

//        String ext = FileUtils.getFileExtension(event.getDataContext());
//
//        event.getPresentation().setEnabledAndVisible("xml".equals(ext));
    }

    private boolean validate(Project project) {
        List<LocalDataSource> ds = DatasourceHelper.listDatasource(project);
        if (ds == null || ds.size() == 0)
            return false;
        return true;
    }
}
