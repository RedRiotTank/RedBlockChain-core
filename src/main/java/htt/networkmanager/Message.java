package htt.networkmanager;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    public enum Type {
        OPERATION, BLOCK, CHAIN_REQUEST, CHAIN_RESPONSE,
        PEER_EXCHANGE, HANDSHAKE, HEARTBEAT
    }

    private final String id;
    private final Type type;
    private final Object payload;

    public Message(Type type, Object payload) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.payload = payload;
    }
}