package com.kookykraftmc.api.global.plugin;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) KookyKraftMC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <p>
 * <p>
 * Class information
 * ---------------------
 * Package: com.kookykraftmc.api.global.plugin
 * Project: GlobalAPI
 */

import com.kookykraftmc.api.global.kookypackets.PacketHub;
import com.kookykraftmc.api.global.file.PropertiesFile;
import com.kookykraftmc.api.global.plugin.updater.FileUpdater;
import com.kookykraftmc.api.global.plugin.updater.SQLUpdater;
import com.kookykraftmc.api.global.sql.SQLConnection;
import de.mickare.xserver.XServerPlugin;

import java.util.UUID;

public interface KookyHub<KookyPlugin> extends FileUpdater {

    KookyPlugin getPlugin();

    Object getPlayer(UUID u);

    SQLConnection getConnection();

    PropertiesFile getSQLProperties();

    PacketHub getPacketHub();

    XServerPlugin getXPlugin();

    void onEnable();

    void onDisable();

    void onLoad();

    void endSetup(String reason) throws RuntimeException;

    void logInfo(String info);

    void logSevere(String error);

    void addUpdater(SQLUpdater updater);

    void addUpdater(FileUpdater updater);

    Plugman<KookyPlugin> getPlugman();

}
