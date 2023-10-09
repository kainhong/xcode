package cn.mercury.xcode.generate;

import java.util.HashMap;
import java.util.Map;

public class ParamsSettingConfig {

    final static Map<String, Object> defaultValues;

    public static Map<String, Object> getDefaultValues() {
        return defaultValues;
    }

    static {
        defaultValues = new HashMap<>();

        defaultValues.put("params.queryCondition", "");

        defaultValues.put("page.startrownum", 0);
        defaultValues.put("page.pagesize", 50);
        defaultValues.put("orderbyfield", "");
        defaultValues.put("orderby", "");
    }
}
