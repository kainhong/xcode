package cn.mercury.xcode.intention;

import cn.mercury.xcode.mybatis.language.service.JavaService;
import cn.mercury.xcode.mybatis.utils.JavaUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The type Java file intention chooser.
 *

 */
public abstract class JavaFileIntentionChooser implements IntentionChooser {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if(!(element instanceof  PsiMethod)) {
            element = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if(element == null )
                return false;
        }

        PsiMethod psiMethod = (PsiMethod) element;
        PsiClass psiClass = psiMethod.getContainingClass();

        //  不应该判断当前模块是不是已经有了这个mapper. 因为无法判断
        boolean r = null != element
                && JavaUtils.isElementWithinInterface(psiClass)
                && isAvailable(element);

        return r;
    }

    /**
     * Is available boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public abstract boolean isAvailable(@NotNull PsiElement element);

    /**
     * Is position of parameter declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfParameterDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiParameter;
    }

    /**
     * Is position of method declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfMethodDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiMethod;
    }

    /**
     * Is position of interface declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfInterfaceDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiClass;
    }

    /**
     * Is target present in xml boolean.
     *
     * @param psiClass the element
     * @return the boolean
     */
    public boolean isTargetPresentInXml(@NotNull PsiClass psiClass) {
        return JavaService.getInstance(psiClass.getProject()).findWithFindFirstProcessor(psiClass).isPresent();
    }

}
