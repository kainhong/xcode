package cn.mercury.xcode.mybatis.language.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The interface Beans.
 *

 */
public interface Beans extends DomElement {

    /**
     * Gets beans.
     *
     * @return the beans
     */
    @NotNull
    @SubTagList("bean")
    List<Bean> getBeans();

}
