package cn.mercury.xcode.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.mercury.xcode.model.ProjectTree;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模块工具类
 *

 * @version 1.0.0
 * @since 2018/09/01 17:15
 */
public final class ModuleUtils {
    /**
     * 禁用构造方法
     */
    private ModuleUtils() {
        throw new UnsupportedOperationException();
    }

    public static ProjectTree getProjectTree(Project project){
        //TODO: 路径规则不明确，不可用
        List<ProjectTree.ModuleNode> nodes = Arrays.stream(ModuleManager.getInstance(project).getModules())
                .map(m->{
                    ProjectTree.ModuleNode node = new ProjectTree.ModuleNode();
                    node.setName(m.getName());
                    node.setPath(ModuleUtil.getModuleDirPath(m));
                    node.setModule(m);
                    return node;
                })
                .sorted(Comparator.comparing(ProjectTree.ModuleNode::getPath))
                .collect(Collectors.toList());

        ProjectTree tree = new ProjectTree();
        tree.setProject(project);
        tree.setName(project.getName());
        tree.setPath(project.getProjectFilePath());



        for(ProjectTree.ModuleNode node :nodes){
            var parent = tree.findParent(node.getPath());
            if( parent == null )
                tree.getChildren().add(node);
            else
                parent.getChildren().add(node);
        }

        return tree;
    }

    /**
     * 获取module路径
     *
     * @param module 模块
     * @return 路径
     */
    public static VirtualFile getModuleDir(@NotNull Module module) {
        // 优先使用ModuleRootManager来获取module路径
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        for (VirtualFile contentRoot : moduleRootManager.getContentRoots()) {
            if (contentRoot.isDirectory() && contentRoot.getName().equals(module.getName())) {
                return contentRoot;
            }
        }
        String modulePath = ModuleUtil.getModuleDirPath(module);
        // 统一路径分割符号
        modulePath = modulePath.replace("\\", "/");
        // 尝试消除不正确的路径
        if (modulePath.contains(".idea/modules/")) {
            modulePath = modulePath.replace(".idea/modules/","");
        }
        if (modulePath.contains(".idea/modules")) {
            modulePath = modulePath.replace(".idea/modules","");
        }
        if (modulePath.contains("/.idea")) {
            modulePath = modulePath.replace("/.idea","");
        }
        VirtualFile dir = VirtualFileManager.getInstance().findFileByUrl(String.format("file://%s", modulePath));
        if (dir == null) {
            Messages.showInfoMessage("无法获取Module路径, path=" + modulePath, "title");
        }
        return dir;
    }

    /**
     * 获取模块的源代码文件夹，不存在
     *
     * @param module 模块对象
     * @return 文件夹路径
     */
    public static VirtualFile getSourcePath(@NotNull Module module) {
        List<VirtualFile> virtualFileList = ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.SOURCE);
        if (CollectionUtil.isEmpty(virtualFileList)) {
            VirtualFile modulePath = getModuleDir(module);
            // 尝试智能识别源代码路径(通过上面的方式，IDEA不能百分百拿到源代码路径)
            VirtualFile srcDir = VfsUtil.findRelativeFile(modulePath, "src", "main", "java");
            if (srcDir != null && srcDir.isDirectory()) {
                return srcDir;
            }
            return modulePath;
        }
        if (virtualFileList.size() > 1) {
            for (VirtualFile file : virtualFileList) {
                String tmpPath = file.getPath();
                if (!tmpPath.contains("build") && !tmpPath.contains("generated")) {
                    return file;
                }
            }
        }
        return virtualFileList.get(0);
    }

    /**
     * 判断模块是否存在源代码文件夹
     *
     * @param module 模块对象
     * @return 是否存在
     */
    public static boolean existsSourcePath(Module module) {
        return !CollectionUtil.isEmpty(ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.SOURCE));
    }
}
