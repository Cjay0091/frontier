/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.dump;

import com.evelus.frontier.Constants;
import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontierfs.util.CompressionUtils;
import com.evelus.frontierfs.util.FileTable;
import com.evelus.frontierfs.util.FileTable.Entry;
import java.io.File;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ScriptDump {
    
    /**
     * Prevent construction;
     */
    private ScriptDump ( ) { }
    
    /**
     * A client script.
     */
    private static class ClientScript 
    {
        /**
         * The amount of integer variables both local and parameters.
         */
        int amountIntVars;
        
        /**
         * The amount of string variables both local and parameters.
         */
        int amountStrVars;
        
        /**
         * The amount of integer parameters.
         */
        int amountIntParams;
        
        /**
         * The amount of string parameters.
         */
        int amountStrParams;
        
        /**
         * The opcodes for this script.
         */
        int[] opcodes;
        
        /**
         * The integer operands.
         */
        int[] intOperands;
        
        /**
         * The string oeprands.
         */
        String[] strOperands;
    }
    
    /**
     * The main entry point for the program.
     * 
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        File file = new File( args[ 0 ] );
        ArchiveManager.initialize( Constants.ARCHIVE_DATABASE_PATH );
        if( !ArchiveManager.load( 255 , 12 ) ) {
            throw new RuntimeException("failed to load the cs2 table");
        }
        FileTable fileTable = new FileTable( CompressionUtils.decompressArchive( ArchiveManager.getPayload( 255 , 12 ) ) );
        for( int i = 0 ; i < fileTable.getAmountEntries() ; i++ ) {
            Entry entry = fileTable.getEntry( i );
            int archiveId = entry.getArchiveId();
            if( !ArchiveManager.load( 12 , archiveId ) ) {
                continue;
            }
            ClientScript clientScript = createClientScript( CompressionUtils.decompressArchive( ArchiveManager.getPayload( 12 , archiveId ) ) );
        }
    }
    
    /**
     * Creates a new client script.
     * 
     * @param src The source to create the client script from.
     * @return The created client script.
     */
    private static ClientScript createClientScript( byte[] src )        
    {   
        ClientScript clientScript = new ClientScript( );
        Buffer buffer = new Buffer( src );
        buffer.setOffset( src.length - 12 );
        int amountOpcodes = buffer.getDword();
        clientScript.amountIntVars = buffer.getUword();
        clientScript.amountStrVars = buffer.getUword();
        clientScript.amountIntParams = buffer.getUword();
        clientScript.amountStrParams = buffer.getUword();
        clientScript.opcodes = new int[ amountOpcodes ];
        clientScript.intOperands = new int[ amountOpcodes ];
        clientScript.strOperands = new String[ amountOpcodes ];
        buffer.setOffset( 0 );
        int offset = 0;
        while (buffer.getOffset() < buffer.getPayload().length - 12) {
	    int operand = buffer.getUword();
	    if (operand != 3) {
		if (operand < 100 && operand != 21 && operand != 38 && operand != 39) {
                    clientScript.intOperands[offset] = buffer.getDword();
                } else {
                    clientScript.intOperands[offset] = buffer.getUbyte();
                }
	    } else {
                clientScript.strOperands[offset] = buffer.getJstr();
            }
	    clientScript.opcodes[offset++] = operand;
	}
        return clientScript;
    }
}
