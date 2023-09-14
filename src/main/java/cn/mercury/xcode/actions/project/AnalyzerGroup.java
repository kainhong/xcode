package cn.mercury.xcode.actions.project;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnalyzerGroup extends ActionGroup {

    private boolean notExistsChildren;

//    public boolean hideIfNoVisibleChildren() {
//        return this.notExistsChildren;
//    }

    @Override
    public void actionPerformed(AnActionEvent e) {

    }


    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent event) {
        // 获取当前项目
        Project project = getEventProject(event);
        if (project == null) {
            return getEmptyAnAction();
        }

        //获取选中的PSI元素
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);

        this.notExistsChildren = false;

        AnAction action  = new AnalyzerAction();

        return new AnAction[]{action};
    }


    private AnAction[] getEmptyAnAction() {
        this.notExistsChildren = true;
        return AnAction.EMPTY_ARRAY;
    }
}
