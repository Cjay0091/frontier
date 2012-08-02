/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.util;

import java.util.Iterator;
import java.util.Stack;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class BackedList<T> implements Iterable<T> {

    /**
     * Constructs a new {@link BackedList};
     */
    public BackedList ( int listSize )
    {
        head = new Node();
        head.prev = head;
        head.next = head;
        nodes = new Node[ listSize ];
        this.listSize = listSize;
        initialize();
    }

    /**
     * The node for this backed list where the element is stored.
     */
    private class Node<T>
    {

        /**
         * The index of this node.
         */
        int index;

        /**
         * The next node in this list.
         */
        Node next;

        /**
         * The previous node in this list.
         */
        Node prev;

        /**
         * The element for this node.
         */
        T element;

        /**
         * Unlinks this node from the active list of nodes.
         */
        public final void unlink( )
        {
            if(next != null) {
                next.prev = prev;
                prev.next = next;
                prev = null;
                next = null;
            }
        }
    }

    /**
     * The custom iterator implementation for this list.
     */
    private class ListIterator implements Iterator<T> {

        /**
         * The node to iterate through the list with.
         */
        Node node;

        @Override
        public boolean hasNext( )
        {
            return node.next != head;
        }

        @Override
        public T next( )
        {
            node = node.next;
            if( node == head )
                throw new IllegalStateException( );
            return (T) node.element;
        }

        @Override
        public void remove( )
        {
            Node currentNode = node;
            node = node.prev;
            currentNode.unlink();
        }

        /**
         * Constructs a new {@link ListIterator};
         */
        ListIterator ( )
        {
            node = head;
        }
    }

    /**
     * The node array where all the elements for this list are stored.
     */
    private Node[] nodes;

    /**
     * The head linked list node.
     */
    private Node head;

    /**
     * The static size of this list.
     */
    private int listSize;

    /**
     * The stack of unused elements in this list.
     */
    private Stack<Node> unusedElements;

    /**
     * Initializes this list.
     */
    private void initialize( )
    {
        for( int i = 0 ; i < listSize ; i++ ) {
            Node node = new Node();
            node.index = i;
            unusedElements.push( node );
        }
    }

    /**
     * Gets an element in this list.
     *
     * @return The element in this list.
     */
    public T getElement( int index )
    {
        Node node = nodes[ index ];
        if( node == null )
            return null;
        return (T) node.element;
    }

    /**
     * Adds an element to this list.
     *
     * @param t The element to add into this list.
     * @return The index at which the element was added.
     */
    public int addElement( T t )
    {
        Node node = unusedElements.pop();
        if( node == null )
            return -1;
        int index = node.index;
        nodes[ index ] = node;
        node.element = t;
        node.next = head.next;
        node.prev = head;
        node.next.prev = node;
        node.prev.next = node;
        return index;
    }

    /**
     * Removes an element from this list.
     *
     * @param index The index of the element to remove.
     */
    public void removeElement( int index )
    {
        Node node = nodes[ index ];
        if( node == null )
            return;
        unusedElements.push( node );
        nodes[ index ] = null;
        node.unlink();
    }

    /**
     * Gets an iterator for this list.
     *
     * @return An iterator.
     */
    public Iterator<T> iterator( )
    {
        return new ListIterator();
    }
}