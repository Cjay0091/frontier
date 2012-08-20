/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */
package com.evelus.frontier.net.game;

import com.evelus.frontier.events.items.ItemEvent;
import com.evelus.frontier.events.widgets.WidgetEvent;
import com.evelus.frontier.game.GameWorld;
import com.evelus.frontier.game.items.GameItem;
import com.evelus.frontier.game.items.ItemContainer;
import com.evelus.frontier.game.items.ItemDefinitionImpl;
import com.evelus.frontier.game.items.ItemLoader;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.game.model.Position;
import com.evelus.frontier.game.model.mob.WalkingQueue;
import com.evelus.frontier.game.widgets.WidgetDefinition;
import com.evelus.frontier.game.widgets.WidgetLoader;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.listeners.items.ItemListener;
import com.evelus.frontier.listeners.widgets.ButtonListener;
import com.evelus.frontier.net.game.codec.FrameEncoder;
import com.evelus.frontier.net.game.frames.SendItemsFrame;
import com.evelus.frontier.net.game.frames.SendMessageFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class GameSessionHandler implements SessionHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(GameSessionHandler.class.getSimpleName());
    /**
     * The widget loader.
     */
    private static WidgetLoader widgetLoader;
    /**
     * The item loader.
     */
    private static ItemLoader itemLoader;

    /**
     * Constructs a new {@link GameSessionHandler};
     * 
     * @param player The player for this session 
     */
    public GameSessionHandler(GamePlayer player) {
        this.player = player;
    }
    /**
     * The player for this session 
     */
    private GamePlayer player;


    /**
     * Sets the widget loader.
     *
     * @param loader The widget loader.
     */
    public static void setWidgetLoader(WidgetLoader loader) {
        widgetLoader = loader;
    }

    /**
     * Sets the item loader.
     *
     * @param loader The item loader.
     */
    public static void setItemLoader(ItemLoader loader) {
        itemLoader = loader;
    }

    @Override
    public void decode(IncomingFrame incomingFrame) {
        int id = incomingFrame.getId();
        int size = incomingFrame.getSize();
        Buffer buffer = new Buffer(incomingFrame.getPayload());
        if(id == 29) {
            int hash = buffer.getDwordA();
            int itemId = buffer.getUword();
            int slot = buffer.getUword128();
            if (!player.getWidgetHandler().widgetOpen(hash >> 16)) {
                return;
            }
            int widgetId = widgetLoader.lookup(hash);
            if (widgetId == -1) {
                return;
            }
            WidgetDefinition definition = widgetLoader.getDefinition(widgetId);
            if (definition.getType() != WidgetDefinition.CONTAINER_TYPE) {
                logger.log(Level.INFO, "Specified widget is not a container [hash=" + hash + "]");
                return;
            }
            int containerId = definition.getContainerId();
            ItemContainer container = player.getItemHandler().getContainer(definition.getContainerId());
            GameItem item = container.getItem(slot);
            if (item == null || item.getId() != itemId) {
                return;
            }
            ItemDefinitionImpl itemDefinition = itemLoader.getDefinition(itemId);
            ItemListener listener = itemLoader.getListener(itemDefinition.getListenerId());
            if (listener != null) {
                ItemEvent itemEvent = new ItemEvent(player, containerId, slot);
                listener.onEquip(itemEvent);
            }
        } else if(id == 54) {
            int hash = buffer.getDword();
            if (!player.getWidgetHandler().widgetOpen(hash >> 16)) {
                return;
            }
            int widgetId = widgetLoader.lookup(hash);
            if (widgetId == -1) {
                return;
            }
            WidgetDefinition definition = widgetLoader.getDefinition(widgetId);
            if (definition.getType() != WidgetDefinition.BUTTON_TYPE) {
                logger.log(Level.INFO, "Specified widget is not a button [hash=" + hash + "]");
                return;
            }
            ButtonListener listener = widgetLoader.getButtonListener(definition.getButtonId());
            if (listener != null) {
                WidgetEvent event = new WidgetEvent(player);
                listener.onActivate(event);
            }
        } else if(id == 70) {
             player.getWidgetHandler().closeWidgets();
        }  else if(id == 99) {
            int[][] steps = new int[(size - buffer.getOffset() - 5) / 2 + 1][2];
            int stepCounter = 1;
            int firstX = buffer.getUwordLe128();
            boolean isRunning = buffer.getUbyteA() == 1;
            int firstY = buffer.getUwordLe();
            for (; stepCounter < steps.length; stepCounter++) {
                steps[stepCounter][0] = buffer.getByte();
                steps[stepCounter][1] = buffer.getByteB();
            }
            WalkingQueue walkingQueue = player.getWalkingQueue();
            walkingQueue.reset();
            player.setRunning(isRunning);
            Position position = player.getPosition();
            int positionX = position.getPositionX();
            int positionY = position.getPositionY();
            for (stepCounter = 0; stepCounter < steps.length; stepCounter++) {
                int targetX = firstX + steps[stepCounter][0];
                int targetY = firstY + steps[stepCounter][1];
                while (positionX != targetX || positionY != targetY) {
                    int deltaX = 0;
                    if (targetX > positionX) {
                        deltaX = 1;
                    } else if (targetX < positionX) {
                        deltaX = -1;
                    }
                    int deltaY = 0;
                    if (targetY > positionY) {
                        deltaY = 1;
                    } else if (targetY < positionY) {
                        deltaY = -1;
                    }
                    walkingQueue.queue(deltaX, deltaY);
                    positionX += deltaX;
                    positionY += deltaY;
                }
            }
        } else if(id == 174) {
            String[] args = buffer.getJstr().split(" ");
            try {
                if (args[0].equals("item")) {
                    int itemId = Integer.parseInt(args[1]);
                    ItemDefinitionImpl definition = itemLoader.getDefinition(itemId);
                    if (definition == null) {
                        player.sendFrame(new SendMessageFrame("No such item exists."));
                        return;
                    }
                    int amount = 1;
                    if (args.length > 2) {
                        amount = Integer.parseInt(args[2]);
                    }
                    ItemContainer container = player.getItemHandler().getContainer(0);
                    if (definition.getStackable()) {
                        GameItem gameItem = new GameItem(itemId, amount);
                        if (!container.addItem(gameItem, true)) {
                            player.sendMessage("Not enough room in your inventory to complete this action.");
                            return;
                        }
                    } else {
                        while (amount-- > 0) {
                            GameItem gameItem = new GameItem(itemId);
                            if (!container.addItem(gameItem, false)) {
                                player.sendMessage("Not enough room in your inventory to complete this action.");
                                return;
                            }
                        }
                    }
                    player.sendFrame(new SendItemsFrame(149, 0, container));
                } else {
                    player.sendMessage("No such command.");
                }
            } catch (Throwable t) {
                player.sendMessage("Error in executing the command.");
            }
        }
    }
    /**
     * Sends all the initial login information.
     */
    public void sendLogin() {
        Channel channel = player.getSession().getChannel();
        ChannelBuffer channelBuffer = ChannelBuffers.buffer(6);
        channelBuffer.writeByte(2);
        channelBuffer.writeByte(2);
        channelBuffer.writeByte(0);
        channelBuffer.writeShort(player.getId());
        channelBuffer.writeByte(1);
        channel.write(channelBuffer);
        channel.getPipeline().addFirst("frameencoder", new FrameEncoder(player.getSession()));
        player.rebuildMap();
        player.getItemHandler().updateContainers();
        player.getWidgetHandler().updateTabs();
        player.getSkillHandler().updateSkills();
        player.sendMessage("Welcome to Frontier");
    }

    @Override
    public void destroy() {
        GameWorld.getInstance().unregisterPlayer(player);
    }
}
