package com.kookykraftmc.api.global.plugin;

import com.kookykraftmc.api.global.kookypackets.PacketHub;
import com.kookykraftmc.api.global.file.PropertiesFile;
import com.kookykraftmc.api.global.plugin.updater.FileUpdater;
import com.kookykraftmc.api.global.plugin.updater.SQLUpdater;
import com.kookykraftmc.api.global.plugin.updater.Updatetask;
import com.kookykraftmc.api.global.sql.SQLConnection;
import com.kookykraftmc.api.global.sql.SQLUtil;
import com.kookykraftmc.api.global.type.ServerType;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public abstract class KookyHubObject<KookyPlugin> implements KookyHub<KookyPlugin> {

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

    public KookyHubObject() {
        logInfo("Assigning instance...");
        instance = this;
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
    }

    public final void onEnable() {

        logInfo("Creating PacketHub");

        hub = new PacketHub();

        logInfo("PacketHub has been created");

        logInfo("Enabling the plugin...");

        onKookyEnable();

        logInfo("The plugin has been enabled");

        logInfo("Registering PacketHub...");
        runTaskLater(new Runnable() {
            public void run() {
                logInfo("Registering PacketHub...");
                try {
                    hub.register(getInstance());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                logInfo("PacketHub has been registered");
            }
        }, bungee() ? 1L : 0L, TimeUnit.SECONDS);

        logInfo("Finding updates table");

        try {
            if (!SQLUtil.tableExists(getConnection(), "updates")) {
                logInfo("Creating updates table");
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
                /*
                SQLUtil.createTable(getConnection(),"updates",
                        new ImmutableMap.Builder<String,Map.Entry<SQLUtil.SQLDataType,Integer>>()
                                .put("artifact",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT,32))
                                .put("version",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT,3))
                                .put("url", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT,-1))
                                .build());
                                */
            }
        } catch (Exception ex) {
            logSevere(ex.getMessage());
            logSevere("Error creating updates table");
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
                            KookyHubObject.this.update(r);
                        }

                        public File getFile() {
                            return getReplace();
                        }
                    };
                } catch (Exception e) {
                    logSevere(e.getMessage());
                    logSevere("Error running updater");
                }
                runTaskLater(this, 5L, TimeUnit.MINUTES);
            }
        }, 30L, TimeUnit.SECONDS);
    }

    public final void onDisable() {
        logInfo("Disabling plugin");

        onKookyDisable();

        logInfo("Plugin is now disabled");

        try {
            getConnection().closeConnection();
            logInfo("The database connection has been closed");
        } catch (SQLException e) {
            logSevere(e.getMessage());
            logSevere("Could not close SQL connection");
        }

        getPacketHub().unregisterThis();

        if (Updatetask.instance != null) {
            try {
                Updatetask.instance.interrupt();
            } catch (Exception e) {
                logSevere(e.getMessage());
                logSevere(e.getClass().getName());
                logSevere("Could not interrupt updatetask thread");
            }
            Updatetask.instance = null;
        }
    }

    public final void onLoad() {
        //Loading properties
        logInfo("Loading SQL Properties");
        if (!sqlpropertiesfile.exists()) {
            try {
                PropertiesFile.generateFresh(sqlpropertiesfile, new String[]{"hostname", "port", "username", "password", "database"}, new String[]{"localhost", "3306", "root", "NONE", "kookyserver"});
            } catch (Exception e) {
                e.printStackTrace();
            }
            logSevere("Properties File not found");
            endSetup("Could not find properties file");
            return;
        }
        try {
            sqlproperties = new PropertiesFile(sqlpropertiesfile);
        } catch (Exception e) {
            logSevere(e.getMessage());
            endSetup("Exception occurred when loading properties");
            return;
        }

        logInfo("Loaded SQL Properties");

        logInfo("Finding database information...");

        String temp;
        try {
            connection = new SQLConnection(sqlproperties.getString("hostname"), sqlproperties.getNumber("port").intValue(), sqlproperties.getString("database"), sqlproperties.getString("username"), (temp = sqlproperties.getString("password")).equals("NONE") ? null : temp);
        } catch (ParseException ex) {
            logSevere(ex.getMessage());
            endSetup("Invalid database port");
        } catch (Exception ex) {
            logSevere(ex.getMessage());
            endSetup("Invalid configuration");
        }

        logInfo("Found database information");

        //Connecting to MySQL
        logInfo("Connecting to MySQL...");

        try {
            getConnection().openConnection();
        } catch (Exception e) {
            logSevere(e.getMessage());
            endSetup("Could not establish connection to database");
            return;
        }

        logInfo("Connected to MySQL");

        //Loading server types

        logInfo("Finding server types...");

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
                /*
                SQLUtil.createTable(getConnection(),"servertypes",new ImmutableMap.Builder<String,Map.Entry<SQLUtil.SQLDataType,Integer>>()
                        .put("name",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT,32))
                        .put("prefix",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT,5))
                        .put("maxplayer",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT,3))
                        .put("low-limit",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT,3))
                        .put("high-limit",new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT,3))
                        .build());
                logInfo("Creating SQL ServerType Table");
                endSetup("You must configure your servertypes!");
                */
            }
            ResultSet set = SQLUtil.query(getConnection(), "servertypes", "*", new SQLUtil.Where("1"));
            while (set.next()) {
                ServerType.registerType(new ServerType(set.getString("name"), set.getString("prefix"), set.getInt("maxplayer"), set.getInt("low-limit"), set.getInt("high-limit")));
            }
            set.close();
        } catch (Exception ex) {
            logSevere(ex.getMessage());
            endSetup("Could not find server types");
        }

        logInfo("Loaded server types");
        logInfo("Saving XServer Configuration...");


        //Normal load stuff
        saveXServerDefaults();

        logInfo("Saved XServer Configuration");
        logInfo("Loading plugin");

        onKookyLoad();

        logInfo("Plugin loading complete");
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

}
