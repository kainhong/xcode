package cn.mercury.xcode.mybatis.language.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * The interface Constructor.
 *

 */
public interface Constructor extends DomElement {

    /**
     * Gets args.
     *
     * @return the args
     */
    @SubTagList("arg")
    List<Arg> getArgs();

    /**
     * Gets id args.
     *
     * @return the id args
     */
    @SubTagList("idArg")
    List<IdArg> getIdArgs();
}
