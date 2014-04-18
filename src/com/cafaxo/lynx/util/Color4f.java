package com.cafaxo.lynx.util;

public class Color4f
{

    public float r;

    public float g;

    public float b;

    public float a;

    public Color4f(float r, float g, float b, float a)
    {
        this.set(r, g, b, a);
    }

    public void set(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

}
