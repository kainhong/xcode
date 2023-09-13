package cn.mercury.xcode.service;

import cn.mercury.xcode.generate.GenerateOptions;
import cn.mercury.xcode.model.table.TableInfo;
import cn.mercury.xcode.model.template.Template;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * 代码生成服务，Project级别Service
 *

 * @version 1.0.0
 * @since 2018/09/02 12:48
 */
public interface CodeGenerateService {
    /**
     * 获取实例对象
     *
     * @param project 项目对象
     * @return 实例对象
     */
    static CodeGenerateService getInstance(@NotNull Project project) {
        //return ApplicationManager.getApplication().getService(CodeGenerateService.class);
        //return ServiceManager.getService(project, CodeGenerateService.class);
        return project.getService(CodeGenerateService.class);
    }

    /**
     * 生成
     *
     * @param templates       模板
     * @param generateOptions 生成选项
     */
    void generate(Collection<Template> templates, GenerateOptions generateOptions, List<DbTable> tables);

    /**
     * 生成代码
     *
     * @param template  模板
     * @param tableInfo 表信息对象
     * @return 生成好的代码
     */
    String generate(Template template, TableInfo tableInfo);
}
