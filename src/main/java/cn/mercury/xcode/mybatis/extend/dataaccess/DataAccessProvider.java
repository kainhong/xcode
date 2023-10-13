package cn.mercury.xcode.mybatis.extend.dataaccess;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAccessProvider {

    static List<String> ary = Arrays.asList("'NK01'", "'BD01'", "'NC01'", "'N901'", "'K9001'", "'S9001'");
    static Map<String, List<String>> datas = new HashMap<>();

    {
        datas.put(DataAccessEnum.BRAND, Arrays.asList("AC01", "AD01", "AK01", "AO01", "AS01", "CB01", "CV01", "NF01", "NK01", "OT01", "PU01"));
        datas.put(DataAccessEnum.SHOP, Arrays.asList("NKGY70", "ADAYT2", "ADAY07", "ADAYT2", "NKM008"));
        datas.put(DataAccessEnum.STORE, Arrays.asList("C0241", "D3142", "D3512", "ZIB0FM", "CE21SD"));
        datas.put(DataAccessEnum.ORDERUNIT, Arrays.asList("C049T", "C075T", "3049T", "I913T", "I927T", "I937T", "K770T", "K779T"));
        datas.put(DataAccessEnum.COMPANY, Arrays.asList("K9001", "K9002", "I9001", "I9002", "H9001", "Z9001", "H9001", "M9002"));
        datas.put(DataAccessEnum.ZONE, Arrays.asList("K", "I", "Z", "M", "H", "C"));
    }

    List<String> getAccessData(String name) {
        var items = datas.get(name);
        if (items == null)
            items = ary;
        return items;
    }

    String getAccessDataString(String name) {
        var items = getAccessData(name);
        return "'" + StringUtils.join(items, "','") + "'";
    }

    void clear() {

    }

}
