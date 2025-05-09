package htt.crypt;

import htt.blockchain.Block;
import htt.blockchain.Blockchain;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();

        System.out.println("Mining block 1");
        blockchain.addBlock(
                new Block<>(
                    1, System.currentTimeMillis(),
                    List.of(
                        new TransactionA(UUID.randomUUID(), UUID.randomUUID(), 1),
                        new TransactionB(UUID.randomUUID(), UUID.randomUUID(), 1)
                    ),
                    blockchain.getLatestBlock().getHash()
                )
        );

        System.out.println("Mining block 2");
        blockchain.addBlock(
                new Block<>(
                        2, System.currentTimeMillis(),
                        List.of(
                                new TransactionA(UUID.randomUUID(), UUID.randomUUID(), 1),
                                new TransactionB(UUID.randomUUID(), UUID.randomUUID(), 1)
                        ),
                        blockchain.getLatestBlock().getHash()
                )
        );

        System.out.println("Mining block 3");
        blockchain.addBlock(
                new Block<>(
                        3, System.currentTimeMillis(),
                        List.of(
                                new TransactionA(UUID.randomUUID(), UUID.randomUUID(), 1),
                                new TransactionB(UUID.randomUUID(), UUID.randomUUID(), 1)
                        ),
                        blockchain.getLatestBlock().getHash()
                )
        );
        System.out.println("\nBlockchain válida: " + blockchain.isChainValid());
    }
}
