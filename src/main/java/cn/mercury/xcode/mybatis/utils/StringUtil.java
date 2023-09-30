package cn.mercury.xcode.mybatis.utils;

import com.intellij.openapi.project.Project;

import java.nio.charset.Charset;


public class StringUtil {

    private StringUtil() {
    }


    public static String encoding(String str, Project project) {
        String encoding = "utf-8";
        return new String(str.getBytes(), Charset.forName(encoding));

    }

}
