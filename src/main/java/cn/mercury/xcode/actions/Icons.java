package cn.mercury.xcode.actions;

import com.intellij.ui.IconManager;

import javax.swing.*;
import java.io.File;

public class Icons {

    public static Icon load(String path){
        if(!path.startsWith("/icons") && !path.endsWith(".svg")){
            return loadForPath(path);
        } else{
            return IconManager.getInstance().getIcon(path, Icons.class);
        }
    }
    static  Icon defaultSearchIcon = load("/icons/default-search.svg");
    private static Icon loadForPath(String path) {
        try {
            var imgURL = new File(path).toURI();
            var ft = new ImageIcon(imgURL.toURL());
            // 大小超过 16*16 或未读取到 使用默认的图标

            if ((ft.getIconHeight() > 16 || ft.getIconWidth() > 16)) {
                return defaultSearchIcon;
            }

            return ft;
        }catch(Exception e){
            return defaultSearchIcon;
        }
    }
}
