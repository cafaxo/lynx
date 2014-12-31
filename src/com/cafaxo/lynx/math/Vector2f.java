package com.cafaxo.lynx.math;

public class Vector2f
{

    public float x, y;

    public Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void translate(float x, float y)
    {
        this.x += x;
        this.y += y;
    }

    public Vector2f normalize()
    {
        float length = (float) Math.sqrt((this.x * this.x) + (this.y * this.y));

        if (length != 0)
        {
            return new Vector2f(this.x / length, this.y / length);
        }

        return null;
    }

    public void scale(float factor)
    {
        this.x *= factor;
        this.y *= factor;
    }

    public static Vector2f add(Vector2f first, Vector2f second)
    {
        return new Vector2f(first.x + second.x, first.y + second.y);
    }

    public static Vector2f subtract(Vector2f first, Vector2f second)
    {
        return new Vector2f(second.x - first.x, second.y - first.y);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof Vector2f)
        {
            if ((((Vector2f) obj).x == this.x) && (((Vector2f) obj).y == this.y))
            {
                return true;
            }
        }

        return false;
    }

    public float length()
    {
        return (float) Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

}
