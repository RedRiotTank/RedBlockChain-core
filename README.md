# RedBlockChain-core 🚀

[![Java Version](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A high-performance, lightweight **P2P Blockchain Core** implemented in Java. This project demonstrates the fundamental principles of distributed ledgers, including cryptographic security, Proof-of-Work (PoW) consensus, and peer-to-peer network synchronization.



## 🏗️ Architectural Overview

RedBlockChain-core is built with a modular approach, separating the ledger logic from the network communication layer.

### Core Components:
- **Blockchain Engine**: Manages the immutable chain, genesis block creation, and chain validation.
- **Proof-of-Work (Mining)**: Implements a SHA-256 based mining algorithm with dynamic difficulty adjustment.
- **P2P Network Manager**: A robust peer-to-peer system using multithreaded TCP sockets for real-time node synchronization.
- **Operation Pool**: A thread-safe (`CopyOnWriteArrayList`) memory pool for pending transactions (operations).

## 🚀 Key Features

- **Cryptographic Integrity**: Every block is hashed using SHA-256, ensuring that any data tampering is immediately detected by the chain validation logic.
- **Mining & Consensus**: Includes a `mineBlock` method that simulates the mining process, requiring nodes to solve a computational puzzle before adding a block to the chain.
- **P2P Synchronization**:
    - **Peer Exchange**: Nodes automatically share their peer lists to ensure a decentralized and healthy network.
    - **Handshake Mechanism**: Secure initial connection protocol between nodes.
    - **Heartbeat System**: Monitors peer health and automatically removes inactive nodes after a timeout.
- **Conflict Resolution**: Implements a "Longest Chain" rule to resolve forks and ensure all nodes converge on the same version of the truth.

## 🛠️ Technical Stack

- **Language**: Java 17+
- **Concurrency**: Multithreading with `ExecutorService` and thread-safe collections.
- **Networking**: TCP/IP Sockets with Object Serialization for message passing.
- **Boilerplate Reduction**: Project Lombok.

## 💻 Code Snippet: The Mining Logic

```java
public void mineBlock(int difficulty) {
    String target = "0".repeat(difficulty);
    while (!hash.substring(0, difficulty).equals(target)) {
        nonce++;
        hash = calculateHash();
    }
    System.out.println("Mined block: " + hash);
}
