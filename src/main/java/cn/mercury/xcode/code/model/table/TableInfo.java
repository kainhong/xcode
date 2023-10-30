package cn.mercury.xcode.code.model.table;

import cn.mercury.xcode.code.dto.TableInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.database.psi.DbTable;

import java.util.List;

/**
 * 表信息
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */

public class TableInfo {
    /**
     * 原始对象
     */
    @JsonIgnore
    private DbTable obj;

    /**
     * 原始对象（从实体生成）
     *
     * Note: 实际类型是com.intellij.psi.PsiClass，为了避免velocity反射出现ClassNotFound，写为Object类型
     */
    @JsonIgnore
    private Object psiClassObj;

    /**
     * 表名（首字母大写）
     */
    private String name;
    /**
     * 表名前缀
     */
    private String preName;
    /**
     * 注释
     */
    private String comment;
    /**
     * 模板组名称
     */
    private String templateGroupName;
    /**
     * 所有列
     */
    private List<ColumnInfo> fullColumn;
    /**
     * 主键列
     */
    private List<ColumnInfo> pkColumn;
    /**
     * 其他列
     */
    private List<ColumnInfo> otherColumn;
    /**
     * 保存的包名称
     */
    private String savePackageName;
    /**
     * 保存路径
     */
    private String savePath;
    /**
     * 保存的model名称
     */
    private String saveModelName;

    public DbTable getObj() {
        return obj;
    }

    public void setObj(DbTable obj) {
        this.obj = obj;
    }

    public Object getPsiClassObj() {
        return psiClassObj;
    }

    public void setPsiClassObj(Object psiClassObj) {
        this.psiClassObj = psiClassObj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreName() {
        return preName;
    }

    public void setPreName(String preName) {
        this.preName = preName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTemplateGroupName() {
        return templateGroupName;
    }

    public void setTemplateGroupName(String templateGroupName) {
        this.templateGroupName = templateGroupName;
    }

    public List<ColumnInfo> getFullColumn() {
        return fullColumn;
    }

    public void setFullColumn(List<ColumnInfo> fullColumn) {
        this.fullColumn = fullColumn;
    }

    public List<ColumnInfo> getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(List<ColumnInfo> pkColumn) {
        this.pkColumn = pkColumn;
    }

    public List<ColumnInfo> getOtherColumn() {
        return otherColumn;
    }

    public void setOtherColumn(List<ColumnInfo> otherColumn) {
        this.otherColumn = otherColumn;
    }

    public String getSavePackageName() {
        return savePackageName;
    }

    public void setSavePackageName(String savePackageName) {
        this.savePackageName = savePackageName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSaveModelName() {
        return saveModelName;
    }

    public void setSaveModelName(String saveModelName) {
        this.saveModelName = saveModelName;
    }

    public static TableInfo from(DbTable table){
        TableInfoDTO dto = new TableInfoDTO(table);
        return dto.toTableInfo(table);
    }
}
