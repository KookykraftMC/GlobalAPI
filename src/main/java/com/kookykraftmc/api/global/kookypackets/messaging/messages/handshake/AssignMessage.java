package com.kookykraftmc.api.global.kookypackets.messaging.messages.handshake;

import com.kookykraftmc.api.global.kookypackets.messaging.AbstractMessageObject;
import com.kookykraftmc.api.global.type.ServerType;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class AssignMessage extends AbstractMessageObject {

    private int id;
    private ServerType type;

    public AssignMessage(int id, ServerType type) {
        super();
        this.id = id;
        this.type = type;
    }

    public AssignMessage(byte[] bytes) {
        super(bytes);
    }

    public void parse(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeUTF(type.getName());
    }

    public void serialize(ByteArrayDataInput in) {
        this.id = in.readInt();
        this.type = ServerType.getType(in.readUTF());
    }

    public int getId() {
        return id;
    }

    public ServerType getWrapperType() {
        return type;
    }

}
