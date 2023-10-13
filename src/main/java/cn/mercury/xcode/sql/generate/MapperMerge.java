package cn.mercury.xcode.sql.generate;

import cn.mercury.xcode.model.table.TableInfo;
import cn.mercury.xcode.utils.NameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//<(select*)\b[^>]*>(.*?)</\1>
//<[^>]+/>
public class MapperMerge {

    final List<String> tags = Arrays.asList("sql,column_list"
            ,"sql,Base_Column_List"
            ,"insert,insert"
            ,"insert,insertSelective"
            ,"insert,insertBatch"
            ,"insert,insertOrUpdateBatch"
            ,"update,updateByPrimaryKeySelective"
            ,"update,updateByPrimaryKey"
            ,"update,update");

    private String oldMapper;
    private final String newMapper;
    private final TableInfo table;

    private List<String> newColumns;

    public MapperMerge(String oldMapper, String newMapper, TableInfo table) {
        this.oldMapper = oldMapper;
        this.newMapper = newMapper;
        this.table = table;
    }

    private Pattern getPattern(String tagName, String id) {
        return Pattern.compile(String.format("<(%s)([\\s\\S])+id=\\s?\"%s\"[^>]*>([\\s\\S])*?</\\1>", tagName, id),
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    }

    public String merge() {
        this.mergeResultMap();

        this.mergeElement();

        this.mergeConditionElement();

        return this.oldMapper;
    }

    private void mergeConditionElement() {
        if (this.newColumns == null || this.newColumns.size() == 0)
            return;
        NameUtils utils = NameUtils.getInstance();

        Pattern regex = getPattern("sql", "condition");
        Matcher matcher = regex.matcher(oldMapper);
        if (!matcher.find())
            return;

        String oldValue = matcher.group();
        int index = oldValue.lastIndexOf("</if>");
        String first = oldValue.substring(0,index);
        String last = oldValue.substring(index);

        String express = "";
        for(String col : this.newColumns ){
            String name = utils.getJavaName(col);
            express += String.format("\n\t\t\t<if test=\"null!=params.%s  and ''!=params.%s \">\n" +
                        "\t\t\t\tAND `%s`=#{params.%s} \n" +
                        "\t\t\t</if>\n",name,name,col,name);
        }
        String text = Matcher.quoteReplacement( first + "\n" + express + "\n" + last);

        this.oldMapper = matcher.replaceFirst(text);
    }

    private void mergeElement() {
        for (String tag : this.tags) {
            String[] vals = tag.split(",");
            Pattern regex = getPattern(vals[0], vals[1]);
            Matcher matcher = regex.matcher(oldMapper);
            if (!matcher.find())
                continue;

            String newValue = getMapperValue(regex, newMapper);

            if (StringUtils.isEmpty(newValue))
                continue;

            this.oldMapper = matcher.replaceFirst( Matcher.quoteReplacement(newValue));
        }
    }

    private void mergeResultMap() {
        Pattern regex = getPattern("resultMap", "baseResultMap");
        Matcher matcher = regex.matcher(oldMapper);
        if (!matcher.find())
            return;

        String oldValue = matcher.group();
        String newValue = getMapperValue(regex, newMapper);

        this.newColumns = matchColumns(oldValue, newValue);

        if (StringUtils.isEmpty(newValue))
            return;

        this.oldMapper = matcher.replaceFirst(newValue);
    }

    private String getMapperValue(Pattern regex, String xml) {
        Matcher matcher = regex.matcher(xml);
        if (!matcher.find())
            return null;
        return matcher.group();
    }

    private List<String> matchColumns(String oldValue, String newValue) {
        List<String> oldCols = getColumns(oldValue);
        List<String> newCols = getColumns(newValue);

        return newCols.stream().filter(c -> !oldCols.contains(c)).collect(Collectors.toList());
    }

    private List<String> getColumns(String val) {
        Pattern regex = Pattern.compile("<(result)\\s+column=\"(?<column>\\w+)\"\\s+[^>]*/>");
        Matcher matcher = regex.matcher(val);
        List<String> cols = new ArrayList<>();
        while (matcher.find()) {
            String col = matcher.group("column");
            cols.add(col);
        }
        return cols;
    }

}
