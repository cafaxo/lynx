package com.cafaxo.lynx.util;

public class PackerNode
{
    public int x, y, width, height;

    public boolean used;

    public PackerNode right, down;

    public PackerNode(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
