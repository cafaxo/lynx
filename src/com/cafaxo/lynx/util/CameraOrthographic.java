package com.cafaxo.lynx.util;

import java.nio.FloatBuffer;

public class CameraOrthographic
{

    private Matrix4x4f projection = new Matrix4x4f();

    private Matrix4x4f view = new Matrix4x4f();

    private Matrix4x4f combined = new Matrix4x4f();

    private boolean isDirty;

    public CameraOrthographic(float left, float right, float bottom, float top)
    {
        this.projection.setIdentity();
        this.view.setIdentity();
        
        this.projection.a11 = 2.f / (right - left);
        this.projection.a22 = 2.f / (top - bottom);
        this.projection.a33 = -1.f;

        this.projection.a41 = -(right + left) / (right - left);
        this.projection.a42 = -(top + bottom) / (top - bottom);

        this.isDirty = true;
    }

    public void setPosition(float x, float y)
    {
        this.view.a41 = x;
        this.view.a42 = y;
        
        this.isDirty = true;
    }

    public void translate(float x, float y)
    {
        this.view.a41 += x;
        this.view.a42 += y;
        
        this.isDirty = true;
    }

    public float getX()
    {
        return this.view.a41;
    }

    public float getY()
    {
        return this.view.a42;
    }

    public FloatBuffer getBuffer()
    {
        if (this.isDirty)
        {
            Matrix4x4f.multiply(this.combined, this.view, this.projection);
            this.isDirty = false;
        }
    
        return this.combined.getBuffer();
    }

}
