package com.kookykraftmc.api.global.kookypackets.messaging.messages.request;

import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;
import com.kookykraftmc.api.global.type.ServerType;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerServerRequest extends AbstractMessageObject {

    private ServerType type;

    public PlayerServerRequest(ServerType type) {
        this.type = type;
    }

    public PlayerServerRequest(byte[] bytes) {
        super(bytes);
    }

    public void serialize(ByteArrayDataInput in) {
        type = ServerType.getType(in.readUTF());
    }

    public void parse(DataOutputStream out) throws IOException {
        out.writeUTF(type.getName());
    }

    public ServerType getServerType() {
        return type;
    }

}
