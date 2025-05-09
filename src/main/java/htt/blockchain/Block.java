package htt.blockchain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class Block<D extends Blockable> {
    @Getter private final int index;
    private final long timestamp;
    @Getter @Setter private String previousHash;
    @Getter private String hash;
    private final List<D> operationList;
    private int nonce;

    public Block(int index, long timestamp, List<D> operationList, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.operationList = operationList;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String operationString = operationList.toString();
        return Utils.applySha256(index + Long.toString(timestamp) + previousHash + operationString + nonce);
    }

    public void mineBlock(int difficulty) {
        String target = "0".repeat(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Mined block: " + hash + " nonce: " + nonce);
    }

}
