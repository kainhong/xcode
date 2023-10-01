package cn.mercury.xcode.mybatis.utils;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class StringUtil {

    private StringUtil() {
    }


    public static String encoding(String str, Project project) {
        String encoding = "utf-8";
        return new String(str.getBytes(), Charset.forName(encoding));

    }

    /**
     * Upper case first char string.
     *
     * @param str the str
     * @return the string
     */
    public static String upperCaseFirstChar(String str) {
        if (str == null) {
            return null;
        } else {
            return str.isEmpty() ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }


    /**
     * Lower case first char string.
     *
     * @param str the str
     * @return the string
     */
    public static String lowerCaseFirstChar(String str) {
        if (str == null) {
            return null;
        } else {
            return str.isEmpty() ? str : str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }


    /**
     * convert string from slash style to camel style, such as my_course will convert to MyCourse
     *
     * @param str the str
     * @return string
     */
    public static String dbStringToCamelStyle(String str) {
        if (str != null) {
            str = str.toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            for (int i = 1; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c != '_') {
                    sb.append(c);
                } else {
                    if (i + 1 < str.length()) {
                        sb.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
                        i++;
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    /**
     * 驼峰转下划线
     * @param camelStr
     * @return
     */
    public static String camelToSlash(String camelStr){
        String[] strings = splitByCharacterType(camelStr, true);
        return Arrays.stream(strings).map(StringUtil::lowerCaseFirstChar).collect(Collectors.joining("_"));
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        } else if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            char[] c = str.toCharArray();
            List<String> list = new ArrayList();
            int tokenStart = 0;
            int currentType = Character.getType(c[tokenStart]);

            for(int pos = tokenStart + 1; pos < c.length; ++pos) {
                int type = Character.getType(c[pos]);
                if (type != currentType) {
                    if (camelCase && type == 2 && currentType == 1) {
                        int newTokenStart = pos - 1;
                        if (newTokenStart != tokenStart) {
                            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                            tokenStart = newTokenStart;
                        }
                    } else {
                        list.add(new String(c, tokenStart, pos - tokenStart));
                        tokenStart = pos;
                    }

                    currentType = type; }
            }

            list.add(new String(c, tokenStart, c.length - tokenStart));
            return (String[])list.toArray(new String[list.size()]);
        }
    }

}
