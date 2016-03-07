package com.kookykraftmc.api.global.kookypackets.messaging.messages.response;

import com.kookykraftmc.api.global.kookypackets.messaging.messages.AbstractDataMapMessageObject;
import com.kookykraftmc.api.global.type.ServerType;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ServerListResponse extends AbstractDataMapMessageObject {

    private String servertype;

    public ServerListResponse(Map<String, String> data, ServerType servertype) {
        super(data);
        this.servertype = servertype.getName();
    }

    public void serializeInfo(ByteArrayDataInput in) {
        servertype = in.readUTF();
    }

    public void parseInfo(DataOutputStream out) throws IOException {
        out.writeUTF(servertype);
    }

    public ServerType getServertype() {
        return ServerType.getType(servertype);
    }

}
