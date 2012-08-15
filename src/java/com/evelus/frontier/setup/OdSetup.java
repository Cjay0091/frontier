/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.setup;

import com.evelus.frontier.Constants;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontierfs.io.CacheIO;
import com.evelus.frontierfs.util.CompressionUtils;
import com.evelus.frontierfs.util.FileTable;
import com.evelus.frontierfs.util.FileTable.Entry;
import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OdSetup {

    /**
     * The CRC32 utility to use for this setup.
     */
    private static CRC32 crc = new CRC32();

    /**
     * The main entry point for the program.
     *
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        System.out.println( "May take a few minutes (maybe an hour :-( )..." );
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;
        try {
            boolean createTable = false;
            if( !new File( Constants.ARCHIVE_DATABASE_PATH ).exists() )
                createTable = true;
            connection = DriverManager.getConnection( "jdbc:sqlite:" + Constants.ARCHIVE_DATABASE_PATH );
            if( createTable ) {
                Statement statement = connection.createStatement();
                try {
                    statement.execute("CREATE TABLE evelus_archives( id INTEGER PRIMARY KEY , checksum INTEGER , data BLOB );");
                } finally {
                    statement.close();
                }
            }
            File path = new File( args[0] );
            RandomAccessFile mainFile = new RandomAccessFile( new File( path , "main_file_cache.dat2" ), "r");
            RandomAccessFile tableFile = new RandomAccessFile( new File( path , "main_file_cache.idx255" ), "r" );
            int maximumId = 0;
            Pattern pattern = Pattern.compile("main_file_cache.idx([0-9]+)");
            LinkedList<Integer> indexes = new LinkedList<Integer>();
            for( File file : path.listFiles() ) {
                Matcher matcher = pattern.matcher( file.getName() );
                if( matcher.find() ) {
                    int id = Integer.parseInt( matcher.group(1) );
                    if( id == 255 )
                        continue;
                    if( id > maximumId )
                        maximumId = id;
                    indexes.add( id );
                }
            }
            CacheIO tableCache = new CacheIO( 255 , mainFile , tableFile );
            int size = (maximumId + 1) * 4;
            Buffer updateTable = new Buffer( size  + 5);
            updateTable.putByte(0);
            updateTable.putDword(size);
            int counter = 0;
            for( int id : indexes ) {
                CacheIO cache = new CacheIO( id , mainFile, new RandomAccessFile( new File( path , "main_file_cache.idx" + id), "r" ) );
                byte[] bytes = tableCache.fetch(id);
                crc.reset();
                crc.update(bytes);
                updateTable.setOffset( 4 * id + 5 );
                updateTable.putDword( (int) crc.getValue() );
               // putArchive( connection , 255 , id , bytes );
                FileTable fileTable = new FileTable( CompressionUtils.decompressArchive(bytes) );
                /*for( int i = 0 ; i < fileTable.getAmountEntries() ; i++ ) {
                    Entry entry = fileTable.getEntry( i );
                    int archiveId = entry.getArchiveId();
                    byte[] src = cache.fetch( entry.getArchiveId() );
                    if( src == null )
                        continue;
                    putArchive( connection , id , archiveId , src );
                    System.out.println(" FINISHED " + i + "/" + fileTable.getAmountEntries() );
                }*/
                System.out.println( "Finished [" + id + "] " + ++counter + "/" + indexes.size() + " indexes...");
            }
            putArchive( connection , 255 , 255 , updateTable.getPayload() );
        } finally {
            connection.close();
        }
    }

    /**
     * Puts an archive into the archives table.
     *
     * @param connection The connection to create the statement from.
     * @param indexId The index id of the archive to put.
     * @param archiveId The id of the archive to put.
     * @param src The source of the archvie to put.
     */
    private static void putArchive( Connection connection , int indexId , int archiveId , byte[] src ) throws Throwable
    {
        int checksum = -1;
        Statement statement = connection.createStatement();
        try {
            ResultSet results = statement.executeQuery("SELECT checksum FROM evelus_archives WHERE id = " + (indexId << 8 | archiveId));
            if( results.next() ) {
                checksum = results.getInt( 1 );
            }
        } finally {
            statement.close();
        }
        crc.reset();
        crc.update(src);
        int cs = (int) crc.getValue();
        //if( cs == checksum )
        //    return;
        PreparedStatement preparedStatement = connection.prepareStatement( "REPLACE INTO evelus_archives ( id , checksum , data ) VALUES ( ? , ? , ? )" );
        try {
            preparedStatement.setInt( 1 , archiveId | indexId << 16 );
            preparedStatement.setInt( 2 , cs );
            preparedStatement.setBytes( 3 , src );
            preparedStatement.execute();
        } finally {
            statement.close();
        }
    }
}