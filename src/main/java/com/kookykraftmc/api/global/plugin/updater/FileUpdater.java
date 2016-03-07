package com.kookykraftmc.api.global.plugin.updater;

import java.io.File;

public interface FileUpdater {

    File getReplace();

    String getArtifact();

    int getVersion();

    void updateTaskBefore();

    void updateTaskAfter();

}
