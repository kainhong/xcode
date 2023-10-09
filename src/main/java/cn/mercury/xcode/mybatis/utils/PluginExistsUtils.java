package cn.mercury.xcode.mybatis.utils;

public class PluginExistsUtils {
    private static volatile Boolean existsDatabaseTools = null;

    public static boolean existsDbTools() {
        if (existsDatabaseTools == null) {
            synchronized (PluginExistsUtils.class) {
                if (existsDatabaseTools == null) {
                    try {
                        Class.forName("com.intellij.database.psi.DbTable");
                        existsDatabaseTools = true;
                    } catch (ClassNotFoundException ex) {
                        existsDatabaseTools = false;
                    }
                }
            }
        }
        return existsDatabaseTools;
    }
}
