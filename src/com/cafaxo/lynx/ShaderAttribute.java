package com.cafaxo.lynx;

public class ShaderAttribute
{

    private String name;

    private int location;

    private int components;

    private int type;

    private boolean normalized;

    private int stride;

    private int bufferOffset;

    private boolean defined;

    public ShaderAttribute(String name, int location)
    {
        this.name = name;
        this.location = location;
    }

    public void define(int components, int type, boolean normalized, int stride, int bufferOffset)
    {
        this.components = components;
        this.type = type;
        this.normalized = normalized;
        this.stride = stride;
        this.bufferOffset = bufferOffset;

        this.defined = true;
    }

    public String getName()
    {
        return this.name;
    }

    public int getLocation()
    {
        return this.location;
    }

    public int getComponents()
    {
        return this.components;
    }

    public int getType()
    {
        return this.type;
    }

    public boolean isNormalized()
    {
        return this.normalized;
    }

    public int getStride()
    {
        return this.stride;
    }

    public int getBufferOffset()
    {
        return this.bufferOffset;
    }

    public boolean isDefined()
    {
        return this.defined;
    }

}
