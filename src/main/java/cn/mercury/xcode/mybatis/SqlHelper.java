package cn.mercury.xcode.mybatis;

import cn.mercury.xcode.idea.DatasourceHelper;
import cn.mercury.xcode.utils.FileUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.intellij.database.view.DataSourceNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;

import java.io.IOException;

public class SqlHelper {

    public static void createSqlConsole(Project project, DataSourceNode dataSourceNode, String parent, String fileName, final String sql) {

        ApplicationManager.getApplication().invokeAndWait(() -> {
            // 获取 LocalFileSystem 实例
            VirtualFileSystem fileSystem = LocalFileSystem.getInstance();
            // 在项目根目录下创建一个新的虚拟文件
            VirtualFile baseDir = FileUtils.getInstance().getParentFolder(project, parent);

            VirtualFile newFile = baseDir.findChild(fileName);
            try {
                if (newFile == null)
                    newFile = FileUtils.getInstance().createChildFile(project, baseDir, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 写入文件内容
            if (newFile == null)
                return;

            final VirtualFile file = newFile;
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    String script = SQLUtils.formatMySql(sql);

                    VfsUtil.saveText(file, script);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 刷新文件系统
            fileSystem.refresh(false);

            DatasourceHelper.openSqlConsole(project, dataSourceNode, newFile);

        });
    }

    public static void createSqlConsole(Project project, String dataSourceName, String parent, String fileName, final String sql) {

        ApplicationManager.getApplication().invokeAndWait(() -> {
            // 获取 LocalFileSystem 实例
            VirtualFileSystem fileSystem = LocalFileSystem.getInstance();
            // 在项目根目录下创建一个新的虚拟文件
            VirtualFile baseDir = FileUtils.getInstance().getParentFolder(project, parent);

            VirtualFile newFile = baseDir.findChild(fileName);
            try {
                if (newFile == null)
                    newFile = FileUtils.getInstance().createChildFile(project, baseDir, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 写入文件内容
            if (newFile == null)
                return;

            final VirtualFile file = newFile;
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    String script = SQLUtils.formatMySql(sql);

                    VfsUtil.saveText(file, script);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 刷新文件系统
            fileSystem.refresh(false);

            DatasourceHelper.openSqlConsole(project, dataSourceName, newFile);

        });
    }

}
