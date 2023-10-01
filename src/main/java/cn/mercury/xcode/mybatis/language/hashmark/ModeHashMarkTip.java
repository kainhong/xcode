package cn.mercury.xcode.mybatis.language.hashmark;

import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * see mybatis : org.apache.ibatis.mapping.ParameterMode
 *
 * @author ls9527
 */
public class ModeHashMarkTip implements HashMarkTip {
    private static final List<String> MODE_LIST = new ArrayList<String>() {
        {
            add("IN");
            add("OUT");
            add("INOUT");
        }
    };

    @Override
    public String getName() {
        return "mode";
    }

    /**
     * 最简单的枚举值提示
     *
     * @param completionResultSet
     * @param mapper
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {
        for (String mode : MODE_LIST) {
            completionResultSet.addElement(LookupElementBuilder.create(mode));
        }
    }
}
