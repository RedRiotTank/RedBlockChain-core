package htt.blockchain;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
public class Blockchain implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Getter private final OperationPool operationPool = new OperationPool();
    private ArrayList<Block<Blockable>> chain;

    public Blockchain() {
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    private Block<Blockable> createGenesisBlock() {
        return new Block<>(0, System.currentTimeMillis(), new ArrayList<>(), "0");
    }

    public Block<Blockable> getLatestBlock() {
        return chain.getLast();
    }

    public void addBlock(Block<Blockable> newBlock) {
        newBlock.setPreviousHash(getLatestBlock().getHash());
        chain.add(newBlock);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block<Blockable> curr = chain.get(i);
            Block<Blockable> prev = chain.get(i - 1);

            if (!curr.getHash().equals(curr.calculateHash())) return false;
            if (!curr.getPreviousHash().equals(prev.getHash())) return false;
        }
        return true;
    }

    public void replaceChain(Blockchain newChain) {
        this.chain = new ArrayList<>(newChain.getChain());
    }
}