package cn.mercury.xcode.mybatis.extend.dataaccess;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAccessStatementProcessor {
    protected boolean shopMode;

    private static final Pattern regex = Pattern.compile("((--)?\\W+(AND|OR)?\\W?\\(?\\W?)?@(\\w+\\.)?(\\w+)(!\\w+)?",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern regexAutoWired = Pattern.compile("--\\W?+@AutoWired+\\W?(\\w+)?", Pattern.CASE_INSENSITIVE);

    protected DataAccessProvider dataAccessProvider = new DataAccessProvider();

    private String[] fields = new String[]{DataAccessEnum.BRAND, DataAccessEnum.ORDERUNIT, DataAccessEnum.SHOP,
            DataAccessEnum.STORE, DataAccessEnum.SUPPLIER, DataAccessEnum.ORGAN};

    private final IFieldDataAccessStatementProcessor statementProcessor;


    public DataAccessStatementProcessor(boolean shopMode, boolean isMixed) {
        this.shopMode = shopMode;

        if (isMixed) {
            statementProcessor = new JoinTypeFieldDataAccessStatementProcessor();
        } else
            statementProcessor = new DefaultFieldDataAccessStatementProcessor();
    }

    public String Process(String script) {
        return scan(script);
    }

    private String scan(String script) {
        String rn = System.getProperty("line.separator");
        Scanner scanner = new Scanner(script);
        StringBuffer sb = new StringBuffer();
        boolean changed = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();
            if (line.length() == 0)
                continue;
            String tmp = replace(line);
            if (tmp == null)
                sb.append(line);
            else {
                sb.append(tmp);
                changed = true;
            }
            sb.append(rn);
        }
        scanner.close();
        if (changed)
            return sb.toString();
        else
            return null;
    }

    private String replace(String script) {
        Matcher m = regex.matcher(script);
        StringBuffer sb = new StringBuffer();
        boolean finded = m.find();
        if (!finded)
            return null;

        while (finded) {
            // String express = m.group(0);
            String op = m.group(1);
            String alias = m.group(4);
            String name = m.group(5);
            String field = m.group(6);
            if (field == null)
                field = name;
            else
                field = field.substring(1);

            if (field.length() < 2) { // mysql中的特殊语法需要排除 @b:=@b+1;
                return null;
            }
            if (null == alias)
                alias = "";
            if (op == null)
                op = "";
            else
                op = op.replaceFirst("--", "");
            String statement = getStatement(alias, field, name);

            statement = op + " " + statement;

            m.appendReplacement(sb, statement);
            finded = m.find();
        }
        m.appendTail(sb);
        String result = sb + System.getProperty("line.separator");
        return result;
    }

    // private DataAccessProvider defaultDataAccessProvider;
    protected DataAccessProvider getProvider() {
        return dataAccessProvider;
    }

    protected String getStatement(String alias, String column, String name) {
        // 过滤店铺权限
        return this.statementProcessor.process(alias, column, name, this.shopMode);
    }


}
