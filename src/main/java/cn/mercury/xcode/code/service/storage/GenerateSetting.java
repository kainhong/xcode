package cn.mercury.xcode.code.service.storage;

import lombok.Data;

@Data
public class GenerateSetting {
    private String templateGroup;

    private String prefix;

    private String basePackage;

    private String baseModule;

    private String modelModule;

    private String modelPackage;

    private boolean modelEnable;

    private String repositoryModule;

    private String repositoryPackage;

    private boolean repositoryEnable;

    private String serviceModule;

    private String servicePackage;

    private boolean serviceEnable;

    private String managerModule;

    private String managerPackage;

    private boolean managerEnable;

    private String controllerModule;

    private String controllerPackage;

    private boolean controllerEnable;

    private String mybatisConfigurationFile;

    private String mybatisMapperFile;
}
