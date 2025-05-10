package htt.networkmanager;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Message implements Serializable {
    public enum Type {
        OPERATION, BLOCK, CHAIN_REQUEST, CHAIN_RESPONSE
    }

    private final Type type;
    private final Object payload;

}