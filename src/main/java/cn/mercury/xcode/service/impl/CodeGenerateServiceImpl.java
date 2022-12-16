package cn.mercury.xcode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.SaveFile;
import cn.mercury.xcode.generate.GenerateContext;
import cn.mercury.xcode.generate.GenerateOptions;
import cn.mercury.xcode.model.SettingsStorage;
import cn.mercury.xcode.model.table.TableInfo;
import cn.mercury.xcode.model.template.Template;
import cn.mercury.xcode.service.CodeGenerateService;
import cn.mercury.xcode.service.SettingsStorageService;
import cn.mercury.xcode.service.TableInfoSettingsService;
import cn.mercury.xcode.utils.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.database.util.DbUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ReflectionUtil;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author makejava
 * @version 1.0.0
 * @since 2018/09/02 12:50
 */
public class CodeGenerateServiceImpl implements CodeGenerateService {
    /**
     * 项目对象
     */
    private Project project;
    /**
     * 模型管理
     */
    private ModuleManager moduleManager;
    /**
     * 表信息服务
     */
    private TableInfoSettingsService tableInfoService;

    /**
     * 导入包时过滤的包前缀
     */
    private static final String FILTER_PACKAGE_NAME = "java.lang";

    public CodeGenerateServiceImpl(Project project) {
        this.project = project;
        this.moduleManager = ModuleManager.getInstance(project);
        this.tableInfoService = TableInfoSettingsService.getInstance();
    }

    /**
     * 生成
     *
     * @param templates       模板
     * @param generateOptions 生成选项
     */
    @Override
    public void generate(Collection<Template> templates, GenerateOptions generateOptions, List<DbTable> tables) {
        // 获取选中表信息
        TableInfo selectedTableInfo;
        List<TableInfo> tableInfoList;

        selectedTableInfo = tableInfoService.getTableInfo(tables.get(0));
        tableInfoList = tables.stream().map(item -> tableInfoService.getTableInfo(item)).collect(Collectors.toList());

        // 校验选中表的保存路径是否正确
        if (StringUtils.isEmpty(selectedTableInfo.getSavePath())) {
            if (selectedTableInfo.getObj() != null) {
                Messages.showInfoMessage(selectedTableInfo.getObj().getName() + "表配置信息不正确，请尝试重新配置", GlobalDict.TITLE_INFO);
            } else {
                Messages.showInfoMessage("配置信息不正确，请尝试重新配置", GlobalDict.TITLE_INFO);
            }
            return;
        }
        // 将未配置的表进行配置覆盖
        TableInfo finalSelectedTableInfo = selectedTableInfo;
        tableInfoList.forEach(tableInfo -> {
            if (StringUtils.isEmpty(tableInfo.getSavePath())) {
                tableInfo.setSaveModelName(finalSelectedTableInfo.getSaveModelName());
                tableInfo.setSavePackageName(finalSelectedTableInfo.getSavePackageName());
                tableInfo.setSavePath(finalSelectedTableInfo.getSavePath());
                tableInfo.setPreName(finalSelectedTableInfo.getPreName());
                tableInfoService.saveTableInfo(tableInfo);
            }
        });
        // 如果使用统一配置，直接全部覆盖
        if (Boolean.TRUE.equals(generateOptions.getUnifiedConfig())) {
            tableInfoList.forEach(tableInfo -> {
                tableInfo.setSaveModelName(finalSelectedTableInfo.getSaveModelName());
                tableInfo.setSavePackageName(finalSelectedTableInfo.getSavePackageName());
                tableInfo.setSavePath(finalSelectedTableInfo.getSavePath());
                tableInfo.setPreName(finalSelectedTableInfo.getPreName());
            });
        }

        // 生成代码
        generate(templates, tableInfoList, generateOptions, null);
    }

    /**
     * 生成代码，并自动保存到对应位置
     *
     * @param templates       模板
     * @param tableInfoList   表信息对象
     * @param generateOptions 生成配置
     * @param otherParam      其他参数
     */
    public void generate(Collection<Template> templates, Collection<TableInfo> tableInfoList, GenerateOptions generateOptions, Map<String, Object> otherParam) {
        if (CollectionUtil.isEmpty(templates) || CollectionUtil.isEmpty(tableInfoList)) {
            return;
        }
        // 处理模板，注入全局变量（克隆一份，防止篡改）
        templates = CloneUtils.cloneByJson(templates, new TypeReference<ArrayList<Template>>() {
        });
        // 生成代码
        for (TableInfo tableInfo : tableInfoList) {
            // 表名去除前缀
            if (!StringUtils.isEmpty(tableInfo.getPreName()) && tableInfo.getObj().getName().startsWith(tableInfo.getPreName())) {
                String newName = tableInfo.getObj().getName().substring(tableInfo.getPreName().length());
                tableInfo.setName(NameUtils.getInstance().getClassName(newName));
            }

            generateTable(templates, generateOptions, otherParam, tableInfo);
        }
    }

