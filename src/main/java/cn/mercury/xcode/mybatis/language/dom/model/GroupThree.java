package cn.mercury.xcode.mybatis.language.dom.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * The interface Group three.
 *

 */
public interface GroupThree extends GroupTwo {

    /**
     * Gets select key.
     *
     * @return the select key
     */
    @SubTagList("selectKey")
    List<SelectKey> getSelectKey();

}
