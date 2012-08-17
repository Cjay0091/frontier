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
import com.evelus.frontierfs.util.Archive;
import com.evelus.frontierfs.util.CompressionUtils;
import com.evelus.frontierfs.util.FileTable;
import com.evelus.frontierfs.util.FileTable.Entry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WidgetDump {
    
    /**
     * A widget for this widget dump.
     */
    private static class Widget
    {
        
        /**
         * The flag for if this widget is encoded in the new format.
         */
        boolean isNewFormat;
        
        /**
         * The type of this widget.
         */
        private int type;
        
        /**
         * The widget of this widget.
         */
        private int width;
        
        /**
         * The height of this widget.
         */
        private int height;
        
        /**
         * The alpha of this widget.
         */
        private int alpha;
        
        /**
         * The inactive sprite id.
         */
        private int inactiveSpriteId;
        
        /**
         * The active sprite id.
         */
        private int activeSpriteId;
        
        /**
         * The option to invert the sprite vertically.
         */
        private boolean invertVertical;
        
        /**
         * The option to invert the sprite horizontally.
         */
        private boolean invertHorizontal;
        
        /**
         * The font id for the text of this widget.
         */
        private int fontId;
        
        /**
         * The text for this widget.
         */
        private String text;
        
        /**
         * The inactive color for this widget.
         */
        private int inactiveColor;
        
        /**
         * The active color for this widget.
         */
        private int activeColor;
        
        /**
         * The option to draw a solid quadrilateral.
         */
        private boolean drawSolidQuad;
        
        /**
         * The conditions opcodes.
         */
        private int[] conditionOpcodes;
        
        /**
         * The condition values.
         */
        private int[] conditionValues;
        
        /**
         * The script opcodes.
         */
        private int[][] scriptOpcodes;
     
       /**
        * Decodes the new format.
        * 
        * @param src The source of the archive to decode.
        */
        private void decodeNewFormat( byte[] src )
        {
            isNewFormat = true;
            Buffer buffer = new Buffer( src );
            buffer.getUbyte();
            type = buffer.getUbyte();
            buffer.getUword();
            buffer.getWord();
            buffer.getWord();
            width = buffer.getUword();
            if (type != 9) {
                height = buffer.getUword();
            } else {
                height = buffer.getWord();
            }
            buffer.getUword();
            buffer.getUbyte();
            if (type == 0) {
                buffer.getUword();
                buffer.getUword();
            }
            if (type == 5) {
                inactiveSpriteId = buffer.getDword();
                activeSpriteId = buffer.getUword();
                buffer.getUbyte();
                alpha = buffer.getUbyte();
                buffer.getUbyte();
                buffer.getDword();
                invertVertical = buffer.getUbyte() == 1;
                invertHorizontal = buffer.getUbyte() == 1;
            }
            if (type == 6) {
                buffer.getUword();
                buffer.getWord();
                buffer.getWord();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUbyte();
            }
            if (type == 4) {
                fontId = buffer.getUword();
                if (fontId == 65535) {
                    fontId = -1;
                }
                text = buffer.getJstr();
                buffer.getUbyte();
                buffer.getUbyte();
                buffer.getUbyte();
                buffer.getUbyte();
                inactiveColor = buffer.getDword();
            }
            if (type == 3) {
                inactiveColor = buffer.getDword();
                drawSolidQuad = buffer.getUbyte() == 1;
                alpha = buffer.getUbyte();
            }
            if (type == 9) {
                buffer.getUbyte();
                inactiveColor = buffer.getDword();
            }
            buffer.getUtri();
            buffer.getJstr();
            int i = buffer.getUbyte();
            if (i > 0) {
                for (int j = 0; i > j; j++) {
                    buffer.getJstr();
                }
            }
            buffer.getUbyte();
            buffer.getUbyte();
            buffer.getUbyte();
            buffer.getJstr();
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
            getScriptParams(buffer);
        }

       /**
        * Decodes the old format.
        * 
        * @param src The source of the archive to decode.
        */
        private void decodeOldFormat( byte[] src )
        {
            isNewFormat = false;
            Buffer buffer = new Buffer( src );
            type = buffer.getUbyte();
            int anInt2089 = buffer.getUbyte();
            buffer.getUword();
            buffer.getWord();
            buffer.getWord();
            width = buffer.getUword();
            height = buffer.getUword();
            alpha = buffer.getUbyte();
            buffer.getUword();
            buffer.getUword();
            int amountConditions = buffer.getUbyte();
            if (amountConditions > 0) {
                conditionOpcodes = new int[amountConditions];
                conditionValues = new int[amountConditions];
                for (int i_1_ = 0; amountConditions > i_1_; i_1_++) {
                    conditionOpcodes[i_1_] = buffer.getUbyte();
                    conditionValues[i_1_] = buffer.getUword();
                }
            }
            int amountOpcodes = buffer.getUbyte();
            if (amountOpcodes > 0) {
                scriptOpcodes = new int[amountOpcodes][];
                for (int i_3_ = 0; amountOpcodes > i_3_; i_3_++) {
                    int i_4_ = buffer.getUword();
                    scriptOpcodes[i_3_] = new int[i_4_];
                    for (int i_5_ = 0; i_4_ > i_5_; i_5_++) {
                        scriptOpcodes[i_3_][i_5_] = buffer.getUword();
                        if (scriptOpcodes[i_3_][i_5_] == 65535) {
                            scriptOpcodes[i_3_][i_5_] = -1;
                        }
                    }
                }
            }
            if (type == 0) {
                buffer.getUword();
                buffer.getUbyte();
            }
            if (type == 1) {
                buffer.getUword();
                buffer.getUbyte();
            }
            if (type == 2) {
                int i_6_ = buffer.getUbyte();
                int i_7_ = buffer.getUbyte();
                int i_8_ = buffer.getUbyte();
                int i_9_ = buffer.getUbyte();
                buffer.getUbyte();
                buffer.getUbyte();
                for (int i_10_ = 0; i_10_ < 20; i_10_++) {
                    int i_11_ = buffer.getUbyte();
                    if (i_11_ == 1) {
                        buffer.getWord();
                        buffer.getWord();
                        buffer.getDword();
                    }
                }
                for (int i_12_ = 0; i_12_ < 5; i_12_++) {
                    buffer.getJstr();
                }
            }
            if (type == 3) {
                drawSolidQuad = buffer.getUbyte() == 1;
            }
            if (type == 4 || type == 1) {
                buffer.getUbyte();
                buffer.getUbyte();
                buffer.getUbyte();
                fontId = buffer.getUword();
                if (fontId == 65535) {
                    fontId = -1;
                }
                buffer.getUbyte();
            }
            if (type == 4) {
                text = buffer.getJstr();
                buffer.getJstr();
            }
            if (type == 1 || type == 3 || type == 4) {
                inactiveColor = buffer.getDword();
            }
            if (type == 3 || type == 4) {
                activeColor = buffer.getDword();
                buffer.getDword();
                buffer.getDword();
            }
            if (type == 5) {
                inactiveSpriteId = buffer.getDword();
                activeSpriteId = buffer.getDword();
            }
            if (type == 6) {
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
                buffer.getUword();
            }
            if (type == 7) {
                buffer.getUbyte();
                fontId = buffer.getUword();
                if (fontId == 65535) {
                    fontId = -1;
                }
                buffer.getUbyte();
                inactiveColor = buffer.getDword();
                buffer.getWord();
                buffer.getWord();
                int i_13_ = buffer.getUbyte();
                for (int i_14_ = 0; i_14_ < 5; i_14_++) {
                    buffer.getJstr();
                }
            }
            if (type == 8) {
                text = buffer.getJstr();
            }
            if (anInt2089 == 2 || type == 2) {
                buffer.getJstr();
                buffer.getJstr();
                int i_15_ = buffer.getUword() & 0x3f;
            }
            if (anInt2089 == 1 || anInt2089 == 4 || anInt2089 == 5 || anInt2089 == 6) {
                buffer.getJstr();
            }
        }
        
        /**
         * Gets the script parameters for an action.
         * 
         * @param buffer The buffer to read the data from.
         * @return The script params.
         */
        public Object[] getScriptParams(Buffer buffer) {
            int amountParams = buffer.getUbyte();
            if (amountParams == 0) {
                return null;
            }
            Object[] params = new Object[amountParams];
            for (int i = 0; i < amountParams; i++) {
                int paramType = buffer.getUbyte();
                if (paramType != 0) {
                    if (paramType == 1) {
                        params[i] = buffer.getJstr();
                    }
                } else {
                    params[i] = new Integer(buffer.getDword());
                }
            }
            return params;
        }
    }
    
    /**
     * The main entry point for the program.
     * 
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        File path = new File( args[ 0 ] );
        ArchiveManager.initialize( Constants.ARCHIVE_DATABASE_PATH ); 
        if( !ArchiveManager.load( 255 , 3 ) ) {
            throw new RuntimeException("failed to load the widget file table");
        }
        FileTable fileTable = new FileTable( CompressionUtils.decompressArchive( ArchiveManager.getPayload( 255 , 3 ) ) );
        for( int i = 0 ; i < fileTable.getAmountEntries() ; i++ ) {
            Entry entry = fileTable.getEntry( i );
            int archiveId = entry.getArchiveId();
            if( !ArchiveManager.load( 3 , archiveId ) ) {
                System.out.println("Failed to load archive for widget [id=" + entry.getArchiveId() + "]");
                continue;
            }
            byte[] src = CompressionUtils.decompressArchive( ArchiveManager.getPayload( 3 , archiveId ) );
            BufferedWriter writer = new BufferedWriter( new FileWriter( new File( path , "w" + archiveId ) ) );
            int amountChildren = entry.getAmountChildren();
            writeHeader( writer , archiveId , amountChildren );
            if( amountChildren > 1 ) {
                Archive archive = new Archive( src  , amountChildren );
                for( int j = 0 ; j < amountChildren ; j++ ) {
                    byte[] widgetSrc = archive.getFile( j );
                    Widget widget = decode( widgetSrc );
                    writeChild( writer , widget , j );
                }
            } else {
                Widget widget = decode( src );
                writeChild( writer , widget , 0 );
            }
            writer.flush();
            writer.close();
        } 
    }
    
    /**
     * Decodes a widget.
     * 
     * @param src The source of the widget to decode.
     * @return The widget.
     */
    private static Widget decode( byte[] src )
    {
        Widget widget= new Widget();
        if( src[0] == -1 ) {
            widget.decodeNewFormat( src );
        } else {
            widget.decodeOldFormat( src );
        }
        return widget;
    } 

    /**
     * Writes the header for a file.
     * 
     * @param writer The buffered writer to append the header to.
     */
    private static void writeHeader( BufferedWriter writer , int id , int amountChildren ) throws IOException
    {
        writer.append("|-------------------------------------------------------|\n");
        writer.append("|                                                       |\n");
        writer.append("|                                                       |\n");
        writer.append("|                                                       |\n");
        writer.append("|          Widget Dump - Id: " + id + ", Children: " + amountChildren + "            |\n");
        writer.append("|                                                       |\n");
        writer.append("|                                                       |\n");
        writer.append("|-------------------------------------------------------|\n");
    }
    
    /**
     * Writes the information for a child widget.
     * 
     * @param writer The buffered writer to append the information to.
     * @param widget The widget to write about.
     * @param childId The child id of the widget.
     */
    private static void writeChild( BufferedWriter writer , Widget widget , int childId ) throws IOException
    {
        writer.append("\n\n\n|-------------------------------------------------------|\n\n");
        writer.append("                       Child - " + childId + "\n\n");
        writer.append("|-------------------------------------------------------|\n\n");
        writer.append( "Type: " + widget.type + "\n" );
        writer.append( "Width: " + widget.width + "\n" );
        writer.append( "Height: " + widget.height + "\n" );
        if( widget.isNewFormat ) {
            
        } else {
            writer.append( "Alpha: " + widget.alpha + "\n" );
            writeConditions( writer , widget );
            writeScripts( writer , widget );
        }
    }
    
    /**
     * Writes the conditions for a widget.
     * 
     * @param writer The writer to append the information to.
     * @param widget The widget to write the condition information for.
     */
    private static void writeConditions( BufferedWriter writer , Widget widget ) throws IOException
    {
        if( widget.conditionOpcodes == null ) {
            return;
        }
        writer.append("\nConditions:\n\n");
        for( int i = 0 ; i < widget.conditionOpcodes.length ; i++ ) {
            String comparison = "!=";
            int opcode = widget.conditionOpcodes[ i ];
            if (opcode == 2) {
                comparison = ">";
	    } else if (opcode == 3) {
		comparison = "<";
	    } else if (opcode != 4) {
		comparison = "==";
	    }
            writer.write("\tscript(" + i + ")" + comparison + " " + widget.conditionValues[i] + "\n\n");
        }
    }
    
    /**
     * Writes the scripts for a widget.
     * 
     * @param writer The writer to append the information to.
     * @param widget The widget to write the scripts for.
     */
    private static void writeScripts( BufferedWriter writer , Widget widget ) throws IOException
    {
        if( widget.scriptOpcodes == null ) {
            return;
        }
        writer.append("\nScripts:\n\n");
        for( int i = 0 ; i < widget.scriptOpcodes.length ; i++ ) {
            int[] opcodes = widget.scriptOpcodes[ i ];
            writer.append("\tScript " + i + ":\n\n");
            writer.append("\t\t");
            int offset = 0;
            int arithmetic = 0;   
            boolean bool = false;
            for( ; ; ) {
                int opcode = opcodes[ offset++ ];
                String operation = "";
                int queuedArithmetic = 0;
                if( opcode == 0 )
                    break;
                if (opcode == 15)
		    queuedArithmetic = 1;
		if (opcode == 1)
		    operation = "dynamicLevel(" + opcodes[ offset++ ] + ")";
		if (opcode == 16)
		    queuedArithmetic = 2;
		if (opcode == 17)
		    queuedArithmetic = 3;
		if (opcode == 2)
		    operation = "staticLevel(" + opcodes[ offset++ ] + ")";
		if (opcode == 3)
		    operation = "levelXp(" + opcodes[ offset++ ] + ")";
		if (opcode == 4) {
		    int parentId = opcodes[offset++];
		    int childId = opcodes[offset++];
		    int itemId = opcodes[offset++];
                    operation = "countOfItem(" + parentId + " , " + childId + " , " + itemId + ")";
		}
		if (opcode == 5)
		    operation = "state(" + opcodes[offset++] + ")";
		if (opcode == 6) {
                    operation = "UNKNOWN";
		    offset++;
                }
		if (opcode == 7) {
		    operation = "UNKNOWN";
		    offset++;
                }
                if (opcode == 8)
		    operation = "UNKNOWN";
		if (opcode == 9) {
		    operation = "UNKNOWN";
		}
		if (opcode == 10) {
		    int i_17_ = opcodes[offset++] << 16;
		    i_17_ += opcodes[offset++];
		    int i_19_ = opcodes[offset++];
                    operation = "UNKNOWN";
		} else if (opcode == 11) {
		    operation = "UNKNOWN";
                } else if (opcode == 12) {
		    operation = "UNKNOWN";
                } else if (opcode == 13) {
		    int i_21_ = opcodes[offset++];
		    int i_22_ = opcodes[offset++];
                    operation = "getBitActive(" + i_21_ + " , " + i_22_ + ")";
		} else if (opcode == 14) {
		    int i_23_ = opcodes[offset++];
		    operation = "getVarbit(" + i_23_ + ")";
		} else if (opcode == 18) {
		    operation = "UNKNOWN";
                } else if (opcode == 19) {
		    operation = "UNKNOWN";
                } else if (opcode == 20) {
                    operation = "" + opcodes[offset++];
                } 
                if (queuedArithmetic != 0) {
                    arithmetic = queuedArithmetic;
                } else {
                    if( bool ) {
                        char op = '+';
                        if (arithmetic == 1) {
                            op = '-';
                        } else if (arithmetic == 2) {
                            op = '/';
                        } else if (arithmetic == 3) {
                            op = '*';
                        }
                        writer.append( " " + op + " " );
                    } else {
                        bool = true;
                    }
                    writer.append( operation );
		    arithmetic = 0;
		}
            }
            writer.append("\n\n");
        }
    }
}