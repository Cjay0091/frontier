/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.setup;

import com.evelus.frontier.game.widgets.WidgetDefinition;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WidgetSetup {
    
    /**
     * The definitions table name constant.
     */
    private static final String DEFS_TABLE = "evelus_widgetdefs";

    /**
     * Prevent construction;
     */
    private WidgetSetup ( ) { }

    /**
     * The item definition class.
     */
    private static class Definition
    {

        /**
         * The id of this widget.
         */
        private int id;
        
        /**
         * The hash of this widget.
         */
        private int hash;
        
        /**
         * The type of this widget.
         */
        private int type;
        
        /**
         * The id of the container for this widget.
         */
        private int containerId;
        
        /**
         * The size of this widget.
         */
        private int containerSize;
        
        /**
         * The id of the button for this widget.
         */
        private int buttonId;

        /**
         * Constructs a new {@link Definition};
         */
        public Definition ( )
        {
            id = -1;
            hash = -1;
            type = -1;
            containerSize = -1;
        }
    }
    
    /**
     * The main entry point for the program.
     * 
     * @param args The command line arguments.
     */
    public static void main( String[] args )throws Exception
    {
        Connection connection = DriverManager.getConnection( "jdbc:mysql://" + args[0] + "/" + args[1] + "?user=" + args[2] + "&password=" + args[3] );
        FileOutputStream fos = new FileOutputStream( args[4] );
        int amountDefs = getAmountDefs( connection );
        Definition[] definitions = new Definition[ amountDefs ];
        loadDefinitions( connection , definitions );
        writeWord( fos , amountDefs );
        writeWord( fos , getMaximumDef(connection) );
        for( int i = 0; i < amountDefs; i++ ) {
            encodeDefinition( definitions[i] , fos );
        }
        fos.flush();
        fos.close();
    }
    
      /**
     * Gets the amount of item definitions in the definitions table.
     *
     * @param connection        The connection to query the information from.
     * @return                  The amount of definitions.
     * @throws SQLException     A SQLException was thrown while querying the information.
     */
    private static int getAmountDefs( Connection connection ) throws SQLException
    {
        Statement statement = connection.createStatement();
        int amountDefs = -1;
        try {
            ResultSet results = statement.executeQuery( "SELECT COUNT(*) FROM " + DEFS_TABLE );
            if( results.next()) {
                amountDefs = results.getInt(1);
            }
        } finally {
            statement.close();
        }
        return amountDefs;
    }

    /**
     * Gets the maximum definition id.
     *
     * @param connection        The connection to query the information from.
     * @return                  The maximum definition id.
     * @throws SQLException     A SQLException was thrown while querying the information.
     */
    private static int getMaximumDef( Connection connection ) throws SQLException
    {
        Statement statement = connection.createStatement();
        int maximumId = 0;
        try {
            ResultSet results = statement.executeQuery( "SELECT widget_id FROM " + DEFS_TABLE + " ORDER BY 1 DESC" );
            if(results.next()) {
                maximumId = results.getInt(1);
            }
        } finally {
            statement.close();
        }
        return maximumId;
    }

    /**
     * Loads all the definitions in the table.
     *
     * @param connection    The connection to query the information from.
     * @param definitions   The item definitions to load the information to.
     */
    private static void loadDefinitions( Connection connection, Definition[] definitions ) throws SQLException
    {
        Statement statement = connection.createStatement();
        int counter = 0;
        try {
            ResultSet results = statement.executeQuery( "SELECT * FROM " + DEFS_TABLE);
            while( results.next() ) {
                Definition definition = definitions[ counter++ ] = new Definition();
                definition.id = results.getInt("widget_id");
                definition.hash = results.getInt("widget_hash");
                int type = definition.type = results.getInt("type");
                if( type == WidgetDefinition.CONTAINER_TYPE ) {
                    definition.containerId = results.getInt("container_id");
                    definition.containerSize = results.getInt("container_size");
                }
                if( type == WidgetDefinition.BUTTON_TYPE ) {
                    definition.buttonId = results.getInt("button_id");
                }
            }
        } finally {
            statement.close();
        }
    }

    /**
     * Encodes an item definition to an outputstream.
     *
     * @param definition    The definition to encode to the outputstream.
     * @param outputStream  The output stream to encode the definition to.
     */
    private static void encodeDefinition( Definition definition , OutputStream os ) throws IOException
    {
        writeWord( os , definition.id );
        writeDword( os , definition.hash );
        os.write( definition.type );
        
        if( definition.type == WidgetDefinition.CONTAINER_TYPE ) {
            os.write( definition.containerId );
            writeWord( os , definition.containerSize );
        }
        
        if( definition.type == WidgetDefinition.BUTTON_TYPE ) {
            writeWord( os , definition.buttonId );
        }
    }

    /**
     * Writes a dword to an outputstream.
     *
     * @param os    The outputstream to write the word to.
     * @param value The value of the dword to write.
     */
    private static void writeDword( OutputStream os , int value ) throws IOException
    {
        os.write(value >> 24);
        os.write(value >> 16);
        os.write(value >> 8);
        os.write(value);
    }
    
    /**
     * Writes a word to an outputstream.
     *
     * @param os    The outputstream to write the word to.
     * @param value The value of the word to write.
     */
    private static void writeWord( OutputStream os , int value ) throws IOException
    {
        os.write(value >> 8);
        os.write(value);
    }

    static {
        try {
            Class.forName( "com.mysql.jdbc.Driver" );
        } catch(Throwable t) {
            throw new RuntimeException( t );
        }
    }
}