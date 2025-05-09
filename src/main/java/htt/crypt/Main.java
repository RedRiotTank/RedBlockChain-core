package htt.crypt;

import htt.blockchain.Block;
import htt.blockchain.Blockchain;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();

        System.out.println("Mining block 1");
        blockchain.addBlock(new Block(1, System.currentTimeMillis(), "Transacción 1", blockchain.getLatestBlock().getHash()));

        System.out.println("Mining block 2");
        blockchain.addBlock(new Block(2, System.currentTimeMillis(), "Transacción 2", blockchain.getLatestBlock().getHash()));

        System.out.println("Mining block 3");
        blockchain.addBlock(new Block(3, System.currentTimeMillis(), "Transacción 3", blockchain.getLatestBlock().getHash()));

        System.out.println("\nBlockchain válida: " + blockchain.isChainValid());
    }
}
