package com.cafaxo.lynx.graphics;

import java.nio.FloatBuffer;

import com.cafaxo.lynx.util.Matrix4f;

public class Camera
{

    protected Matrix4f projection = new Matrix4f();

    protected Matrix4f transformation = new Matrix4f();

    protected Matrix4f combined = new Matrix4f();

    protected boolean isDirty;

    public FloatBuffer getFloatBuffer()
    {
        if (this.isDirty)
        {
            Matrix4f.multiply(this.projection, this.transformation, this.combined);
            this.isDirty = false;
        }

        return this.combined.getComponents();
    }
}
