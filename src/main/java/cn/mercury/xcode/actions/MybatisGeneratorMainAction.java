package cn.mercury.xcode.actions;

import cn.mercury.xcode.utils.ProjectUtils;
import cn.mercury.xcode.model.IntellijTableInfo;
import cn.mercury.xcode.utils.DbToolsUtils;
import cn.mercury.xcode.utils.FileUtils;
import cn.mercury.xcode.utils.ModuleUtils;
import com.intellij.database.model.DasObject;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MybatisGeneratorMainAction extends AnAction {
    private static final Logger logger = LoggerFactory.getLogger(MybatisGeneratorMainAction.class);
    private LinkedList<Module> moduleList;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        logger.info(project.getBasePath());

        PsiElement[] tableElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (tableElements == null) {
            logger.error("must select a table.");
            return;
        }

        List<DbTable> dbTables = Stream.of(tableElements).filter(t -> t instanceof DbTable).map(t -> (DbTable) t).collect(Collectors.toList());

        Map<String, DbTable> tableMapping = dbTables.stream().collect(Collectors.toMap(DasObject::getName, a -> a, (a, b) -> a));

        IntellijTableInfo intellijTableInfo = DbToolsUtils.buildIntellijTableInfo(dbTables.get(0));

        this.moduleList = new LinkedList<>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            if (ModuleUtils.existsSourcePath(module)) {
                this.moduleList.add(0, module);
            } else {
                this.moduleList.add(module);
            }
        }

        writeFile(project);
    }

    void writeFile(Project project){
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);

        VirtualFile baseDir = ProjectUtils.getBaseDir(project);

        String savePath = "eagle-deployer/src/main/java";

        @Nullable VirtualFile saveDir = VfsUtil.findRelativeFile(baseDir, savePath.split("/"));

        VirtualFile file = FileUtils.getInstance().createChildFile(project, saveDir, "a.java");

        Document doc = FileUtils.getInstance().writeFileContent(project, file, "a.java", "public class Brand{ }");

        psiDocumentManager.commitDocument(doc);
    }
}
