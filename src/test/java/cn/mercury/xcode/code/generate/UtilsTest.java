package cn.mercury.xcode.code.generate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.code.setting.TemplateGroup;
import cn.mercury.xcode.model.GlobalConfigEntryGroup;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void json_test() throws IOException {
        String json = FileUtil.readUtf8String("E:\\work\\xcode\\src\\main\\resources\\extensions\\templates\\Spring\\.meta.json");
        TemplateGroup group = JsonUtils.fromJson(json, TemplateGroup.class);

        assertNotNull(group);
    }

    @Test
    public void regex_test(){
        Pattern regex = Pattern.compile("varchar", Pattern.CASE_INSENSITIVE);
        boolean b = regex.matcher("varchar(20)").find();
        assertTrue(b);
    }


    @Test
    public void test() throws IOException {
        var group = JsonUtils.fromJson(ResourceUtil.readUtf8Str("Default/vmConfig.json"), GlobalConfigEntryGroup.class);
        assertNotNull(group);
    }
}
