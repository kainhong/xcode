package cn.mercury.xcode.intention;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The type Generate mapper chooser.
 *

 */
public class GenerateMapperChooser extends JavaFileIntentionChooser {

    /**
     * The constant INSTANCE.
     */
    public static final JavaFileIntentionChooser INSTANCE = new GenerateMapperChooser();

    Pattern regex = Pattern.compile("(Repository|Mapper)$");

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        if (isPositionOfInterfaceDeclaration(element)) {
            // ensure parent element is a PsiClass
            PsiElement firstParent = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (firstParent != null) {
                PsiClass psiClass = (PsiClass) firstParent;
                PsiAnnotation @NotNull [] annos = psiClass.getModifierList().getAnnotations();
                if (annos != null) {
                    Optional<PsiAnnotation> op = Arrays.stream(annos)
                            .filter(anno -> anno.getQualifiedName().equals("org.apache.ibatis.annotations.Mapper"))
                            .findFirst();
                    if (op.isPresent())
                        return true;
                }

                if( regex.matcher(psiClass.getName()).find() )
                    return true;

                if (psiClass.isInterface()) {
                    return !isTargetPresentInXml(psiClass);
                }
            }
        }
        return false;
    }

}
