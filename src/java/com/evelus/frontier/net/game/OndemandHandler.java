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
import com.evelus.frontier.io.Buffer;
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
    private static final Logger logger = Logger.getLogger(OndemandHandler.class.getSimpleName());

    /**
     * Constructs a new {@link OdHandler};
     *
     * @param session The ondemand session for this handler.
     */
    public OndemandHandler(OndemandSession session) {
        this.session = session;
    }
    /**
     * The ondemand session for this handler.
     */
    private OndemandSession session;

    @Override
    public void decode(IncomingFrame incomingFrame) {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer(incomingFrame.getPayload());
        if (id == 0 || id == 1) {
            int indexId = buffer.getUbyte();
            int archiveId = buffer.getUword();
            if (ArchiveManager.getPayload(indexId, archiveId) == null) {
                logger.log(Level.INFO, "Rejected archive request [indexid=" + indexId + ", archiveid=" + archiveId + "]");
                return;
            }
            int hash = indexId << 16 | archiveId;
            if (id == 1) {
                session.queuePriorityRequest(hash);
            } else {
                session.queueRegularRequest(hash);
            }
        }
    }

    @Override
    public void destroy() {
        OndemandWorker.getInstance().unregisterSession(session);
    }
}
