package htt.crypt;

import htt.networkmanager.Node;

public class Main {
    public static void main(String[] args) {
        try {
            Node node = new Node("192.168.1.153", 8080);
            System.out.println("Nodo iniciado en 192.168.1.153:8080");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
