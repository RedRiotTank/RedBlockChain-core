package htt.crypt;

import htt.blockchain.Blockable;
import lombok.AllArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
public class TransactionA implements Blockable {
    private UUID fromUser;
    private UUID toUser;
    private double amount;

    @Override
    public String toString() {
        return "Transaction -> {fromUser='" + fromUser + "',toUser='" + toUser+ "',amount='" + amount + "'}";
    }

}
