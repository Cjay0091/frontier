/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.game.ondemand.OndemandSession;
import com.evelus.frontier.game.ondemand.OndemandWorker;
import com.evelus.frontier.io.ArchiveManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OndemandHandler implements SessionHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger( OndemandHandler.class.getSimpleName() );

    /**
     * Constructs a new {@link OdHandler};
     *
     * @param session The ondemand session for this handler.
     */
    public OndemandHandler ( OndemandSession session )
    {
        this.session = session;
    }

    /**
     * The ondemand session for this handler.
     */
    private OndemandSession session;

    /**
     * Queues a new archive request to the session.
     *
     * @param archiveId The id of the archive to queue for the session.
     * @param indexId The id of he index to queue for the session.
     * @param isPriority Flag for if the request is priority.
     */
    public void queueRequest( int indexId , int archiveId , boolean isPriority )
    {
        if( ArchiveManager.getArchive( indexId , archiveId ) == null ) {
            logger.log( Level.INFO , "Rejected archive request [indexid=" + indexId + ", archiveid=" + archiveId + "]" );
            return;
        }
        int hash = indexId << 16 | archiveId;
        if( isPriority ) {
            session.queuePriorityRequest( hash );
        } else {
            session.queueRegularRequest( hash );
        }
    }

    @Override
    public void destroy( )
    {
        OndemandWorker.getInstance().unregisterSession( session );
    }
}