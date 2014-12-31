package com.cafaxo.lynx.math.camera;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.cafaxo.lynx.math.Matrix4x4f;
import com.cafaxo.lynx.math.Vector3f;

public class CameraCubemap
{

    // posX, negX, posY, negY, posZ, negZ
    private Matrix4x4f viewMatrices[] = new Matrix4x4f[6];

    private Matrix4x4f projection = new Matrix4x4f();

    private Matrix4x4f multMatrices[] = new Matrix4x4f[6];

    public CameraCubemap(float fovy, float aspect, float zNear, float zFar)
    {
        float f = (float) (1. / Math.tan(fovy / 2.f));

        this.projection.a11 = f / aspect;
        this.projection.a22 = f;
        this.projection.a33 = (zFar + zNear) / (zNear - zFar);
        this.projection.a43 = (2 * zFar * zNear) / (zNear - zFar);
        this.projection.a34 = -1.f;
    }

    public void setPosition(Vector3f position)
    {
        this.viewMatrices[0] = Matrix4x4f.lookAt(position, position.add(new Vector3f(1.f, 0, 0)), new Vector3f(0, -1.f, 0));
        this.viewMatrices[1] = Matrix4x4f.lookAt(position, position.add(new Vector3f(-1.f, 0, 0)), new Vector3f(0, -1.f, 0));

        this.viewMatrices[2] = Matrix4x4f.lookAt(position, position.add(new Vector3f(0, 1.f, 0)), new Vector3f(0, 0, 1.f));
        this.viewMatrices[3] = Matrix4x4f.lookAt(position, position.add(new Vector3f(0, -1.f, 0)), new Vector3f(0, 0, -1.f));

        this.viewMatrices[4] = Matrix4x4f.lookAt(position, position.add(new Vector3f(0, 0, 1.f)), new Vector3f(0, -1.f, 0));
        this.viewMatrices[5] = Matrix4x4f.lookAt(position, position.add(new Vector3f(0, 0, -1.f)), new Vector3f(0, -1.f, 0));
    }

    public void multiplyWithProjection()
    {
        for (int i = 0; i < this.multMatrices.length; ++i)
        {
            this.multMatrices[i] = Matrix4x4f.multiply(this.viewMatrices[i], this.projection);
        }
    }

    public FloatBuffer getMultBuffer()
    {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16 * 6);

        for (int i = 0; i < 6; ++i)
        {
            fb.put(this.multMatrices[i].getBuffer());
        }

        fb.flip();

        return fb;
    }

}
