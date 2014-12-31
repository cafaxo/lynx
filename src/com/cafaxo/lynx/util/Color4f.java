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

    public float pack()
    {
        int intBits = ((int) (255 * this.a) << 24) | ((int) (255 * this.b) << 16) | ((int) (255 * this.g) << 8) | ((int) (255 * this.r));
        return Float.intBitsToFloat(intBits & 0xfeffffff);
    }

}
