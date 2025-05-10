package htt.crypt;

import htt.blockchain.Blockable;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


public class TransactionB implements Blockable, Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID fromUser;
    private final UUID toUser;
    private final long timeStamp;
    private final double amount;

    public TransactionB(UUID id, UUID fromUser, UUID toUser, double amount) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.timeStamp = System.currentTimeMillis();
        this.amount = amount;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String getInfoForHash() {
        return "{fromUser='" + fromUser + "',toUser='" + toUser+ "',amount='" + amount + "'}";
    }

}
