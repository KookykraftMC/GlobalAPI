package com.kookykraftmc.api.global.type;

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
 * Package: com.kookykraftmc.api.global.type
 * Project: GlobalAPI
 */

import com.kookykraftmc.api.global.plugin.KookyHub;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ServerType {

    public static ServerType registerType(ServerType type) {
        types.add(type);
        KookyHub.getInstance().getLogger().log(Level.INFO, "Registered servertype: " + type.getName());
        return type;
    }

    public static ServerType getType(String name) {
        for (ServerType wrapper : types) {
            if (wrapper.getName().equals(name)) {
                return wrapper;
            }
        }
        throw new IllegalArgumentException(name + " is not a correct servertype");
    }

    public static Set<ServerType> getTypes() {
        return types;
    }

    private static Set<ServerType> types = new HashSet<>();
    private String name, prefix;
    private int maxplayers, lowlimit, highlimit;

    public ServerType(String name, String prefix, int maxplayers, int lowlimit, int highlimit) {
        this.name = name;
        this.highlimit = highlimit;
        this.lowlimit = lowlimit;
        this.maxplayers = maxplayers;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getMaxPlayers() {
        return maxplayers;
    }

    public int getLowlimit() {
        return lowlimit;
    }

    public int getHighlimit() {
        return highlimit;
    }

}
