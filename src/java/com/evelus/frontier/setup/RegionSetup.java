/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.setup;

import com.evelus.frontier.Constants;
import com.evelus.frontier.game.model.Position;
import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontierfs.util.CompressionUtils;
import com.evelus.frontierfs.util.FileTable;
import com.evelus.frontierfs.util.FileTable.Entry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class RegionSetup {

    /**
     * The main entry point for the program.
     * 
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        ArchiveManager.initialize( Constants.ARCHIVE_DATABASE_PATH );
        if( !ArchiveManager.load( 255 , 5 ) )
            throw new IOException("failed to load table");
        FileOutputStream os = new FileOutputStream( Constants.REGION_CONFIG_PATH );
        FileTable fileTable = new FileTable( CompressionUtils.decompressArchive( ArchiveManager.getArchive( 255 , 5 ) ) );
        for( int x = 0 ; x < (Position.MAXIMUM_X >> 6) ; x++ ) {
            for( int y = 0 ; y < (Position.MAXIMUM_Y >> 6) ; y++ ) {
                Entry entry = fileTable.lookup("m" + x + "_" + y);
                if( entry == null )
                    continue;
                if( !ArchiveManager.load( 5 , entry.getArchiveId() ) ) {
                    System.out.println( "Failed to load archive [regionx=" + x + ", regiony=" + y + "]" );
                    continue;
                }
                os.write(1);
                writeWord( os , x << 8 | y );
                boolean[][] flags = new boolean[ 8 ][ 8 ];
                getActiveSectors( flags , CompressionUtils.decompressArchive( ArchiveManager.getArchive( 5 , entry.getArchiveId() ) ) );
                for( int sectorX = 0 ; sectorX < 8 ; sectorX++ ) {
                    for( int sectorY = 0 ; sectorY < 8 ; sectorY++ ) {
                        if( !flags[ sectorX ][ sectorY ])
                            continue;
                        os.write(1);
                        os.write(sectorX << 4 | sectorY);
                    }
                }
                os.write(0);
            }
        }
        os.write(0);
        os.flush();
        os.close();
    }

    /**
     * Gets the active sectors from a map file.
     *
     * @param flags The active sector flags.
     * @param src The source of the map file.
     */
    private static void getActiveSectors( boolean[][] flags , byte[] src )
    {
        Buffer buffer = new Buffer( src );
        for( int z = 0 ; z < 4 ; z++ ) {
            for( int x = 0 ; x < 64 ; x++ ) {
                for( int y = 0 ; y < 64 ; y++ ) {
                    int opcode = buffer.getUbyte();
                    if ( opcode == 0 )
                        break;
                    if ( opcode == 1 ) {
                        buffer.getUbyte();
                        break;
                    }
                    if ( opcode <= 49 ) {
                        flags[ x >> 3 ][ y >> 3 ] = true;
                        buffer.getUbyte();
                    } else if (opcode > 81) {
                        flags[ x >> 3 ][ y >> 3 ] = true;
                    }
                }
            }
        }
    }

    /**
     * Writes a word to an outputstream.
     *
     * @param os The outputstream to write the value to.
     * @param value The value to write.
     */
    private static void writeWord( OutputStream os , int value ) throws IOException
    {
        os.write( value >> 8 );
        os.write( value );
    }
}
