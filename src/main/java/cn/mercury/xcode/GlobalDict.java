package cn.mercury.xcode;

import java.util.Properties;

public class GlobalDict {

    public static final String TITLE_INFO = "xCode";

    public static final String ID = "cn.mercury.xcode";

    public static String getSystemUsername() {
        Properties properties = System.getProperties();
        return properties.getProperty("user.name");
    }

    public static final String AUTHOR = getSystemUsername();


    public static final String[] DEFAULT_JAVA_TYPE_LIST = new String[]{
            "java.lang.String",
            "java.lang.Integer",
            "java.lang.Long",
            "java.util.Boolean",
            "java.util.Date",
            "java.time.LocalDateTime",
            "java.time.LocalDate",
            "java.time.LocalTime",
            "java.lang.Short",
            "java.lang.Byte",
            "java.lang.Character",
            "java.lang.Character",
            "java.math.BigDecimal",
            "java.math.BigInteger",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.String[]",
            "java.util.List",
            "java.util.Set",
            "java.util.Map",
    };


}
