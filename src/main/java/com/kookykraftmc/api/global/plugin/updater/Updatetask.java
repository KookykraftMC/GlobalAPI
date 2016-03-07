package com.kookykraftmc.api.global.plugin.updater;

import com.kookykraftmc.api.global.file.DownloadUtil;
import com.kookykraftmc.api.global.sql.SQLConnection;
import com.kookykraftmc.api.global.sql.SQLUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class Updatetask extends Thread implements Runnable {
    public static Updatetask instance;

    private long start = System.currentTimeMillis();
    private Iterable<FileUpdater> fileUpdaters;
    private Iterable<SQLUpdater> sqlUpdaters;
    private Map<String, Integer> updatesmap = new HashMap<>();
    private Map<String, String> updateurls = new HashMap<>();
    private SQLConnection connection;

    protected Updatetask(SQLConnection connection, String table, Iterable<FileUpdater> fileUpdaters, Iterable<SQLUpdater> sqlUpdaters) throws SQLException, ClassNotFoundException {
        super("UpdateTask");
        this.fileUpdaters = fileUpdaters;
        this.sqlUpdaters = sqlUpdaters;
        ResultSet set = null;
        try {
            set = SQLUtil.query(connection, table, "*", new SQLUtil.Where("1"));
            while (set.next()) {
                String artifact = set.getString("artifact");
                updatesmap.put(artifact, set.getInt("version"));
                updateurls.put(artifact, set.getString("url"));
            }
        } finally {
            if (set != null) {
                set.close();
            }
        }
        this.connection = new SQLConnection(connection.getHostname(), connection.getPort(), connection.getDatabase(), connection.getUser(), connection.getPassword());
        start();
    }

    public void run() {
        instance = this;
        try {
            this.connection.openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            logSevere(e.getMessage());
            logSevere("Could not connect to SQL");
        }
        final Map<FileUpdater, InputStream> streamMap = new HashMap<>();
        for (FileUpdater update : fileUpdaters) {
            if (updatesmap.containsKey(update.getArtifact()) && updateurls.containsKey(update.getArtifact())) {
                int updateto = updatesmap.get(update.getArtifact());
                if (update.getVersion() < updateto) {
                    String url = updateurls.get(update.getArtifact());
                    InputStream stream;
                    try {
                        stream = DownloadUtil.download(url);
                        logInfo("Downloaded update v" + String.valueOf(updateto) + " for BubbleNetwork");
                    } catch (Exception e) {
                        logSevere(e.getMessage());
                        logSevere("Error - could not download update " + update.getArtifact() + " v" + String.valueOf(updateto));
                        continue;
                    }
                    streamMap.put(update, stream);
                }
            }
        }

        for (SQLUpdater updater : sqlUpdaters) {
            try {
                updater.update(connection);
                logInfo("Successfully updated " + updater.getName());
            } catch (Exception ex) {
                logSevere(ex.getMessage());
                logSevere("Error - could not update " + updater.getName());
            }
        }
        try {
            connection.closeConnection();
        } catch (SQLException e) {
            logSevere(e.getMessage());
            logSevere("Could not disconnect from SQL");
        }
        if (streamMap.size() > 0) {
            update(new Runnable() {
                public void run() {
                    for (final Map.Entry<FileUpdater, InputStream> update : streamMap.entrySet()) {
                        new ReloadTask(update.getKey()) {
                            public void whenUnloaded() {
                                FileUpdater updater = update.getKey();
                                InputStream stream = update.getValue();
                                try {
                                    Files.copy(stream, updater.getReplace().toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    logInfo("Successfully installed update " + updater.getArtifact());
                                } catch (Exception ex) {
                                    logSevere(ex.getMessage());
                                    logSevere(ex.getClass().getName());
                                    logSevere("Failed to install update " + updater.getArtifact());
                                } finally {
                                    try {
                                        stream.close();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }.start();
                    }
                }
            });
        }
        onFinish();
        instance = null;
    }

    public abstract void update(Runnable r);

    public abstract File getFile();

    public abstract void logInfo(String line);

    public abstract void logSevere(String line);

    public void onFinish() {
        logInfo("Completed updating task, this took " + String.valueOf(((double) (System.currentTimeMillis() - start)) / 1000D) + " seconds");
    }
}
