package htt.blockchain;

import lombok.Getter;
import lombok.Setter;


public class Block {
    @Getter private final int index;
    private final long timestamp;
    @Getter @Setter private String previousHash;
    @Getter private String hash;
    private final String data;
    private int nonce;

    public Block(int index, long timestamp, String data, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return Utils.applySha256(index + Long.toString(timestamp) + previousHash + data + nonce);
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
