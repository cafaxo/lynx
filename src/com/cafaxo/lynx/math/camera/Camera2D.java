package com.cafaxo.lynx.math.camera;

public class Camera2D extends Camera
{

    public Camera2D(float left, float right, float bottom, float top)
    {
        this.projection.setIdentity();
        this.view.setIdentity();

        this.projection.a11 = 2.f / (right - left);
        this.projection.a22 = 2.f / (top - bottom);
        this.projection.a33 = -1.f;

        this.projection.a41 = -(right + left) / (right - left);
        this.projection.a42 = -(top + bottom) / (top - bottom);
    }

    public void setPosition(float x, float y)
    {
        this.view.a41 = x;
        this.view.a42 = y;
    }

    public void translate(float x, float y)
    {
        this.view.a41 += x;
        this.view.a42 += y;
    }

    public float getX()
    {
        return this.view.a41;
    }

    public float getY()
    {
        return this.view.a42;
    }

}
