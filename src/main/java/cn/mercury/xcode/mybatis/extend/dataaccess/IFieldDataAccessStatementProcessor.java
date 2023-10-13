package cn.mercury.xcode.mybatis.extend.dataaccess;

public interface IFieldDataAccessStatementProcessor {
    String process(String alias, String column, String name, boolean shopMode);
}
