package cn.mercury.xcode.mybatis.language.provider;


import cn.mercury.xcode.mybatis.Icons;
import cn.mercury.xcode.mybatis.language.annotation.Annotation;
import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import cn.mercury.xcode.mybatis.utils.JavaUtils;
import cn.mercury.xcode.mybatis.utils.MapperUtils;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * The type Injection line marker provider.
 *

 */
public class InjectionLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,@NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof PsiField)) {
            return;
        }
        PsiField field = (PsiField) element;
        if (!isTargetField(field)) {
            return;
        }

        PsiType type = field.getType();
        if (!(type instanceof PsiClassReferenceType)) {
            return;
        }

        Optional<PsiClass> clazz = JavaUtils.findClazz(element.getProject(), type.getCanonicalText());
        if (!clazz.isPresent()) {
            return;
        }

        PsiClass psiClass = clazz.get();
        Optional<Mapper> mapper = MapperUtils.findFirstMapper(element.getProject(), psiClass);
        if (!mapper.isPresent()) {
            return;
        }

        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(Icons.SPRING_INJECTION_ICON)
                .setAlignment(GutterIconRenderer.Alignment.CENTER)
                .setTarget(psiClass)
                .setTooltipTitle("Data access object found - " + psiClass.getQualifiedName());
        result.add(builder.createLineMarkerInfo(field.getNameIdentifier()));
    }

    private boolean isTargetField(PsiField field) {
        if (JavaUtils.isAnnotationPresent(field, Annotation.AUTOWIRED)) {
            return true;
        }
        Optional<PsiAnnotation> resourceAnno = JavaUtils.getPsiAnnotation(field, Annotation.RESOURCE);
        if (resourceAnno.isPresent()) {
            PsiAnnotationMemberValue nameValue = resourceAnno.get().findAttributeValue("name");
            String name = nameValue.getText().replaceAll("\"", "");
            return StringUtils.isBlank(name) || name.equals(field.getName());
        }
        return false;
    }

}
