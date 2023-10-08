package cn.mercury.xcode.mybatis.language.dom.model;

import cn.mercury.xcode.mybatis.language.dom.converter.SqlConverter;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * The interface Include.
 *

 */
public interface Include extends DomElement {

    /**
     * Gets ref id.
     *
     * @return the ref id
     */
    @Attribute("refid")
    @Convert(SqlConverter.class)
    GenericAttributeValue<XmlTag> getRefId();

}
