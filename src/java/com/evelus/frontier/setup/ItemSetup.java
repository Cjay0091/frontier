/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.setup;

import java.io.*;
import java.sql.*;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemSetup {

    /**
     * The definitions table name constant.
     */
    private static final String DEFS_TABLE = "evelus_itemdefs";

    /**
     * Prevent construction;
     */
    private ItemSetup ( ) { }

    /**
     * The item definition class.
     */
    private static class ItemDefinition
    {

        /**
         * The id of this item.
         */
        private int id;

        /**
         * The name of this item.
         */
        private String name;
        
        /**
         * The flag for if this item is stackable.
         */
        private boolean stackable;

        /**
         * Constructs a new {@link ItemDefinition};
         */
        public ItemDefinition ( )
        {
            id = -1;
            name = "";
        }
    }

    /**
     * The main entry point of this program.
     *
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Exception
    {
        Connection connection = DriverManager.getConnection( "jdbc:mysql://" + args[0] + "/" + args[1] + "?user=" + args[2] + "&password=" + args[3] );
        FileOutputStream fos = new FileOutputStream( args[4] );
        int amountDefs = getAmountDefs( connection );
        ItemDefinition[] definitions = new ItemDefinition[ amountDefs ];
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
            ResultSet results = statement.executeQuery( "SELECT item_id FROM " + DEFS_TABLE + " ORDER BY 1 DESC" );
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
    private static void loadDefinitions( Connection connection, ItemDefinition[] definitions ) throws SQLException
    {
        Statement statement = connection.createStatement();
        int counter = 0;
        try {
            ResultSet results = statement.executeQuery( "SELECT * FROM " + DEFS_TABLE);
            while( results.next() ) {
                ItemDefinition definition = definitions[ counter++ ] = new ItemDefinition();
                definition.id = results.getInt("item_id");
                definition.name = results.getString("name");
                definition.stackable = results.getInt("is_stackable") == 1;
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
    private static void encodeDefinition( ItemDefinition itemDefinition , OutputStream os ) throws IOException
    {
        writeWord( os , itemDefinition.id );
        os.write(1);
        writeStr( os , itemDefinition.name );
        if( itemDefinition.stackable ) {
            os.write( 2 );
        }
        os.write(0);
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

    /**
     * Writes a jagex formatted string to an outputstream.
     *
     * @param os    The outputstream to write the string to.
     * @param str   The string to write to the output stream.
     */
    private static void writeStr( OutputStream os , String str ) throws IOException
    {
        os.write(str.getBytes());
        os.write(0);
    }

    static {
        try
        {
            Class.forName( "com.mysql.jdbc.Driver" );
        }
         catch(Throwable t)
        {
            throw new RuntimeException(t);
        }
    }
}