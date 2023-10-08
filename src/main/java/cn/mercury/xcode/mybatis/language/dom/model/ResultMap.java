package cn.mercury.xcode.mybatis.language.dom.model;

import cn.mercury.xcode.mybatis.language.dom.converter.AliasConverter;
import cn.mercury.xcode.mybatis.language.dom.converter.ResultMapConverter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Result map.
 *

 */
public interface ResultMap extends GroupFour, IdDomElement {

    /**
     * Gets extends.
     *
     * @return the extends
     */
    @NotNull
    @Attribute("extends")
    @Convert(ResultMapConverter.class)
    GenericAttributeValue<XmlAttributeValue> getExtends();

    /**
     * Gets type.
     *
     * @return the type
     */
    @NotNull
    @Attribute("type")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getType();

}
