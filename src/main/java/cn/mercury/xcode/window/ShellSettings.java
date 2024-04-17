package cn.mercury.xcode.window;

import lombok.Data;

@Data
public class ShellSettings {

    private String completeURL = "https://dev-wx.demo.cn/";

    private Integer maxTokenSize = 1024;

    static ShellSettings current = new ShellSettings();
    public static ShellSettings getInstance() {
        return current;
    }
}
