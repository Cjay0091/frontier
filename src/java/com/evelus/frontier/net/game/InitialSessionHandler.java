/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */
package com.evelus.frontier.net.game;

import com.evelus.frontier.Constants;
import com.evelus.frontier.Server;
import com.evelus.frontier.game.GameWorld;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.game.ondemand.OndemandSession;
import com.evelus.frontier.game.ondemand.OndemandWorker;
import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.codec.OndemandDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class InitialSessionHandler implements SessionHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(InitialSessionHandler.class.getSimpleName());

    /**
     * Constructs a new {@link InitialHandler};
     */
    public InitialSessionHandler(Session session) 
    {
        this.session = session;
    }
    
    /**
     * The session for this session handler.
     */
    private Session session;

    @Override
    public void decode(IncomingFrame incomingFrame) {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer(incomingFrame.getPayload());
        Channel channel = session.getChannel();
        if(id == 14) {
            buffer.getUbyte();
            if (Server.getState() == Server.DEV_STATE) {
                ChannelBuffer channelBuffer = ChannelBuffers.buffer(9);
                channelBuffer.writeByte(0);
                channelBuffer.writeLong((long) (Long.MAX_VALUE * Math.random()));
                session.getChannel().write(channelBuffer);
            }
        } else if(id == 15) {
            int revision = buffer.getDword();
            if (revision != 443) {
                logger.log(Level.INFO, "Unexpected client revision sent on od connect [revision=" + revision + "]");
                ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                channelBuffer.writeByte(6);
                channel.write(channelBuffer).addListener(ChannelFutureListener.CLOSE);
            } else {
                channel.getPipeline().replace("decoder", "oddecoder", new OndemandDecoder());
                OndemandSession odSession = new OndemandSession(session);
                if (!OndemandWorker.getInstance().registerSession(odSession)) {
                    ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                    channelBuffer.writeByte(7);
                    channel.write(channelBuffer).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                OndemandHandler handler = new OndemandHandler(odSession);
                session.setHandler(handler);
                ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                channelBuffer.writeByte(0);
                channel.write(channelBuffer);
            }
        } else if(id == 16) {
            int revision = buffer.getDword();
            if (revision != 443) {
                logger.log(Level.INFO, "Unexpected client revision sent on login [revision=" + revision + "]");
                ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                channelBuffer.writeByte(6);
                channel.write(channelBuffer).addListener(ChannelFutureListener.CLOSE);
            } else {
                buffer.getUbyte();
                for (int i = 0; i < 14; i++) {
                    int checksum = buffer.getDword();
                    if (checksum != ArchiveManager.getChecksum(255, i)) {
                        logger.log(Level.INFO, "Unexpected checksum sent on login [id=" + i + ", checksum=" + checksum + "]");
                        ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                        channelBuffer.writeByte(6);
                        channel.write(channelBuffer).addListener(ChannelFutureListener.CLOSE);
                        return;
                    }
                }
                int length = buffer.getUbyte();
                byte[] bytes = new byte[length];
                System.arraycopy(buffer.getPayload(), buffer.getOffset(), bytes, 0, length);
                if (Constants.RSA_ACTIVE) {
                    throw new RuntimeException("not yet implemented");
                }
                buffer = new Buffer(bytes);
                if (buffer.getUbyte() != 10) {
                    throw new RuntimeException("invalid rsa check");
                }
                int[] seeds = new int[4];
                for (int i = 0; i < 4; i++) {
                    seeds[i] = buffer.getDword();
                }
                buffer.getDword();
                buffer.getQword();
                buffer.getJstr();
                GamePlayer player = new GamePlayer(session);
                if (!GameWorld.getInstance().registerPlayer(player)) {
                    ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                    channelBuffer.writeByte(7);
                    session.getChannel().write(channelBuffer).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                session.initIncomingIsaac(seeds);
                for (int i = 0; i < 4; i++) {
                    seeds[i] += 50;
                }
                session.initOutgoingIsaac(seeds);
                GameSessionHandler handler = new GameSessionHandler(player);
                session.setHandler(handler);
                handler.sendLogin();
            }
        }
    }

    @Override
    public void destroy() { }
}
