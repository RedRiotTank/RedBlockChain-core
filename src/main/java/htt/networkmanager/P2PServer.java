package htt.networkmanager;

import htt.blockchain.Block;
import htt.blockchain.Blockable;
import htt.blockchain.Blockchain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final Node node;

        public ClientHandler(Socket socket, Node node) {
            this.socket = socket;
            this.node = node;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                Message message = (Message) in.readObject();
                processMessage(message, out);

            } catch (IOException | ClassNotFoundException e) {
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
            }
        }
    }
}
