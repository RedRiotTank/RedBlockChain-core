package htt.crypt;

import htt.blockchain.Blockable;

import java.util.UUID;


public class TransactionA implements Blockable {
    private final UUID id;
    private final UUID fromUser;
    private final UUID toUser;
    private final long timeStamp;
    private final double amount;

    public TransactionA(UUID id, UUID fromUser, UUID toUser, double amount) {
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
