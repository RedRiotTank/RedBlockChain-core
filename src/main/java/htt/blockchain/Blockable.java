package htt.blockchain;

import java.io.Serializable;
import java.util.UUID;

public interface Blockable extends Serializable {
    UUID getId();
    long getTimeStamp();
    String getInfoForHash();
}
