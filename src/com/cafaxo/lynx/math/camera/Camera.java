package com.cafaxo.lynx.math.camera;

import com.cafaxo.lynx.math.Matrix4x4f;

public class Camera
{

    protected Matrix4x4f projection = new Matrix4x4f();

    protected Matrix4x4f view = new Matrix4x4f();

    public Matrix4x4f getProjection()
    {
        return this.projection;
    }

    public Matrix4x4f getView()
    {
        return this.view;
    }

}
