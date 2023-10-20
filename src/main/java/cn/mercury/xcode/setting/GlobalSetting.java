package cn.mercury.xcode.setting;

import cn.mercury.xcode.GlobalDict;
import com.intellij.ide.extensionResources.ExtensionsRootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GlobalSetting {

    public static File getScratchPath(@NotNull String pathParam) throws IOException {
        @NotNull PluginId id = Objects.requireNonNull(PluginId.findId(GlobalDict.ID));

        final ScratchFileService scratchFileService = ScratchFileService.getInstance();

        final ExtensionsRootType extensionsRootType = ExtensionsRootType.getInstance();

        final String path = scratchFileService.getRootPath(extensionsRootType) + "/" + id.getIdString() +
                (StringUtil.isEmpty(pathParam) ? "" : "/"
                        + pathParam);

        final File file = new File(path);

        extensionsRootType.extractBundledResources(id, "");

//        if (!file.exists()) {
//            extensionsRootType.extractBundledResources(id, "");
//        }

        return file;
    }

}
