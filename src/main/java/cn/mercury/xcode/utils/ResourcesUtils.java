package cn.mercury.xcode.utils;

import cn.hutool.core.io.FileUtil;
import com.intellij.ide.fileTemplates.impl.UrlUtil;

import java.io.IOException;

public class ResourcesUtils {

    public static String readText(String resourceUri){
        try {
            if( resourceUri.startsWith("classpath:")) {
                resourceUri = resourceUri.replace("classpath:","");
                return UrlUtil.loadText(ResourcesUtils.class.getResource(resourceUri));
            }
            else if( resourceUri.startsWith("file:")){
                resourceUri = resourceUri.replace("file:","");
                return FileUtil.readUtf8String(resourceUri);
            }
            throw new IllegalArgumentException("unkown file path:" + resourceUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
