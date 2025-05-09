package htt.blockchain;

import lombok.Getter;

import java.util.ArrayList;

public class Blockchain {

    @Getter private ArrayList<Block> chain;

    public Blockchain(){
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        return new Block(0, System.currentTimeMillis(), "Genesis Block", "0");
    }

    public Block getLatestBlock() {
        return chain.getLast();
    }

    public void addBlock(Block newBlock) {
        newBlock.setPreviousHash(getLatestBlock().getHash());
        int difficulty = 5;
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block curr = chain.get(i);
            Block prev = chain.get(i - 1);

            if (!curr.getHash().equals(curr.calculateHash())) return false;
            if (!curr.getPreviousHash().equals(prev.getHash())) return false;
        }
        return true;
    }


}
