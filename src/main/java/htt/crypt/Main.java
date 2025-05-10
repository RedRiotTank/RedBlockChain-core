package htt.crypt;



import htt.networkmanager.Node;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Node node1 = new Node("Node1", 5000);

        Node node2 = new Node("Node2", 5001);
        try {
            node2.connectToPeer("localhost", 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Node node3 = new Node("Node3", 5002);
        try {
            node3.connectToPeer("localhost", 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}