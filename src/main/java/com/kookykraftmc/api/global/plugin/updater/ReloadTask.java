package com.kookykraftmc.api.global.plugin.updater;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ReloadTask extends Thread implements Runnable {

    private FileUpdater updater;

    public ReloadTask(FileUpdater updater) {
        this.updater = updater;
    }

    @Override
    public void run() {
        try {
            updater.updateTaskBefore();
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Could not disable itself", e);
            return;
        }
        whenUnloaded();
        try {
            updater.updateTaskAfter();
            ;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Could not enable itself", e);
        }
    }

    public abstract void whenUnloaded();

}
