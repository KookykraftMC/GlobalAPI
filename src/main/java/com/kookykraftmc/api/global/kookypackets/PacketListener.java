package com.kookykraftmc.api.global.kookypackets;

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

import com.kookykraftmc.api.global.kookypackets.messaging.IPluginMessage;

public interface PacketListener {

    void onMessage(PacketInfo info, IPluginMessage message);

    void onConnect(PacketInfo info);

    void onDisconnect(PacketInfo info);

}
