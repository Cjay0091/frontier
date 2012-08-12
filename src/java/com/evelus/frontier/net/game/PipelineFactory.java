/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class PipelineFactory implements ChannelPipelineFactory {

    /**
     * The instance of this class.
     */
    private static PipelineFactory instance;

    /**
     * Constructs a new {@link PipelineFactory};
     */
    private PipelineFactory ( ) { }

    @Override
    public ChannelPipeline getPipeline( ) throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addFirst( "handler" , ChannelHandler.getInstance() );
        return pipeline;
    }

    /**
     * Gets the instance of this pipeline factory.
     *
     * @return The instance.
     */
    public static PipelineFactory getInstance( )
    {
        if( instance == null )
            instance = new PipelineFactory( );
        return instance;
    }
}
