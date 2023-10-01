package cn.mercury.xcode.mybatis.language.dom.model;

import cn.mercury.xcode.mybatis.language.dom.converter.AliasConverter;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Select.
 *
 * @author yanglin
 */
public interface Select extends GroupTwo, ResultMapGroup, IdDomElement  {

    /**
     * select 标签对应的 resultType
     * @return the result type
     */
    @NotNull
    @Attribute("resultType")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getResultType();

}
