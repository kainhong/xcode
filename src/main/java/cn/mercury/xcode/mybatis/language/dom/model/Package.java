package cn.mercury.xcode.mybatis.language.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Package.
 *

 */
public interface Package extends DomElement {

    /**
     * Gets name.
     *
     * @return the name
     */
    @NotNull
    @Attribute("name")
    GenericAttributeValue<String> getName();

}