    private void generateTable(Collection<Template> templates, GenerateOptions generateOptions, Map<String, Object> otherParam, TableInfo tableInfo) {
        // 构建参数
        Map<String, Object> param = getDefaultParam();
        // 其他参数
        if (otherParam != null) {
            param.putAll(otherParam);
        }

        // 表信息对象
        param.put("tableInfo", tableInfo);
        // 设置模型路径与导包列表
        setModulePathAndImportList(param, tableInfo);
        // 设置额外代码生成服务
        param.put("generateService", new ExtraCodeGenerateUtils(this, tableInfo, generateOptions));
        String packagePath = tableInfo.getSavePackageName().replace(".", "/");

        for (Template template : templates) {
            GenerateContext context = new GenerateContext();
            context.setWriteFile(true);
            context.setReformat(generateOptions.getReFormat());
            // 默认名称
            context.setFileName(tableInfo.getName() + "Default.java");
            // 默认路径
            context.setSavePath(tableInfo.getSavePath());
            // 设置回调对象
            String path = getSavePath(template, generateOptions, tableInfo.getSavePath());
            param.put("context", context);
            param.put("modulePath", path);

            if (generateOptions.isMixed())
                param.put("sourcePath", path + "/src/main/java/" + packagePath + "/" + template.getPackageSuffix());
            else
                param.put("sourcePath", path);

            context.setSavePath(path);

            // 开始生成
            String code = VelocityUtils.generate(template, param);

            // 设置一个默认保存路径与默认文件名
            new SaveFile(project, code, context, generateOptions).write();
        }
    }

    private String getSavePath(Template template, GenerateOptions generateOptions, String savePath) {
        String path = savePath;
        path = path.replace("\\", "/");
        // 针对相对路径进行处理
        if (path.startsWith(".")) {
            path = project.getBasePath() + path.substring(1);
        }

        if (!generateOptions.isMixed())
            return path;

        if (StringUtils.isEmpty(template.getPath()))
            return path;

        return path + "/" + String.format(template.getPath(), generateOptions.getModuleName()) + "/";
    }

    /**
     * 生成代码
     *
     * @param template  模板
     * @param tableInfo 表信息对象
     * @return 生成好的代码
     */
    @Override
    public String generate(Template template, TableInfo tableInfo) {
        // 获取默认参数
        Map<String, Object> param = getDefaultParam();
        // 表信息对象，进行克隆，防止篡改
        param.put("tableInfo", tableInfo);
        // 设置模型路径与导包列表
        setModulePathAndImportList(param, tableInfo);

        return VelocityUtils.generate(template, param);
    }

    /**
     * 设置模型路径与导包列表
     *
     * @param param     参数
     * @param tableInfo 表信息对象
     */
    private void setModulePathAndImportList(Map<String, Object> param, TableInfo tableInfo) {
        Module module = null;
        if (!StringUtils.isEmpty(tableInfo.getSaveModelName())) {
            module = this.moduleManager.findModuleByName(tableInfo.getSaveModelName());
        }
        if (module != null) {
            // 设置modulePath
            param.put("modulePath", ModuleUtils.getModuleDir(module).getPath());
        }
        // 设置要导入的包
        param.put("importList", getImportList(tableInfo));
    }

    /**
     * 获取默认参数
     *
     * @return 参数
     */
    private Map<String, Object> getDefaultParam() {
        // 系统设置
        SettingsStorage settings = SettingsStorageService.getSettingsStorage();
        Map<String, Object> param = new HashMap<>(20);
        // 作者
        param.put("author", settings.getAuthor());
        //工具类
        param.put("tool", GlobalTool.getInstance());
        param.put("time", TimeUtils.getInstance());
        // 项目路径
        param.put("projectPath", project.getBasePath());
        // Database数据库工具
        param.put("dbUtil", ReflectionUtil.newInstance(DbUtil.class));
        param.put("dasUtil", ReflectionUtil.newInstance(DasUtil.class));
        return param;
    }

    /**
     * 获取导入列表
     *
     * @param tableInfo 表信息对象
     * @return 导入列表
     */
    private Set<String> getImportList(TableInfo tableInfo) {
        // 创建一个自带排序的集合
        Set<String> result = new TreeSet<>();
        tableInfo.getFullColumn().forEach(columnInfo -> {
            if (!columnInfo.getType().startsWith(FILTER_PACKAGE_NAME)) {
                String type = NameUtils.getInstance().getClsFullNameRemoveGeneric(columnInfo.getType());
                result.add(type);
            }
        });
        return result;
    }
}
