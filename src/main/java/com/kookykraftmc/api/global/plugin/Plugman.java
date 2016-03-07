package com.kookykraftmc.api.global.plugin;

import java.io.File;

public interface Plugman<KookyPlugin> {

    void disable(KookyPlugin p);

    void enable(KookyPlugin p);

    void unload(KookyPlugin p);

    KookyPlugin load(File jar);

    KookyPlugin get(String name);

}
