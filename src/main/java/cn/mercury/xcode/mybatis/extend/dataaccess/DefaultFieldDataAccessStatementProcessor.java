package cn.mercury.xcode.mybatis.extend.dataaccess;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultFieldDataAccessStatementProcessor extends FieldDataAccessStatementProcessor {


    protected String processFilterStatement(String alias, String column, String name) {
        List<String> vals = null;

        vals = getProvider().getAccessData(name);

        if (vals == null)
            return null;
        if (vals.size() > max_size && max_size > 5) {
            List<String> lst = new ArrayList<>();
            List<String> tmp = new ArrayList<>();
            for (int i = 0; i < vals.size(); i++) {
                tmp.add("'" + vals.get(i) + "'");
                if (tmp.size() == max_size) {
                    lst.add(alias + column + " in (" + StringUtils.join(tmp, ",") + ")");
                    tmp.clear();
                }
            }
            if (tmp.size() > 0) {
                lst.add(alias + column + " in (" + StringUtils.join(tmp, ",") + ")");
            }
            return "(" + StringUtils.join(lst, " OR ") + ")";
        } else {
            String script = getProvider().getAccessDataString(name);
            if (script == null)
                return null;
            
            if("".equals(script))
            	script = "''";
            
            return alias + column + " in (" + script + ")";
        }
    }
}

