package cn.mercury.xcode.mybatis.actions;

import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.ui.mybatis.ParamsSettingForm;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewSqlAction extends AnAction {

    final Pattern pattern = Pattern.compile("<select.*?id\\W*=\\W*\"(?<id>\\w+)\".*?>", Pattern.CASE_INSENSITIVE);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
//        if (!validate(project)) {
//            Messages.showInfoMessage("请先配置数据源", GlobalDict.TITLE_INFO);
//            return;
//        }

        @Nullable VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        String id = getId(e);

        ParamsSettingForm dialog = new ParamsSettingForm(project, file, null, id);

        dialog.show();
    }


    @Override
    public void update(@NotNull final AnActionEvent e) {
        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);


        boolean enable = editor != null && project != null
                && getId(e) != null;

        e.getPresentation().setEnabledAndVisible(enable);
    }

    private boolean validate(Project project) {
        List<LocalDataSource> ds = DatasourceHelper.listDatasource(project);
        if (ds == null || ds.size() == 0)
            return false;
        return true;
    }

    private String getId(AnActionEvent e) {
        String text = getSelectLineText(e);
        if (text == null)
            return null;

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String id = matcher.group("id");
            return id;
        }

        return null;
    }

    private String getSelectLineText(AnActionEvent e) {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null)
            return null;

        SelectionModel selectionModel = editor.getSelectionModel();

        Document document = editor.getDocument();

        int line = selectionModel.getLeadSelectionPosition().getLine();

        int start = document.getLineStartOffset(line);

        int end = document.getLineEndOffset(line);

        TextRange range = new TextRange(start, end);

        String text = document.getText(range);

        return text;
    }
}
