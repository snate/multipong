package com.multipong.activity;

import java.util.List;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public interface ParticipantActivity {
    void receiveList(int hostID, String hostName, List<String> participants);
}
