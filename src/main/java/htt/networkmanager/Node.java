package htt.networkmanager;




import htt.blockchain.Block;
import htt.blockchain.Blockable;
import htt.blockchain.Blockchain;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Node {
    @Getter private final String host;
    @Getter private final Blockchain blockchain;
    private final P2PServer p2pServer;
    @Getter private final CopyOnWriteArrayList<Peer> peers = new CopyOnWriteArrayList<>();
    @Getter private final Set<String> processedMessageIds = ConcurrentHashMap.newKeySet();

    @Getter private int port;

    public Node(String host, int port) {
        this.host = host;
        this.port = port;
        this.blockchain = new Blockchain();
        this.p2pServer = new P2PServer(this, port);
        new Thread(p2pServer).start();
        startHeartbeatChecker();
    }

    private void startHeartbeatChecker() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            peers.removeIf(peer -> {
                if (now - peer.getLastSeen() > 120_000) {
                    try {
                        peer.disconnect();
                    } catch (IOException e) {
                    }
                    return true;
                }
                return false;
            });
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void addPeer(Peer peer) {
        if (peers.stream().noneMatch(p ->
                p.getHost().equals(peer.getHost()) && p.getListeningPort() == peer.getListeningPort())) {
            peers.add(peer);
            System.out.println("Nuevo peer añadido: " + peer.getHost() + ":" + peer.getListeningPort());
        }
    }

    public void connectToPeer(String host, int port) throws IOException {
        Peer peer = new Peer(host, port);
        peer.connect();

        peer.sendMessage(new Message(Message.Type.HANDSHAKE, this.port));

        List<String> peerList = peers.stream()
                .filter(p -> !p.getHost().equals(peer.getHost()) || p.getListeningPort() != peer.getListeningPort())
                .map(p -> p.getHost() + ":" + p.getListeningPort())
                .collect(Collectors.toList());
        peer.sendMessage(new Message(Message.Type.PEER_EXCHANGE, peerList));

        peers.add(peer);
    }

    public void broadcastMessage(Message message) {
        if (processedMessageIds.contains(message.getId())) return;

        processedMessageIds.add(message.getId());
        peers.forEach(peer -> {
            try {
                peer.sendMessage(message);
            } catch (IOException e) {
                System.err.println("Error enviando mensaje a " + peer.getHost() + ": " + e.getMessage());
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