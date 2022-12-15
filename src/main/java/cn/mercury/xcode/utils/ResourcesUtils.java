package cn.mercury.xcode.utils;

import com.intellij.ide.fileTemplates.impl.UrlUtil;

import java.io.IOException;

public class ResourcesUtils {

    public static String readText(String resourceUri){
        try {
            return UrlUtil.loadText(ResourcesUtils.class.getResource(resourceUri));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
