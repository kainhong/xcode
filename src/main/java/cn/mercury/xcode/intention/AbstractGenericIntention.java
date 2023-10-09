package cn.mercury.xcode.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGenericIntention implements IntentionAction {


    /**
     * The Chooser.
     */
    protected IntentionChooser chooser;

    /**
     * Instantiates a new Generic intention.
     *
     * @param chooser the chooser
     */
    protected AbstractGenericIntention(@NotNull IntentionChooser chooser) {
        this.chooser = chooser;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return chooser.isAvailable(project, editor, file);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

}
