package cn.mercury.xcode.mybatis.language.reference;

import cn.mercury.xcode.mybatis.language.dom.converter.PropertySetterFind;
import cn.mercury.xcode.mybatis.utils.JavaUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Psi field reference set resolver.
 *

 */
public class PsiFieldReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {

    /**
     * Instantiates a new Psi field reference set resolver.
     *
     * @param from the from
     */
    protected PsiFieldReferenceSetResolver(XmlAttributeValue from) {
        super(from);
    }

    @NotNull
    @Override
    public String getText() {
        return getElement().getValue();
    }

    @Override
    public Optional<PsiField> resolve(PsiField current, String text) {
        PsiType type = current.getType();
        if (type instanceof PsiClassReferenceType && !((PsiClassReferenceType) type).hasParameters()) {
            PsiClass clazz = ((PsiClassReferenceType) type).resolve();
            if (null != clazz) {
                return JavaUtils.findSettablePsiField(clazz, text);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<PsiField> getStartElement(@Nullable String firstText) {
        PropertySetterFind propertySetterFind = new PropertySetterFind();
        return propertySetterFind.getStartElement(firstText, getElement());
    }

}
