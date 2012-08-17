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
import com.evelus.frontierfs.io.CacheIO;
import com.evelus.frontierfs.util.CompressionUtils;
import com.evelus.frontierfs.util.FileTable;
import com.evelus.frontierfs.util.FileTable.Entry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import org.evelus.tekkfs.ArchivePackage;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class MapConvert {
 
    /**
     * The main entry point for a program.
     * 
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        CRC32 crc = new CRC32();
        ArchiveManager.initialize( Constants.ARCHIVE_DATABASE_PATH );
        if( !ArchiveManager.load( 255 , 255  ) )
            throw new RuntimeException("check table does not exist");
        
        RandomAccessFile mainFile = new RandomAccessFile( new File( args[0] , "main_file_cache.dat"), "r" );
        CacheIO configCache = new CacheIO( 1 , mainFile , new RandomAccessFile( new File( args[0] , "main_file_cache.idx0"), "r" ) );
        CacheIO landscapeCache = new CacheIO( 5 , mainFile , new RandomAccessFile( new File( args[0] , "main_file_cache.idx4"), "r" ) );
        FileTable fileTable = new FileTable();
        fileTable.setVersion( 5 );
        fileTable.setFlagActive( FileTable.NAMED_FLAG );
        ArchivePackage configPack = new ArchivePackage( configCache.fetch( 5 ) ); 
        Buffer mapIndex = new Buffer( configPack.getArchive("map_index") );
        int amountEntries = mapIndex.getPayload().length / 7;
        for( int i = 0 ; i < amountEntries ; i++ ) {
            System.out.println( "STARTED " + (i + 1) + "/" + amountEntries );
            int hash = mapIndex.getUword();
            int regionX = hash >> 8;
            int regionY = hash & 0xFF;
            int mapId = mapIndex.getUword();
            int landscapeId = mapIndex.getUword();
            mapIndex.getUbyte();
            Entry mapEntry = fileTable.createEntry( 2 * i );
            mapEntry.setName( "m" + regionX + "_" + regionY );
            byte[] mapSrc = CompressionUtils.compressArchive( decompressArchive( landscapeCache.fetch( mapId ) ) , CompressionUtils.GZIP_COMPRESSION );
            crc.reset();
            crc.update(mapSrc);
            mapEntry.setChecksum( (int) crc.getValue() );
            ArchiveManager.putArchive( mapSrc , 5 , mapEntry.getArchiveId() );
            Entry landscapeEntry = fileTable.createEntry( (2 * i) + 1 );
            landscapeEntry.setName( "l" + regionX + "_" + regionY );
            byte[] landscapeSrc = CompressionUtils.compressArchive( decompressArchive( landscapeCache.fetch( landscapeId ) ) , CompressionUtils.GZIP_COMPRESSION );
            crc.reset();
            crc.update(landscapeSrc);
            landscapeEntry.setChecksum( (int) crc.getValue() );
            ArchiveManager.putArchive( landscapeSrc , 5 , landscapeEntry.getArchiveId() );
        }    
        byte[] tableSrc = CompressionUtils.compressArchive( fileTable.encode() , CompressionUtils.BZIP_COMPRESSION );
        ArchiveManager.putArchive( tableSrc , 255 , 5 );
        Buffer buffer = new Buffer( ArchiveManager.getPayload( 255 , 255 ) );
        buffer.setOffset(25);
        crc.reset();
        crc.update( tableSrc );
        buffer.putDword( (int) crc.getValue() );
        ArchiveManager.putArchive( buffer.getPayload() , 255 , 255 );
    }
    
    /**
     * Decompresses an archive.
     * 
     * @param src The source of the archive to decompress.
     * @return The decompressed archive.
     */
    private static byte[] decompressArchive( byte[] src ) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPInputStream is = new GZIPInputStream( new ByteArrayInputStream( src ) );
        byte[] buffer = new byte[ 1024 ];
        while(true) {
            int read = is.read( buffer );
            if( read == -1 ) {
                break;
            }
            os.write(buffer, 0, read);
        }
        return os.toByteArray();
    }
}