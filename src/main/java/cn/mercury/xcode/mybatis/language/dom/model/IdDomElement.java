package cn.mercury.xcode.mybatis.language.dom.model;


import cn.mercury.xcode.mybatis.language.dom.converter.DaoMethodConverter;
import com.intellij.util.xml.*;

/**
 * The interface Id dom element.
 *

 */
public interface IdDomElement extends DomElement {

    /**
     * Gets id.
     *
     * @return the id
     */
    @Required
    @NameValue
    @Attribute("id")
    @Convert(DaoMethodConverter.class)
    GenericAttributeValue<Object> getId();

    /**
     * Sets value.
     *
     * @param content the content
     */
    void setValue(String content);
}
