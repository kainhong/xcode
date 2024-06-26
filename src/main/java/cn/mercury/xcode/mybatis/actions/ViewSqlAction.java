package cn.mercury.xcode.mybatis.actions;

import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import cn.mercury.xcode.mybatis.ui.ParamsSettingForm;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewSqlAction extends AnAction {

    final Pattern pattern = Pattern.compile("<select[^<>]*?\\sid=['\"]?(?<id>\\w+)['\"]?(\\s|.)*?>", Pattern.CASE_INSENSITIVE);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
//        if (!validate(project)) {
//            Messages.showInfoMessage("请先配置数据源", GlobalDict.TITLE_INFO);
//            return;
//        }

        @Nullable VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        @Nullable PsiFile psFile = CommonDataKeys.PSI_FILE.getData(e.getDataContext());

        if(!file.getName().endsWith(".xml"))
            return;

        String id = getId(e);
        String namespace = null;

        if( psFile instanceof XmlFile) {
            XmlTag rootTag = ((XmlFile) psFile).getRootTag();
            namespace = rootTag.getAttributeValue("namespace");
        }

        ParamsSettingForm dialog = new ParamsSettingForm(project, file, namespace, id);

        dialog.show();
    }


    @Override
    public void update(@NotNull final AnActionEvent e) {
        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        boolean enable = editor != null && project != null
                && getId(e) != null;

        enable = enable && file.getName().endsWith(".xml");

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

        int count = document.getLineCount();

        int start = document.getLineStartOffset(line);

        int end = document.getLineEndOffset(Math.min(line + 4, count - 1)); //

        TextRange range = new TextRange(start, end);

        String text = document.getText(range);

        return text;
    }
}
