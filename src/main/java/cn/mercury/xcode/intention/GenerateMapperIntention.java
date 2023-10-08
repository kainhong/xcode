package cn.mercury.xcode.intention;

import cn.mercury.xcode.mybatis.generate.ManagerClassMethodBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * The type Generate mapper intention.
 *

 */
public class GenerateMapperIntention extends AbstractGenericIntention {

    /**
     * Instantiates a new Generate mapper intention.
     */
    public GenerateMapperIntention() {
        super(GenerateMapperChooser.INSTANCE);
    }

    @NotNull
    @Override
    public String getText() {
        return "[xCode] Auto Generate";
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);

        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        String beanName = clazz.getName().replaceAll("(Repository|Mapper)$", "");

        complementServiceClass(project, psiMethod, beanName);
    }

    private void complementServiceClass(Project project, PsiMethod psiMethod, String beanName) {
        String packageName = getPackageName(psiMethod.getContainingClass());

        var builder = new ManagerClassMethodBuilder(project, psiMethod, packageName, beanName);
        builder.build();
    }

    private String getPackageName(PsiClass clazz) {
        return PsiUtil.getPackageName(clazz);
//        PsiJavaFile javaFile = (PsiJavaFile)psiClass.getContainingFile();
//        return javaFile.getPackageName();
    }
}
