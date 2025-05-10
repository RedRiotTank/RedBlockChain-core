package htt.networkmanager;




import htt.blockchain.Block;
import htt.blockchain.Blockable;
import htt.blockchain.Blockchain;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Node {
    @Getter private final Blockchain blockchain;
    private final P2PServer p2pServer;
    private final CopyOnWriteArrayList<Peer> peers = new CopyOnWriteArrayList<>();
    private final String nodeId;
    private final int port;

    public Node(String nodeId, int port) {
        this.nodeId = nodeId;
        this.port = port;
        this.blockchain = new Blockchain();
        this.p2pServer = new P2PServer(this, port);
        new Thread(p2pServer).start();
    }

    public void connectToPeer(String host, int port) throws IOException {
        Peer peer = new Peer(host, port);
        peer.connect();
        peers.add(peer);

        peer.sendMessage(new Message(Message.Type.CHAIN_REQUEST, null));
    }

    public void broadcastMessage(Message message) {
        peers.forEach(peer -> {
            try {
                peer.sendMessage(message);
            } catch (IOException e) {
                System.err.println("Error sending message to peer: " + e.getMessage());
                peers.remove(peer);
            }
        });
    }

    private boolean validateBlock(Block<Blockable> block) {
        if (!block.getHash().equals(block.calculateHash())) {
            return false;
        }

        return block.getPreviousHash().equals(blockchain.getLatestBlock().getHash());
    }

    private boolean validateOperation(Blockable operation) {
        // TODO: implement
        return true;
    }

    public void receiveOperation(Blockable operation) {
        if (validateOperation(operation)) {
            blockchain.getOperationPool().addOperation(operation);
            broadcastMessage(new Message(Message.Type.OPERATION, operation));

            if (blockchain.getOperationPool().getOperations().size() >= 5) {
                mineNewBlock();
            }
        }
    }

    public void receiveBlock(Block<Blockable> block) {
        if (validateBlock(block)) {
            blockchain.addBlock(block);
            broadcastMessage(new Message(Message.Type.BLOCK, block));
        }
    }

    private void mineNewBlock() {
        List<Blockable> operations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            operations.add(blockchain.getOperationPool().getOperations().remove(0));
        }

        Block<Blockable> newBlock = new Block<>(
                blockchain.getLatestBlock().getIndex() + 1,
                System.currentTimeMillis(),
                operations,
                blockchain.getLatestBlock().getHash()
        );

        newBlock.mineBlock(4);
        blockchain.addBlock(newBlock);
        broadcastMessage(new Message(Message.Type.BLOCK, newBlock));
    }

    public void replaceChain(Blockchain newChain) {
        if (newChain.getChain().size() > blockchain.getChain().size() && newChain.isChainValid()) {
            this.blockchain.replaceChain(newChain);
        }
    }
}