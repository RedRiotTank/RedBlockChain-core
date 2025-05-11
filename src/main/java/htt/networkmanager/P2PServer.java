package htt.networkmanager;

import htt.blockchain.Block;
import htt.blockchain.Blockable;
import htt.blockchain.Blockchain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class P2PServer implements Runnable {
    private final Node node;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private volatile boolean running = true;

    public P2PServer(Node node, int port) {
        this.node = node;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Node listening on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket, node));
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final Node node;

        public ClientHandler(Socket socket, Node node) {
            this.socket = socket;
            this.node = node;
        }

        @Override
        public void run() {
            try (Socket socket = this.socket;
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                Message handshakeMsg = (Message) in.readObject();
                if (handshakeMsg.getType() != Message.Type.HANDSHAKE) {
                    throw new IOException("Handshake esperado");
                }
                int remotePort = (int) handshakeMsg.getPayload();
                String remoteHost = socket.getInetAddress().getHostAddress();

                Peer peer = new Peer(remoteHost, remotePort, socket, in, out);
                this.node.addPeer(peer);

                out.writeObject(new Message(Message.Type.HANDSHAKE, this.node.getPort()));

                List<String> peerList = this.node.getPeers().stream()
                        .map(p -> p.getHost() + ":" + p.getListeningPort())
                        .collect(Collectors.toList());
                out.writeObject(new Message(Message.Type.PEER_EXCHANGE, peerList));

                while (true) {
                    Message msg = (Message) in.readObject();
                    peer.updateLastSeen();
                    if (!this.node.getProcessedMessageIds().contains(msg.getId())) {
                        this.node.getProcessedMessageIds().add(msg.getId().toString());
                        System.out.println("Mensaje recibido de tipo: " + msg.getType() + " desde: " + peer.getHost() + ":" + peer.getListeningPort());
                        processMessage(msg, out);
                        this.node.broadcastMessage(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void processMessage(Message message, ObjectOutputStream out) throws IOException {
            switch (message.getType()) {
                case OPERATION:
                    node.receiveOperation((Blockable) message.getPayload());
                    break;
                case BLOCK:
                    node.receiveBlock((Block<Blockable>) message.getPayload());
                    break;
                case CHAIN_REQUEST:
                    out.writeObject(new Message(Message.Type.CHAIN_RESPONSE, node.getBlockchain()));
                    break;
                case CHAIN_RESPONSE:
                    node.replaceChain((Blockchain) message.getPayload());
                    break;
                case PEER_EXCHANGE:
                    @SuppressWarnings("unchecked")
                    List<String> receivedPeers = (List<String>) message.getPayload();
                    for (String peerStr : receivedPeers) {
                        String[] parts = peerStr.split(":");
                        String host = parts[0];
                        int port = Integer.parseInt(parts[1]);

                        if (!host.equals(this.node.getHost()) || port != this.node.getPort()) {
                            try {
                                node.connectToPeer(host, port);
                            } catch (IOException e) {
                                System.err.println("Error conectando a peer: " + peerStr);
                            }
                        }
                    }
                    break;
            }
        }
    }
}
