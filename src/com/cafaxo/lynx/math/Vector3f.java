package com.cafaxo.lynx.math;

public class Vector3f
{
    public float x, y, z;

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void translate(float x, float y, float z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void normalize()
    {
        float length = (float) Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));

        if (length != 0)
        {
            this.x /= length;
            this.y /= length;
            this.z /= length;
        }
        else
        {
            //throw new RuntimeException();
        }
    }

    public void scale(float factor)
    {
        this.x *= factor;
        this.y *= factor;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof Vector3f)
        {
            if ((((Vector3f) obj).x == this.x) && (((Vector3f) obj).y == this.y) && (((Vector3f) obj).z == this.z))
            {
                return true;
            }
        }

        return false;
    }

    public void rotateY(float angle)
    {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        float newX = (this.x * cos) - (this.z * sin);
        float newZ = (this.x * sin) + (this.z * cos);

        this.x = newX;
        this.z = newZ;
    }

    public static Vector3f cross(Vector3f v, Vector3f w)
    {
        Vector3f u = new Vector3f(0.f, 0.f, 0.f);

        u.x = (v.y * w.z) - (v.z * w.y);
        u.y = (v.z * w.x) - (v.x * w.z);
        u.z = (v.x * w.y) - (v.y * w.x);

        return u;
    }

    public static float dotProduct(Vector3f v, Vector3f w)
    {
        return (v.x * w.x) + (v.y * w.y) + (v.z * w.z);
    }

    @Override
    public String toString()
    {
        return "(" + (String.format("%.02f", this.x)) + ", " + (String.format("%.02f", this.y)) + ", " + (String.format("%.02f", this.z)) + ")";
    }

    public Vector3f sub(Vector3f other)
    {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3f add(Vector3f other)
    {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public float length()
    {
        return (float) Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }
}
