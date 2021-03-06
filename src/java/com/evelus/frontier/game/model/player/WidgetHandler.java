/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */
package com.evelus.frontier.game.model.player;

import com.evelus.frontier.events.widgets.WidgetEvent;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.game.widgets.WidgetDefinition;
import com.evelus.frontier.game.widgets.WidgetLoader;
import com.evelus.frontier.listeners.widgets.WindowListener;
import com.evelus.frontier.net.game.frames.DisplayChatboxInterfaceFrame;
import com.evelus.frontier.net.game.frames.DisplayOverlayFrame;
import com.evelus.frontier.net.game.frames.DisplayTabOverlayFrame;
import com.evelus.frontier.net.game.frames.DisplayWindowFrame;
import com.evelus.frontier.net.game.frames.SetTabWidgetFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WidgetHandler {

    /**
     * The default tab widgets.
     */
    private static final int[] DEFAULT_TAB_WIDGETS;
    /**
     * The widget loader for this widget handler.
     */
    private static WidgetLoader widgetLoader;

    /**
     * Constructs a new {@link WidgetHandler};
     * 
     * @param player The player for this widget handler.
     */
    public WidgetHandler(GamePlayer player) {
        this.player = player;
        windowId = -1;
        overlayId = -1;
        tabId = -1;
        initialize();
    }

    /**
     * The player for this widget handler.
     */
    private GamePlayer player;

    /**
     * The currently active window id.
     */
    private int windowId;

    /**
     * The currently active widget overlay parent id.
     */
    private int overlayId;

    /**
     * The currently active tab overlay parent id.
     */
    private int tabId;

    /**
     * The currently active chatbox overlay id.
     */
    private int chatboxId;

    /**
     * The widget ids for all the tabs.
     */
    private int[] tabWidgetIds;

    /**
     * Initializes this widget handler.
     */
    private void initialize() {
        tabWidgetIds = new int[15];
        for (int i = 0; i < 15; i++) {
            tabWidgetIds[i] = DEFAULT_TAB_WIDGETS[i];
        }
    }

    /**
     * Sets the currently open window.
     *
     * @param id The window widget id.
     */
    public void setWindow(int id) {
        if (windowId != id) {
            WidgetDefinition definition = widgetLoader.getWindow(id);
            if (definition == null) {
                return;
            }
            WindowListener listener = widgetLoader.getWindowListener(definition.getListenerId());
            if (listener != null) {
                WidgetEvent event = new WidgetEvent(player);
                listener.onOpen(event);
            }
            player.sendFrame(new DisplayWindowFrame(definition.getParentId()));
            windowId = id;
        }
    }

    /**
     * Sets the current overlay.
     *
     * @param id The overlay widget id.
     */
    public void setOverlay(int id) {
        if (overlayId != id) {
            player.sendFrame(new DisplayOverlayFrame(id));
            overlayId = id;
        }
    }

    /**
     * Displays an inventory window.
     * 
     * @param tab The tab widget id.
     * @param window The window id.
     */
    public void setTabAndWindow(int tab, int window) {
        if(tabId != tab) {
            WidgetDefinition definition = widgetLoader.getWindow(window);
            if (definition == null) {
                return;
            }
            WindowListener listener = widgetLoader.getWindowListener(definition.getListenerId());
            if (listener != null) {
                WidgetEvent event = new WidgetEvent(player);
                listener.onOpen(event);
            }
            player.sendFrame(new DisplayTabOverlayFrame(definition.getParentId(), tab));
            tabId = tab;
            windowId = window;
        }
    }

    /**
     * Sets the chatbox interface.
     *
     * @param id The parent id of the widget to set the chatbox as.
     */
    public void displayChatboxInterface(int id) {
        if(chatboxId != id) {
            player.sendFrame(new DisplayChatboxInterfaceFrame(id));
            chatboxId = id;
        }
    }

    /**
     * Closes all the displayed widgets.
     */
    public void closeWidgets() {
        if (windowId != -1) {
            WidgetDefinition definition = widgetLoader.getWindow(windowId);
            WindowListener listener = widgetLoader.getWindowListener(definition.getWindowId());
            if (listener != null) {
                WidgetEvent event = new WidgetEvent(player);
                listener.onClose(event);
            }
        }
        windowId = -1;
        tabId = -1;
    }

    /**
     * Gets if a widget is currently open.
     *
     * @param parentId The parent id of the widget to see if its open.
     * @return If the widget is currently open on the clients screen.
     */
    public boolean widgetOpen(int parentId) {
        if (windowId == parentId) {
            return true;
        }
        if (overlayId == parentId) {
            return true;
        }
        if(tabId == -1) {
            for (int i = 0; i < 15; i++) {
                if (tabWidgetIds[i] == parentId) {
                    return true;
                }
            }
        } else if(tabId == parentId) {
            return true;
        }
        return false;
    }

    /**
     * Update all the tabs.
     */
    public void updateTabs() {
        for (int i = 0; i < 15; i++) {
            updateTab(i);
        }
    }

    /**
     * Updates a tab.
     * 
     * @param id The id of the tab to update.
     */
    public void updateTab(int id) {
        player.sendFrame(new SetTabWidgetFrame(id, tabWidgetIds[id]));
    }

    /**
     * Sets the widget loader for this widget handler.
     *
     * @param loader The widget loader.
     */
    public static void setWidgetLoader(WidgetLoader loader) {
        widgetLoader = loader;
    }

    static {
        DEFAULT_TAB_WIDGETS = new int[15];
        for (int i = 0; i < 15; i++) {
            DEFAULT_TAB_WIDGETS[i] = -1;
        }
        DEFAULT_TAB_WIDGETS[  1] = 320;
        DEFAULT_TAB_WIDGETS[  3] = 149;
        DEFAULT_TAB_WIDGETS[  4] = 387;
        DEFAULT_TAB_WIDGETS[ 10] = 182;
    }
}
