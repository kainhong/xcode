package cn.mercury.xcode.mybatis;

public class MybatisDataHolder {

    static MybatisDataHolder inc = new MybatisDataHolder();

    private MybatisDataHolder() {

    }

    public static MybatisDataHolder getInstance() {
        return inc;
    }
}
