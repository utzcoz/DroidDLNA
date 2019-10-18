
package com.github.dlna.dmr;

import android.content.Context;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ZxtMediaPlayers extends ConcurrentHashMap<UnsignedIntegerFourBytes, ZxtMediaPlayer> {

    final private static Logger log = Logger.getLogger(ZxtMediaPlayers.class.getName());

    private final LastChange avTransportLastChange;
    private final LastChange renderingControlLastChange;
    private Context mContext;


    ZxtMediaPlayers(int numberOfPlayers,
                    Context context,
                    LastChange avTransportLastChange,
                    LastChange renderingControlLastChange) {
        super(numberOfPlayers);
        this.mContext = context;
        this.avTransportLastChange = avTransportLastChange;
        this.renderingControlLastChange = renderingControlLastChange;

        for (int i = 0; i < numberOfPlayers; i++) {
            ZxtMediaPlayer player =
                    new ZxtMediaPlayer(
                            new UnsignedIntegerFourBytes(i),
                            mContext,
                            avTransportLastChange,
                            renderingControlLastChange
                    ) {
                        @Override
                        protected void transportStateChanged(TransportState newState) {
                            super.transportStateChanged(newState);
                            if (newState.equals(TransportState.PLAYING)) {
                                onPlay(this);
                            } else if (newState.equals(TransportState.STOPPED)) {
                                onStop(this);
                            }
                        }
                    };
            put(player.getInstanceId(), player);
        }
    }

    protected void onPlay(ZxtMediaPlayer player) {
        log.fine("Player is playing: " + player.getInstanceId());
    }

    protected void onStop(ZxtMediaPlayer player) {
        log.fine("Player is stopping: " + player.getInstanceId());
    }
}
