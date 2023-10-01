package cn.mercury.xcode.mybatis.language.dom.converter;


import cn.mercury.xcode.mybatis.language.dom.MapperBacktrackingUtils;
import cn.mercury.xcode.mybatis.utils.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PropertySetterFind {

    public Optional<PsiField> getStartElement(@Nullable String firstText, XmlAttributeValue element) {
        if (firstText == null) {
            return Optional.empty();
        }
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getPropertyClazz(element);
        if (!clazz.isPresent()) {
            return Optional.empty();
        }
        return getPsiField(firstText, clazz.get());
    }

    public List<PsiField> getSetterFields(PsiClass psiClass) {
        return Arrays.asList(psiClass.getAllFields()).stream()
            .filter(field -> getPsiField(field, psiClass).isPresent()).collect(Collectors.toList());
    }

    private Optional<PsiField> getPsiField(@NotNull PsiField psiField, PsiClass psiClass) {
        return getPsiField(psiField.getName(), psiClass);
    }

    private Optional<PsiField> getPsiField(@NotNull String firstText, PsiClass psiClass) {
        if (psiClass.isAnnotationType() || psiClass.isInterface()) {
            return Optional.empty();
        }
        PsiMethod propertySetter = PropertyUtil.findPropertySetter(psiClass, firstText, false, true);
        if (null == propertySetter) {
            propertySetter = findPsiMethodIfNotSetter(firstText, psiClass);
        }
        return propertySetter == null ? Optional.empty() : Optional.ofNullable(PropertyUtil.findPropertyField(psiClass, firstText, false));
    }

    /**
     * 支持特殊的 setter 方法
     * 例如 oName, 正常来说： 生成的方法名应该是: setoName, 部分框架会生成: setOName
     * 而mybatis是支持这两种写法的
     *
     * @param firstText
     * @param psiClass
     * @return
     */
    private PsiMethod findPsiMethodIfNotSetter(@NotNull String firstText, PsiClass psiClass) {
        String setterMethodName = "set" + StringUtil.upperCaseFirstChar(firstText);
        final PsiMethod[] methodsByName = psiClass.findMethodsByName(setterMethodName, true);
        for (PsiMethod psiMethod : methodsByName) {
            if (psiMethod.hasModifierProperty(PsiModifier.STATIC) != false) {
                continue;
            }
            return psiMethod;
        }
        return null;
    }

}
