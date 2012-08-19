/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.setup;

import com.evelus.frontier.Constants;
import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontierfs.util.CompressionUtils;
import com.evelus.frontierfs.util.FileTable;
import com.evelus.frontierfs.util.FileTable.Entry;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.CRC32;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class MusicSetup {

    /**
     * The main entry point for the program.
     *
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        CRC32 crc = new CRC32();
        File path = new File( args[0] );
        ArchiveManager.initialize( Constants.ARCHIVE_DATABASE_PATH );
        if( !ArchiveManager.load( 255 , 255 ) ) {
            throw new RuntimeException("failed to load the update table");
        }
        FileTable fileTable = new FileTable();
        fileTable.setVersion( 5 );
        fileTable.setFlagActive( FileTable.NAMED_FLAG );
        int counter = 0;
        for( File file : path.listFiles() ) {
            String name = file.getName();
            if( file.isDirectory() || !name.endsWith(".mid")) {
                continue;
            }
            Entry entry = fileTable.createEntry( counter++ );
            entry.setName( name.substring(0, name.indexOf('.') ) );
            byte[] bytes = CompressionUtils.compressArchive( getFile( file ) , CompressionUtils.BZIP_COMPRESSION );
            crc.reset();
            crc.update( bytes );
            entry.setChecksum( (int) crc.getValue() );
            ArchiveManager.putArchive( bytes , 6 , entry.getArchiveId() );
            System.out.println( "Inserted " + file );
        }
        byte[] src = CompressionUtils.compressArchive( fileTable.encode() , CompressionUtils.BZIP_COMPRESSION );
        Buffer buffer = new Buffer( ArchiveManager.getPayload( 255 , 255 ) );
        buffer.setOffset( 5 + (4 * 6) );
        crc.reset();
        crc.update( src );
        buffer.putDword( (int) crc.getValue() );
        ArchiveManager.putArchive( src , 255 , 6 );
        ArchiveManager.putArchive( buffer.getPayload() , 255 , 255 );
    }

    /**
     * Gets the data for a file.
     *
     * @param file The file to get the data for.
     * @return The data for the file.
     */
    private static byte[] getFile( File file ) throws Throwable
    {
        DataInputStream inputStream = new DataInputStream( new FileInputStream( file ));
        byte[] bytes = new byte[ inputStream.available() ];
        inputStream.readFully(bytes);
        inputStream.close();
        return bytes;
    }
}