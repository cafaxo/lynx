package com.cafaxo.lynx.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DepthSorter<T extends IDepthContainer>
{

    private class Itr implements Iterator<T>
    {

        private int counter = 0;

        private int currentLayer = 0, currentElement = -1;

        @Override
        public boolean hasNext()
        {
            return this.counter != DepthSorter.this.size;
        }

        @Override
        public T next()
        {
            if (this.hasNext())
            {
                if ((this.currentElement + 1) < DepthSorter.this.layers.get(this.currentLayer).size())
                {
                    this.currentElement++;

                    this.counter++;
                    return DepthSorter.this.layers.get(this.currentLayer).get(this.currentElement);
                }

                for (int i = this.currentLayer + 1; i < DepthSorter.this.layers.size(); ++i)
                {
                    if (DepthSorter.this.layers.get(i).size() > 0)
                    {
                        this.currentLayer = i;
                        this.currentElement = 0;

                        this.counter++;
                        return DepthSorter.this.layers.get(this.currentLayer).get(this.currentElement);
                    }
                }
            }

            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }

    private int size;

    private ArrayList<ArrayList<T>> layers = new ArrayList<ArrayList<T>>();

    public void addBulkIntoLayer(int depth, ArrayList<T> depthContainers)
    {
        if (depth < 0)
        {
            throw new RuntimeException("layers must have a positive index");
        }

        for (int i = this.layers.size(); (i - 1) <= depth; ++i)
        {
            this.layers.add(new ArrayList<T>());
        }

        this.layers.get(depth).addAll(depthContainers);
        this.size += depthContainers.size();
    }

    public void add(T depthContainer)
    {
        if (depthContainer.getDepth() < 0)
        {
            throw new RuntimeException("layers must have a positive index");
        }

        for (int i = this.layers.size(); (i - 1) <= depthContainer.getDepth(); ++i)
        {
            this.layers.add(new ArrayList<T>());
        }

        this.layers.get(depthContainer.getDepth()).add(depthContainer);
        this.size++;
    }

    public boolean remove(T depthContainer)
    {
        if (depthContainer.getDepth() < this.layers.size())
        {
            ArrayList<T> layer = this.layers.get(depthContainer.getDepth());
            int index = layer.indexOf(depthContainer);

            if (index > -1)
            {
                layer.remove(index);
                this.size--;
            }
        }

        return false;
    }

    public Iterator<T> iterator()
    {
        return new Itr();
    }

}
