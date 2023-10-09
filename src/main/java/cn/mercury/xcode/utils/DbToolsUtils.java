package cn.mercury.xcode.utils;

import cn.mercury.xcode.model.IntellijColumnInfo;
import cn.mercury.xcode.model.IntellijTableInfo;
import com.intellij.database.model.*;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import com.rits.cloning.Cloner;
import org.jetbrains.annotations.NotNull;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Db tools utils.
 */
public class DbToolsUtils {
    private static final Cloner MY_CLONER = new Cloner();

    /**
     * Build intellij table info intellij table info.
     *
     * @param currentTable the current table
     * @return the intellij table info
     */
    public static IntellijTableInfo buildIntellijTableInfo(DbTable currentTable) {
        IntellijTableInfo tableInfo = new IntellijTableInfo();
        tableInfo.setTableName(currentTable.getName());
        tableInfo.setDatabaseType(extractDatabaseTypeFromUrl(currentTable));
        tableInfo.setTableRemark(currentTable.getComment());
        tableInfo.setTableType(currentTable.getTypeName());
        List<IntellijColumnInfo> intellijColumnInfos = new ArrayList<>();
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(currentTable);
        for (DasColumn column : columns) {
            IntellijColumnInfo columnInfo = convertColumnToIntellijColumnInfo(column, tableInfo.getDatabaseType());
            intellijColumnInfos.add(columnInfo);
        }

        tableInfo.setColumnInfos(intellijColumnInfos);
        List<IntellijColumnInfo> primaryColumnInfos = new ArrayList<>();
        DasTableKey primaryKey = DasUtil.getPrimaryKey(currentTable);
        if (primaryKey != null) {
            MultiRef<? extends DasTypedObject> columnsRef = primaryKey.getColumnsRef();
            MultiRef.It<? extends DasTypedObject> iterate = columnsRef.iterate();
            short s = 0;
            while (iterate.hasNext()) {
                String columnName = iterate.next();
                for (IntellijColumnInfo intellijColumnInfo : intellijColumnInfos) {
                    if (columnName.equals(intellijColumnInfo.getName())) {
                        IntellijColumnInfo info = MY_CLONER.deepClone(intellijColumnInfo);
                        info.setKeySeq(s);
                        primaryColumnInfos.add(info);
                        s++;
                        break;
                    }
                }
            }

        }

        tableInfo.setPrimaryKeyColumns(primaryColumnInfos);
        return tableInfo;
    }


    private static String extractDatabaseTypeFromUrl(DbTable currentTable) {
        String url = currentTable.getDataSource().getConnectionConfig().getUrl();
        return extractDatabaseTypeFromUrl(url);
    }

    /**
     * Extract database type from url string.
     *
     * @param url the url
     * @return the string
     */
    public static String extractDatabaseTypeFromUrl(String url) {
        if (url == null) {
            return "";
        } else {
            url = url.toLowerCase();
            if (url.contains(":mysql")) {
                return "MySql";
            } else if (url.contains(":oracle")) {
                return "Oracle";
            } else if (url.contains(":postgresql")) {
                return "PostgreSQL";
            } else if (url.contains(":sqlserver")) {
                return "SqlServer";
            } else {
                return url.contains(":sqlite") ? "Sqlite" : "";
            }
        }
    }

    /**
     * Convert column to intellij column info intellij column info.
     *
     * @param column       the column
     * @param databaseType the database type
     * @return the intellij column info
     */
    @NotNull
    public static IntellijColumnInfo convertColumnToIntellijColumnInfo(DasColumn column, String databaseType) {
        IntellijColumnInfo columnInfo = new IntellijColumnInfo();
        columnInfo.setName(column.getName());
        @NotNull DataType dataType = column.getDasType().toDataType();

        columnInfo.setDataType(convertTypeNameToJdbcType(dataType.typeName, dataType.size, databaseType));
        if (DasUtil.isAutoGenerated(column)) {
            columnInfo.setGeneratedColumn(true);
        }

        if (DasUtil.isAutoGenerated(column)) {
            columnInfo.setAutoIncrement(true);
        }

        columnInfo.setSize(dataType.getLength());
        columnInfo.setDecimalDigits(dataType.getScale());
        columnInfo.setRemarks(column.getComment());
        columnInfo.setColumnDefaultValue(column.getDefault());
        columnInfo.setNullable(!column.isNotNull());
        columnInfo.setKeySeq(column.getPosition());

        return columnInfo;
    }

    /**
     * Convert type name to jdbc type int.
     *
     * @param jdbcTypeName the jdbc type name
     * @param size         the size
     * @param databaseType the database type
     * @return the int
     */
    public static int convertTypeNameToJdbcType(String jdbcTypeName, int size, String databaseType) {
        if (StringUtil.isEmpty(jdbcTypeName)) {
            return Types.OTHER;
        }

        String fixed = jdbcTypeName.toUpperCase();
        if (fixed.contains("BIGINT")) {
            return Types.BIGINT;
        } else if (fixed.contains("TINYINT")) {
            return Types.TINYINT;
        } else if (fixed.contains("LONGVARBINARY")) {
            return Types.LONGVARBINARY;
        } else if (fixed.contains("VARBINARY")) {
            return Types.VARBINARY;
        } else if (fixed.contains("LONGVARCHAR")) {
            return Types.LONGVARCHAR;
        } else if (fixed.contains("SMALLINT")) {
            return Types.SMALLINT;
        } else if (fixed.contains("DATETIME")) {
            return Types.TIMESTAMP;
        } else if (fixed.equals("DATE") && "Oracle".equals(databaseType)) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("NUMBER")) {
            return Types.DECIMAL;
        } else if (fixed.contains("BOOLEAN")) {
            return Types.BOOLEAN;
        } else if (fixed.contains("BINARY")) {
            return Types.VARBINARY;
        } else if (fixed.contains("BIT")) {
            return Types.BIT;
        } else if (fixed.contains("BOOL")) {
            return Types.BOOLEAN;
        } else if (fixed.contains("DATE")) {
            return Types.DATE;
        } else if (fixed.contains("TIMESTAMP")) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("TIME")) {
            return Types.TIME;
        } else if (!fixed.contains("REAL") && !fixed.contains("NUMBER")) {
            if (fixed.contains("FLOAT")) {
                return Types.FLOAT;
            } else if (fixed.contains("DOUBLE")) {
                return Types.DOUBLE;
            } else if ("CHAR".equals(fixed)) {
                return Types.CHAR;
            } else if (fixed.equals("INT")) {
                return Types.INTEGER;
            } else if (fixed.contains("DECIMAL")) {
                return Types.DECIMAL;
            } else if (fixed.contains("NUMERIC")) {
                return Types.NUMERIC;
            } else if (!fixed.contains("CHAR") && !fixed.contains("TEXT")) {
                if (fixed.contains("BLOB")) {
                    return Types.BLOB;
                } else if (fixed.contains("CLOB")) {
                    return Types.CLOB;
                } else {
                    return fixed.contains("REFERENCE") ? Types.REF : Types.OTHER;
                }
            } else {
                return Types.VARCHAR;
            }
        } else {
            return Types.REAL;
        }

    }

}
