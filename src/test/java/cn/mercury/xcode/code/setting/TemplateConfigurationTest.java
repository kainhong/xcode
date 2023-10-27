package cn.mercury.xcode.code.setting;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class TemplateConfigurationTest {


    @Test
    public void test(){
        TemplateConfiguration templateConfiguration = new TemplateConfiguration();
        var lst = templateConfiguration.loadTemplate(new File("E:\\work\\xcode\\src\\main\\resources\\extensions\\templates"));

        assertTrue(lst.size() > 0);
    }
}
