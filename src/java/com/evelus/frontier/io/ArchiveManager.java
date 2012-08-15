/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ArchiveManager {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger( ArchiveManager.class.getSimpleName() );

    /**
     * The archives sources.
     */
    private static byte[][][] archives;

    /**
     * The connection to the archives database.
     */
    private static Connection connection;

    /**
     * Initializes this archive manager.
     *
     * @param filePath The path to the database file to use to initialize with.
     */
    public static void initialize( String filePath )
    {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection( "jdbc:sqlite:" + filePath );
            archives = new byte[ 256 ][ ][ ];
        } catch( Throwable t ) {
            logger.log( Level.INFO , "Failed to initialize the archive manager." );
        }
    }

    /**
     * Loads all the archives.
     */
    public static void loadAll( ) throws Throwable
    {
        if( connection == null )
            throw new IllegalStateException("not yet initialized");
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT id , data FROM evelus_archives");
            while( resultSet.next() ) {
                int id = resultSet.getInt( 1 );
                int indexId = id >> 16;
                int archiveId = id & 0xFFFF;
                if( archives[ indexId ] == null) {
                    initializeIndex( indexId );
                }
                if( archives[ indexId ][ archiveId ] != null )
                    continue;
                archives[ indexId ][ archiveId ] = resultSet.getBytes( 2 );
            }
            logger.log( Level.INFO , "Successfully loaded all the archives into memory." );
        } finally {
            statement.close();
        }
    }

    /**
     * Gets the source of an archive from this archive manager.
     *
     * @param indexId The index of the archive.
     * @param archiveId The id of the archive.
     * @return The source of the archive.
     */
    public static byte[] getArchive( int indexId , int archiveId )
    {
        if( indexId < 0 || indexId >= archives.length || archives[ indexId ] == null || archiveId < 0 || archiveId >= archives[ indexId ].length )
            return null;
        return archives[ indexId ][ archiveId ];
    }

    /**
     * Initializes an index.
     *
     * @param id The id of the index to initialize.
     */
    private static void initializeIndex( int id ) throws Throwable
    {
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT MAX( id & 65535 ) FROM evelus_archives WHERE (id >> 16) = " + id);
            if( resultSet.next() ) {
                archives[ id ] = new byte[ resultSet.getInt( 1 ) + 1 ][ ];
            }
        } finally {
            statement.close();
        }
    }
}
