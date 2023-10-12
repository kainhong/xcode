package cn.mercury.xcode.sql.generate;

import cn.hutool.core.io.FileUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.sql.setting.TemplateGroup;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class UtilsTest {

    @Test
    public void json_test() throws IOException {
        String json = FileUtil.readUtf8String("E:\\work\\xcode\\src\\main\\resources\\extensions\\templates\\Spring\\.meta.json");
        TemplateGroup group = JsonUtils.fromJson(json, TemplateGroup.class);

        assertNotNull(group);
    }
}
