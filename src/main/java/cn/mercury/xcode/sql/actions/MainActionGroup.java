package cn.mercury.xcode.sql.actions;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.service.SettingsStorageService;
import cn.mercury.xcode.service.TableInfoSettingsService;
import cn.mercury.xcode.utils.CacheDataUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作按钮分组
 *
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class MainActionGroup extends ActionGroup {
    /**
     * 缓存数据工具类
     */
    private CacheDataUtils cacheDataUtils = CacheDataUtils.getInstance();

    /**
     * 是否不存在子菜单
     */
    private boolean notExistsChildren;

    /**
     * 是否分组按钮
     *
     * @return 是否隐藏
     */
    @Override
    public boolean hideIfNoVisibleChildren() {
        return this.notExistsChildren;
    }


    /**
     * 根据右键在不同的选项上展示不同的子菜单
     *
     * @param event 事件对象
     * @return 动作组
     */
    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent event) {
        // 获取当前项目
        Project project = getEventProject(event);
        if (project == null) {
            return getEmptyAnAction();
        }

        //获取选中的PSI元素
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
        DbTable selectDbTable = null;
        if (psiElement instanceof DbTable) {
            selectDbTable = (DbTable) psiElement;
        }
        if (selectDbTable == null) {
            return getEmptyAnAction();
        }
        //获取选中的所有表
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return getEmptyAnAction();
        }
        List<DbTable> dbTableList = new ArrayList<>();
        for (PsiElement element : psiElements) {
            if (!(element instanceof DbTable)) {
                continue;
            }
            DbTable dbTable = (DbTable) element;
            dbTableList.add(dbTable);
        }
        if (dbTableList.isEmpty()) {
            return getEmptyAnAction();
        }

        //保存数据到缓存
        cacheDataUtils.setDbTableList(dbTableList);
        this.notExistsChildren = false;
        return getMenuList();
    }

    /**
     * 初始化注册子菜单项目
     *
     * @return 子菜单数组
     */
    private AnAction[] getMenuList() {
        String mainActionId = "cn.mercury.xcode.generate";
        String configActionId = "cn.mercury.xcode.config";
        ActionManager actionManager = ActionManager.getInstance();
        // 代码生成菜单
        AnAction mainAction;

        mainAction = actionManager.getAction(mainActionId);
        if (mainAction == null) {
            mainAction = new GenerateAction("Generate CodeX");
            actionManager.registerAction(mainActionId, mainAction);
        }
//
//        mainAction = actionManager.getAction(mainActionId + "1");
//        if (mainAction == null) {
//            mainAction = new MainAction("Generate Code2");
//            actionManager.registerAction(mainActionId + "1", mainAction);
//        }

        // 表配置菜单
        AnAction configAction = actionManager.getAction(configActionId);
        if (configAction == null) {
            configAction = new ConfigAction("Config Table");
            actionManager.registerAction(configActionId, configAction);
        }

        AnAction clearConfigAction = new AnAction("Clear Config", "Clear Config", Icons.load("icons/clear.svg")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                List<DbTable> dbTables = CacheDataUtils.getInstance().getDbTableList();
                if (dbTables == null) {
                    return;
                }
                for (DbTable dbTable : dbTables)
                    TableInfoSettingsService.getInstance().removeTableInfo(dbTable);

                SettingsStorageService.getInstance().getState().resetDefaultVal();
                Messages.showInfoMessage("配置信息已重置成功", GlobalDict.TITLE_INFO);
            }
        };
        // 返回所有菜单
        return new AnAction[]{mainAction, configAction, clearConfigAction};
    }


    /**
     * 获取空菜单组
     *
     * @return 空菜单组
     */
    private AnAction[] getEmptyAnAction() {
        this.notExistsChildren = true;
        return AnAction.EMPTY_ARRAY;
    }
}