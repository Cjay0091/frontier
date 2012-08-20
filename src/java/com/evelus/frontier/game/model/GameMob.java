/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */
package com.evelus.frontier.game.model;

import com.evelus.frontier.events.game.EffectEvent;
import com.evelus.frontier.game.model.mob.ServerBindings;
import com.evelus.frontier.game.model.mob.WalkingQueue.Step;
import com.evelus.frontier.game.model.mob.ServerBindingsImpl;
import com.evelus.frontier.game.model.mob.WalkingQueue;
import com.evelus.frontier.listeners.game.EffectListener;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public abstract class GameMob extends Entity implements Mob {

    /**
     * Constructs a new {@link Mob};
     */
    public GameMob() {
        initialize();
    }

    /**
     * The walking queue for this mob.
     */
    private WalkingQueue walkingQueue;
    
    /**
     * The server bindings for this mob.
     */
    private ServerBindingsImpl serverBindings;
    
    /**
     * The registered effects for this game mob.
     */
    private LinkedList<EffectListener> effects;

    /**
     * The movement hash of this mob.
     */
    private int movementHash;

    /**
     * The update hash of this mob.
     */
    private int updateHash;

    /**
     * Flag for if the mob is currently running.
     */
    private boolean isRunning;

    /**
     * Flag for if the mob teleported.
     */
    private boolean teleport;

    /**
     * Initializes this mob.
     */
    private void initialize() {
        walkingQueue = new WalkingQueue(100);
        serverBindings = new ServerBindingsImpl(500);
        effects = new LinkedList<EffectListener>();
    }

    /**
     * Updates the movement.
     */
    public void updateMovement() {
        if (!teleport) {
            Step step = walkingQueue.poll();
            if (step != null) {
                Position position = getPosition();
                position.add(step.getDeltaX(), step.getDeltaY());
                movementHash = step.getDirection() << 2;
                if (isRunning && (step = walkingQueue.poll()) != null) {
                    position.add(step.getDeltaX(), step.getDeltaY());
                    movementHash |= step.getDirection() << 5 | 2;
                } else {
                    movementHash |= 1;
                }
            } else {
                movementHash = 0;
            }
        } else {
            movementHash = 3;
        }
    }
    
    /**
     * Updates all the registered effects of this mob.
     */
    public void updateEffects() {
        Iterator<EffectListener> iterator = effects.iterator();
        EffectEvent event = new EffectEvent(this);
        while(iterator.hasNext()) {
            EffectListener listener = iterator.next();
            if(!listener.onUpdate(event)) {
                iterator.remove();
            }
        }
    }

    /**
     * Updates the mob and finalizes everything for a cycle.
     */
    public abstract void update();

    /**
     * Gets the walking queue for this mob.
     * 
     * @return The walking queue.
     */
    public WalkingQueue getWalkingQueue() {
        return walkingQueue;
    }

    /**
     * Sets the movement hash.
     *
     * @param movementHash The movement hash value.
     */
    public void setMovementHash(int movementHash) {
        this.movementHash = movementHash;
    }

    /**
     * Gets the movement hash.
     *
     * @return The movement hash.
     */
    public int getMovementHash() {
        return movementHash;
    }

    /**
     * Sets the update hash for this mob.
     *
     * @param updateHash The update hash value.
     */
    public void setUpdateHash(int updateHash) {
        this.updateHash = updateHash;
    }

    /**
     * Gets the update hash for this mob.
     * 
     * @return The update hash.
     */
    public int getUpdateHash() {
        return updateHash;
    }

    /**
     * Sets if the mob is running.
     *
     * @param isRunning The option for if the mob is running.
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    /**
     * Gets if the mob is running.
     *
     * @return If the mob is running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Sets if the mob has teleported.
     *
     * @param teleport The option for if the mob teleported.
     */
    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
    }

    @Override
    public void registerEffect(EffectListener listener) {
        effects.add(listener);
        listener.onActivate(new EffectEvent(this));
    }
    
    @Override
    public ServerBindings getServerBindings() {
        return serverBindings;
    }
}
