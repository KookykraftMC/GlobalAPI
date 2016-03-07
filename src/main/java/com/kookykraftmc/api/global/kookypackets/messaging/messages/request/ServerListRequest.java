package com.kookykraftmc.api.global.kookypackets.messaging.messages.request;

import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;
import com.kookykraftmc.api.global.type.ServerType;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerListRequest extends AbstractMessageObject {

    private String servertype;

    public ServerListRequest(ServerType servertype) {
        this.servertype = servertype.getName();
    }

    public ServerListRequest(byte[] bytes) {
        super(bytes);
    }

    public void serialize(ByteArrayDataInput in) {
        servertype = in.readUTF();
    }

    public void parse(DataOutputStream out) throws IOException {
        out.writeUTF(servertype);
    }

    public ServerType getServertype() {
        return ServerType.getType(servertype);
    }

}
