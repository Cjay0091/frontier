/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.IncomingFrame;
import com.evelus.frontier.net.Session;
import com.evelus.frontier.net.SessionHandler;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OdHandler implements SessionHandler {

    /**
     * The client state is currently set to being offline.
     */
    private static final int OFFLINE_STATE = 0;

    /**
     * The client state is currently set to being online.
     */
    private static final int ONLINE_STATE = 1;

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(OdHandler.class.getSimpleName());

    /**
     * Constructs a new {@link OdHandler};
     */
    public OdHandler ( ) 
    {
        priorityRequests = new LinkedList<Integer>();
        regularRequests = new LinkedList<Integer>();
        currentRequest = -1;
        currentBlock = -1;
        state = -1;
    }

    /**
     * The queue of priority archive requests.
     */
    private Queue<Integer> priorityRequests;

    /**
     * The queue of regular archive requests.
     */
    private Queue<Integer> regularRequests;

    /**
     * The current request hash.
     */
    private int currentRequest;

    /**
     * The current block for the request.
     */
    private int currentBlock;

    /**
     * The current state of the client requesting archives.
     */
    private int state;

    @Override
    public void handleIncomingFrame( Session session , IncomingFrame incomingFrame )
    {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer( incomingFrame.getPayload() );
        switch(id) {
            case 0:
            case 1:
                parseArchiveRequest( buffer , id == 1 );
                return;
            case 3:
                state = OFFLINE_STATE;
                return;
            case 4:
                state = ONLINE_STATE;
                return;
            default:
                logger.log(Level.INFO, "Undeclared request sent from client [id=" + id + "]");
                session.getChannel().close();
                return;
        }
    }

    @Override
    public void update( Session session )
    {
        if( currentRequest == -1 ) {
            if( !priorityRequests.isEmpty() ) {
                currentRequest = priorityRequests.poll();
            } else if( !regularRequests.isEmpty() ) {
                currentRequest = regularRequests.poll();
            }
            if( currentRequest == -1 )
                return;
            currentBlock = 0;
        }
    }

    /**
     * Parses an archive request.
     *
     * @param buffer The buffer to parse the request from.
     * @param isPriority If the archive to be downloaded is a priority.
     */
    private void parseArchiveRequest( Buffer buffer , boolean isPriority )
    {
        int hash = buffer.getUtri();
        if( isPriority ) {
            priorityRequests.add( hash );
        } else {
            regularRequests.add( hash );
        }
    }
}