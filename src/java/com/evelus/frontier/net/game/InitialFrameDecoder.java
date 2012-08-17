/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.Constants;
import com.evelus.frontier.Server;
import com.evelus.frontier.game.World;
import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.game.ondemand.OndemandSession;
import com.evelus.frontier.game.ondemand.OndemandWorker;
import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.codec.FrameEncoder;
import com.evelus.frontier.net.game.codec.OndemandDecoder;
import com.evelus.frontier.net.game.frames.RebuildMapFrame;
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
public class InitialFrameDecoder implements FrameDecoder {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(InitialFrameDecoder.class.getSimpleName());

    /**
     * The instance of this class.
     */
    private static InitialFrameDecoder instance;

    /**
     * Constructs a new {@link InitialHandler};
     */
    private InitialFrameDecoder ( ) { }

    @Override
    public void decode( Session session , IncomingFrame incomingFrame )
    {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer( incomingFrame.getPayload() );
        switch( id ) {
            case 14:
                handleLoginServerSelect( session , buffer );
                return;
            case 15:
                handleOdConnect( session , buffer );
                return;
            case 16:
                handleLoginRequest( session , buffer );
                return;
            default:
                logger.log(Level.INFO, "Unknown initial connection frame id [id=" + id + "]");
                return;
        }
    }

    /**
     * Hanldes when a login server is requested.
     *
     * @param session The session from where the request came from.
     * @param buffer The buffer to parse the request from.
     */
    private static void handleLoginServerSelect( Session session , Buffer buffer )
    {
        int id = buffer.getUbyte();
        if( Server.getState() == Server.DEV_STATE ) {
            ChannelBuffer channelBuffer = ChannelBuffers.buffer(9);
            channelBuffer.writeByte(0);
            channelBuffer.writeLong( (long) ( Long.MAX_VALUE * Math.random() ) );
            session.getChannel().write( channelBuffer );
        }
    }

    /**
     * Handles an incoming ondemand connection request.
     *
     * @param session The session from which the request came from.
     * @param buffer The buffer to parse the request from.
     */
    private static void handleOdConnect( Session session , Buffer buffer )
    {
        Channel channel = session.getChannel( );
        int revision = buffer.getDword( );
        if( revision != 443 ) {
            logger.log(Level.INFO, "Unexpected client revision sent on od connect [revision=" + revision + "]");
            ChannelBuffer channelBuffer = ChannelBuffers.buffer( 1 );
            channelBuffer.writeByte( 6 );
            channel.write( channelBuffer ).addListener( ChannelFutureListener.CLOSE );
        } else {
            channel.getPipeline().replace( "decoder" , "oddecoder" , new OndemandDecoder( ) );
            OndemandSession odSession = new OndemandSession( session );
            if( !OndemandWorker.getInstance().registerSession( odSession ) ) {
                ChannelBuffer channelBuffer = ChannelBuffers.buffer( 1 );
                channelBuffer.writeByte( 7 );
                channel.write( channelBuffer ).addListener( ChannelFutureListener.CLOSE );
                return;
            }
            OndemandHandler handler = new OndemandHandler( odSession );
            session.setHandler( handler );
            session.setFrameDecoder( new OndemandFrameDecoder( handler ) );
            ChannelBuffer channelBuffer = ChannelBuffers.buffer( 1 );
            channelBuffer.writeByte( 0 );
            channel.write( channelBuffer );
        }
    }

    /**
     * Handles a request to login.
     *
     * @param session The session from which the request was sent from.
     * @param buffer The buffer to parse the request from.
     */
    private void handleLoginRequest( Session session , Buffer buffer )
    {
        Channel channel = session.getChannel();
        int revision = buffer.getDword();
        if( revision != 443 ) {
            logger.log(Level.INFO, "Unexpected client revision sent on login [revision=" + revision + "]");
            ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
            channelBuffer.writeByte(6);
            channel.write( channelBuffer ).addListener(ChannelFutureListener.CLOSE);
        } else {
            buffer.getUbyte();
            for( int i = 0 ; i < 14 ; i++ ) {
                int checksum = buffer.getDword();
                if( checksum != ArchiveManager.getChecksum( 255 , i ) ) {
                    logger.log(Level.INFO, "Unexpected checksum sent on login [id=" + i + ", checksum=" + checksum + "]");
                    ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
                    channelBuffer.writeByte(6);
                    channel.write( channelBuffer ).addListener( ChannelFutureListener.CLOSE );
                    return;
                }
            }
            int length = buffer.getUbyte();
            byte[] bytes = new byte[ length ];
            System.arraycopy( buffer.getPayload() , buffer.getOffset() , bytes , 0 , length );
            if( Constants.RSA_ACTIVE ) {
                throw new RuntimeException("not yet implemented");
            }
            buffer = new Buffer( bytes );
            if( buffer.getUbyte() != 10 ) {
                throw new RuntimeException("invalid rsa check");
            }
            int[] seeds = new int[ 4 ];
            for( int i = 0 ; i < 4 ; i++ ) {
                seeds[ i ] = buffer.getDword();
            }
            buffer.getDword();
            buffer.getQword();
            buffer.getJstr();
            Player player = new Player( session );
            if( !World.getInstance().registerPlayer( player ) ) {
                ChannelBuffer channelBuffer = ChannelBuffers.buffer( 1 );
                channelBuffer.writeByte( 7 );
                session.getChannel().write( channelBuffer ).addListener( ChannelFutureListener.CLOSE );
                return;
            }      
            session.initIncomingIsaac( seeds );
            for( int i = 0 ; i < 4 ; i++) {
                seeds[ i ] += 50;
            }
            session.initOutgoingIsaac( seeds );
            GameSessionHandler handler = new GameSessionHandler( player );
            session.setHandler( handler );
            session.setFrameDecoder( new GameFrameDecoder( handler ) );
            handler.sendLogin( );
        }
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static InitialFrameDecoder getInstance( )
    {
        if( instance == null )
            instance = new InitialFrameDecoder( );
        return instance;
    }
}