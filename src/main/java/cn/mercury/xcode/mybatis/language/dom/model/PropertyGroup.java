package cn.mercury.xcode.mybatis.language.dom.model;

import cn.mercury.xcode.mybatis.language.dom.converter.ColumnConverter;
import cn.mercury.xcode.mybatis.language.dom.converter.JdbcTypeConverter;
import cn.mercury.xcode.mybatis.language.dom.converter.PropertyConverter;
import cn.mercury.xcode.mybatis.language.dom.converter.TypeHandlerConverter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * The interface Property group.
 *

 */
public interface PropertyGroup extends DomElement {

    /**
     * Gets property.
     *
     * @return the property
     */
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();

    /**
     * column
     *
     * @return
     */
    @Attribute("column")
    @Convert(value = ColumnConverter.class, soft = true)
    GenericAttributeValue<XmlAttributeValue> getColumn();

    /**
     * jdbcType
     *
     * @return
     */
    @Attribute("jdbcType")
    @Convert(JdbcTypeConverter.class)
    GenericAttributeValue<XmlAttributeValue> getJdbcType();

    /**
     * jdbcType
     *
     * @return
     */
    @Attribute("typeHandler")
    @Convert(TypeHandlerConverter.class)
    GenericAttributeValue<PsiClass> getTypeHandler();
}
