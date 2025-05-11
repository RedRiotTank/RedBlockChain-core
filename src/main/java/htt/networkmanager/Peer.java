package htt.networkmanager;

import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Peer {
    @Getter
    private final String host;
    @Getter private final int listeningPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    @Getter private volatile long lastSeen;

    public Peer(String host, int listeningPort) {
        this.host = host;
        this.listeningPort = listeningPort;
    }

    public Peer(String host, int listeningPort, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.host = host;
        this.listeningPort = listeningPort;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.lastSeen = System.currentTimeMillis();
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    public void connect() throws IOException {
        this.socket = new Socket(host, listeningPort);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(Message message) throws IOException {
        synchronized (this) {
            out.writeObject(message);
            out.flush();
            out.reset();
        }
    }

    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
