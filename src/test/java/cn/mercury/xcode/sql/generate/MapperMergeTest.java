package cn.mercury.xcode.sql.generate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.Test;

import java.util.regex.Pattern;

public class MapperMergeTest {

    @Test
    public void test(){
        String xml = ResourceUtil.readUtf8Str("BrandMapper2.xml");

        Pattern regex = Pattern.compile("<resultMap(?<value>([\\s\\S])*?)<\\/resultMap>");

        var matchs = regex.matcher(xml);

        matchs.find();
        int start = matchs.start();
        int end = matchs.end();

        String val = matchs.group("value");
        String xml2 = matchs.replaceFirst( "<resultMap" + val + "\n" + "<!-- test -->\n</resultMap>");

        FileUtil.writeUtf8String(xml2,"f:/1.xml");
    }

    @Test
    public void merge_test(){
        String xml1 = ResourceUtil.readUtf8Str("BrandMapper1.xml");
        String xml2 = ResourceUtil.readUtf8Str("BrandMapper2.xml");
        MapperMerge merge = new MapperMerge(xml1,xml2,null);

        String xml = merge.merge();

        FileUtil.writeUtf8String(xml2,"f:/2.xml");
    }
}
