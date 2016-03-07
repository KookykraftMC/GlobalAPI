package com.kookykraftmc.api.global.kookypackets;

import de.mickare.xserver.net.XServer;

public class PacketInfo {

    private XServer server;
    private String channel;

    public PacketInfo(XServer server, String channel) {
        this.server = server;
        this.channel = channel;
    }

    public XServer getServer() {
        return server;
    }

    public String getChannel() {
        return channel;
    }

}
