package cn.mercury.xcode.mybatis.language.dom.model;

import cn.mercury.xcode.mybatis.language.dom.converter.ResultMapConverter;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Result map group.
 *
 * @author yanglin
 */
public interface ResultMapGroup extends DomElement {

    /**
     * Gets result map.
     *
     * @return the result map
     */
    @NotNull
    @Attribute("resultMap")
    @Convert(ResultMapConverter.class)
    GenericAttributeValue<XmlTag> getResultMap();
}
