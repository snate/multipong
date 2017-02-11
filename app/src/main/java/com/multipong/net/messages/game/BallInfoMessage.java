package com.multipong.net.messages.game;

import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.net.messages.Message;

public class BallInfoMessage extends Message {
    @Override
    protected String getMessageType() {
        return MultiplayerStateManager.MessageType.BALL_INFO;
    }
}
