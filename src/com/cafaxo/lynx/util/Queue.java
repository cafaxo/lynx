package com.cafaxo.lynx.util;

public class Queue<T>
{

    private Object[] elements;

    private int offset, position;

    public Queue(int capacity)
    {
        this.elements = new Object[capacity];
    }

    public void add(T element)
    {
        this.elements[this.offset] = element;
        this.offset++;
    }

    public boolean hasNext()
    {
        return this.position < this.offset;
    }

    @SuppressWarnings("unchecked")
    public T pop()
    {
        ++this.position;
        return (T) this.elements[this.position - 1];
    }

    public void reset()
    {
        this.position = 0;
    }

    public void clear()
    {
        this.offset = 0;
        this.position = 0;
    }

    public int getOffset()
    {
        return this.offset;
    }

}
