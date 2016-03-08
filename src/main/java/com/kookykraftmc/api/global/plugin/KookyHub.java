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
import com.kookykraftmc.api.global.plugin.updater.Updatetask;
import com.kookykraftmc.api.global.sql.SQLConnection;
import com.kookykraftmc.api.global.sql.SQLUtil;
import com.kookykraftmc.api.global.type.ServerType;
import de.mickare.xserver.XServerPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class KookyHub<KookyPlugin> implements FileUpdater {

    public static KookyHub getInstance() {
        return instance;
    }

    private static KookyHub<?> instance;
    private final File sqlpropertiesfile = new File("sql.properties");
    private PropertiesFile sqlproperties;
    private SQLConnection connection;
    private PacketHub hub;
    private Set<FileUpdater> fileupdaters = new HashSet<>();
    private Set<SQLUpdater> sqlUpdaters = new HashSet<>();

    public KookyHub() {
        instance = this;
    }

    public final void onEnable() {

        getLogger().log(Level.INFO, "Creating PacketHub");

        hub = new PacketHub();

        getLogger().log(Level.INFO, "Enabling the plugin...");

        onKookyEnable();

        runTaskLater(new Runnable() {
            public void run() {
                getLogger().log(Level.INFO, "Registering PacketHub...");
                try {
                    hub.register(getInstance());
                } catch (Exception ex) {
                    getLogger().log(Level.SEVERE, "Could not register packethub", ex);
                }
            }
        }, bungee() ? 1L : 0L, TimeUnit.SECONDS);

        getLogger().log(Level.INFO, "Finding updates table");

        try {
            if (!SQLUtil.tableExists(getConnection(), "updates")) {
                getLogger().log(Level.INFO, "Creating updates table");
                getConnection().executeSQL("CREATE TABLE `updates` (" +
                        "`artifact` VARCHAR(32) NOT NULL," +
                        "`version` INT(3) NOT NULL," +
                        "`url` VARCHAR(255) NOT NULL," +
                        "PRIMARY KEY (`artifact`)," +
                        "UNIQUE INDEX `UNIQUE KEY` (`artifact`)," +
                        "UNIQUE INDEX `UNIQUE URL` (`url`)," +
                        "INDEX `KEY` (`artifact`)" +
                        ")" +
                        ";");
                //TODO - SQL API
            }
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Could not create updates table", ex);
        }


        runTaskLater(new Runnable() {
            public void run() {
                try {
                    new Updatetask(getConnection(), "updates", fileupdaters, sqlUpdaters) {
                        String name = getName();

                        public void logInfo(String s) {
                            System.out.println("[" + name + "] " + s);
                        }

                        public void logSevere(String s) {
                            System.err.println("[" + name + "] " + s);
                        }

                        public void update(Runnable r) {
                            KookyHub.this.update(r);
                        }

                        public File getFile() {
                            return getReplace();
                        }
                    };
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Could not run updater", e);
                }
                runTaskLater(this, 5L, TimeUnit.MINUTES);
            }
        }, 30L, TimeUnit.SECONDS);
    }

    public final void onDisable() {
        getLogger().log(Level.INFO, "Plugin is now disabling...");

        onKookyDisable();

        getLogger().log(Level.INFO, "Closing connection to MySQL");

        try {
            getConnection().closeConnection();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Could not close database connection", e);
        }

        getPacketHub().unregisterThis();

        if (Updatetask.instance != null) {
            try {
                Updatetask.instance.interrupt();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Could not interrupt updatetask thread", e);
            }
            Updatetask.instance = null;
        }
    }

    public final void onLoad() {
        getLogger().log(Level.INFO,"Adding updaters");

        addUpdater(this);
        addUpdater(new SQLUpdater() {
            public void update(SQLConnection connection) throws SQLException, ClassNotFoundException {
                ResultSet set = null;
                try {
                    set = SQLUtil.query(getConnection(), "servertypes", "*", new SQLUtil.Where("1"));
                    final Set<ServerType> serverTypeObjects = new HashSet<>();
                    while (set.next()) {
                        serverTypeObjects.add(new ServerType(set.getString("name"), set.getString("prefix"), set.getInt("maxplayer"), set.getInt("low-limit"), set.getInt("high-limit")));
                    }
                    runTaskLater(new Runnable() {
                        public void run() {
                            synchronized (ServerType.getTypes()) {
                                ServerType.getTypes().clear();
                                for (ServerType serverType : serverTypeObjects) {
                                    ServerType.registerType(serverType);
                                }
                            }
                        }
                    }, 0L, TimeUnit.MILLISECONDS);
                } finally {
                    try {
                        if (set != null) {
                            set.close();
                        }
                    } catch (Throwable throwable) {
                    }
                }
            }

            public String getName() {
                return "ServerTypeUpdater";
            }
        });

        //Loading properties
        getLogger().log(Level.INFO, "Loading SQL Properties");

        if (!sqlpropertiesfile.exists()) {

            try {
                PropertiesFile.generateFresh(sqlpropertiesfile, new String[]{"hostname", "port", "username", "password", "database"}, new String[]{"localhost", "3306", "root", "NONE", "bubbleserver"});
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Could not generate fresh properties file");
            }
            endSetup("Could not find properties file");
        } else {
            try {
                sqlproperties = new PropertiesFile(sqlpropertiesfile);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not load SQL properties file", e);
                endSetup("Exception occurred when loading properties");
            }
        }


        getLogger().log(Level.INFO, "Finding database information...");

        String temp;
        try {
            connection = new SQLConnection(sqlproperties.getString("hostname"), sqlproperties.getNumber("port").intValue(), sqlproperties.getString("database"), sqlproperties.getString("username"), (temp = sqlproperties.getString("password")).equals("NONE") ? null : temp);
        } catch (ParseException ex) {
            getLogger().log(Level.WARNING, "Could not load database information", ex);
            endSetup("Invalid database port");
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Could not load database information", ex);
            endSetup("Invalid configuration");
        }

        //Connecting to MySQL
        getLogger().log(Level.INFO, "Connecting to MySQL...");

        try {
            getConnection().openConnection();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not connect to MySQL", e);
            endSetup("Could not establish connection to database");
        }

        //Loading server types

        getLogger().log(Level.INFO, "Finding server types...");

        try {
            if (!SQLUtil.tableExists(getConnection(), "servertypes")) {
                //TODO - SQL API
                connection.executeSQL("CREATE TABLE `servertypes` (" +
                        "`name` VARCHAR(32) NOT NULL," +
                        "`prefix` VARCHAR(16) NOT NULL," +
                        "`maxplayer` INT(3) NOT NULL," +
                        "`low-limit` INT(3) NOT NULL," +
                        "`high-limit` INT(3) NOT NULL," +
                        "PRIMARY KEY (`name`)," +
                        "UNIQUE INDEX `UNIQUE` (`name`, `prefix`)," +
                        "INDEX `NAME KEY` (`name`)," +
                        "INDEX `PREFIX` (`prefix`)" +
                        ")" +
                        ";");
            }
            ResultSet set = SQLUtil.query(getConnection(), "servertypes", "*", new SQLUtil.Where("1"));
            while (set.next()) {
                ServerType.registerType(new ServerType(set.getString("name"), set.getString("prefix"), set.getInt("maxplayer"), set.getInt("low-limit"), set.getInt("high-limit")));
            }
            set.close();
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Could not find server types", ex);
            endSetup("Could not find server types");
        }

        getLogger().log(Level.INFO, "Saving XServer Configuration...");


        //Normal load stuff
        saveXServerDefaults();

        getLogger().log(Level.INFO, "Loading plugin...");

        onKookyLoad();

        getLogger().log(Level.INFO, "Load complete");
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public PropertiesFile getSQLProperties() {
        return sqlproperties;
    }

    public PacketHub getPacketHub() {
        return hub;
    }

    public void addUpdater(FileUpdater updater) {
        fileupdaters.add(updater);
    }

    public void addUpdater(SQLUpdater updater) {
        sqlUpdaters.add(updater);
    }

    public abstract void update(Runnable r);

    public abstract boolean bungee();

    public abstract void runTaskLater(Runnable r, long l, TimeUnit timeUnit);

    public abstract void saveXServerDefaults();

    public abstract void onKookyEnable();

    public abstract void onKookyDisable();

    public abstract void onKookyLoad();

    public abstract KookyPlugin getPlugin();

    public abstract Object getPlayer(UUID u);

    public abstract Logger getLogger();

    public abstract XServerPlugin getXPlugin();

    public void endSetup(String s) {
        getLogger().log(Level.SEVERE, s);
        stop();
        throw new IllegalArgumentException("Disabling... " + s);
    }

    public abstract void stop();

}
