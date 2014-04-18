package com.cafaxo.lynx.util;

import java.util.ArrayList;

public class SortedPool<T extends Comparable<T>>
{

    private T[] buffer;

    private int size;

    public SortedPool(T[] buffer)
    {
        this.buffer = buffer;
    }

    public void add(T element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("\"element\" can't be null");
        }

        if (this.size == this.buffer.length)
        {
            throw new RuntimeException("pool overflow");
        }

        if (this.size == 0)
        {
            this.buffer[0] = element;
            ++this.size;

            return;
        }

        if (element.compareTo(this.buffer[this.size - 1]) != -1)
        {
            this.buffer[this.size] = element;
            ++this.size;

            return;
        }

        for (int i = 0; i < this.size; ++i)
        {
            if (element.compareTo(this.buffer[i]) == -1)
            {
                for (int j = this.size; j > i; --j)
                {
                    this.buffer[j] = this.buffer[j - 1];
                }

                this.buffer[i] = element;
                ++this.size;

                return;
            }
        }
    }

    public void add(T[] elements)
    {
        for (T element : elements)
        {
            this.add(element);
        }
    }

    public void add(ArrayList<? extends T> elements)
    {
        for (T element : elements)
        {
            this.add(element);
        }
    }

    public void remove(T element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("\"element\" can't be null");
        }

        boolean found = false;

        for (int i = 0; i < this.size; ++i)
        {
            if (!found)
            {
                if (element.equals(this.buffer[i]))
                {
                    this.buffer[i] = this.buffer[i + 1];
                    found = true;
                }
            }
            else
            {
                this.buffer[i] = this.buffer[i + 1];
            }
        }

        if (found)
        {
            --this.size;
        }
    }

    public T get(int index)
    {
        return this.buffer[index];
    }

    public T[] getBuffer()
    {
        return this.buffer;
    }

    public int getSize()
    {
        return this.size;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder(this.size);

        for (int i = 0; i < (this.size - 1); ++i)
        {
            stringBuilder.append(this.buffer[i].toString() + ", ");
        }

        stringBuilder.append(this.buffer[this.size - 1].toString());

        return stringBuilder.toString();
    }

}
