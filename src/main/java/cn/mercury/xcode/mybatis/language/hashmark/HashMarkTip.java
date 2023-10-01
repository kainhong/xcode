package cn.mercury.xcode.mybatis.language.hashmark;

import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import com.intellij.codeInsight.completion.CompletionResultSet;

public interface HashMarkTip {
    String getName();

    void tipValue(CompletionResultSet completionResultSet, Mapper mapper);
}
