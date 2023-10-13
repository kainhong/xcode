package cn.mercury.xcode.mybatis.extend.dataaccess;


import org.apache.commons.lang.StringUtils;

public abstract class FieldDataAccessStatementProcessor implements IFieldDataAccessStatementProcessor {

    protected final static String shopFlag = " in (select DISTINCT shop_no from shop_brand where (%s) )";


    protected DataAccessProvider getProvider() {
        return new DataAccessProvider();
    }

    protected FieldDataAccessStatementProcessor() {
        String val = System.getProperty("dataaccess.size");
        if (!StringUtils.isEmpty(val))
            max_size = Math.max(1, Integer.parseInt(val));
    }

    @Override
    public String process(String alias, String column, String name, boolean shopMode) {
        // 过滤店铺权限
        if (shopMode && DataAccessEnum.SHOP.equalsIgnoreCase(name)) {
            String orderUnit = processFilterStatement("", "order_unit_no", DataAccessEnum.ORDERUNIT);
            if (orderUnit == null)
                return null;//
            return alias + column + String.format(shopFlag, orderUnit);
        } else {
            return processFilterStatement(alias, column, name);
        }
    }

    protected int max_size = 50;

    protected abstract String processFilterStatement(String alias, String column, String name);


}
