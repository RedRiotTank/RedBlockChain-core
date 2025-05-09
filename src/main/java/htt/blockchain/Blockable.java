package htt.blockchain;

import java.util.UUID;

public interface Blockable {
    UUID getId();
    long getTimeStamp();
    String getInfoForHash();
}
