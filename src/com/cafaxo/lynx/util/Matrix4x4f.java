package com.cafaxo.lynx.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4x4f
{

    public float a11, a12, a13, a14;

    public float a21, a22, a23, a24;

    public float a31, a32, a33, a34;

    public float a41, a42, a43, a44;

    public FloatBuffer buffer;

    public void setIdentity()
    {
        this.a11 = 1;
        this.a21 = 0;
        this.a31 = 0;
        this.a41 = 0;
        this.a12 = 0;
        this.a22 = 1;
        this.a32 = 0;
        this.a42 = 0;
        this.a13 = 0;
        this.a23 = 0;
        this.a33 = 1;
        this.a43 = 0;
        this.a14 = 0;
        this.a24 = 0;
        this.a34 = 0;
        this.a44 = 1;
    }

    public FloatBuffer getBuffer()
    {
        if (this.buffer == null)
        {
            this.buffer = BufferUtils.createFloatBuffer(16);
        }

        this.buffer.put(this.a11);
        this.buffer.put(this.a12);
        this.buffer.put(this.a13);
        this.buffer.put(this.a14);
        this.buffer.put(this.a21);
        this.buffer.put(this.a22);
        this.buffer.put(this.a23);
        this.buffer.put(this.a24);
        this.buffer.put(this.a31);
        this.buffer.put(this.a32);
        this.buffer.put(this.a33);
        this.buffer.put(this.a34);
        this.buffer.put(this.a41);
        this.buffer.put(this.a42);
        this.buffer.put(this.a43);
        this.buffer.put(this.a44);

        this.buffer.flip();

        return this.buffer;
    }

    public static void multiply(Matrix4x4f c, Matrix4x4f a, Matrix4x4f b)
    {
        c.a11 = (a.a11 * b.a11) + (a.a12 * b.a21) + (a.a13 * b.a31) + (a.a14 * b.a41);
        c.a12 = (a.a11 * b.a12) + (a.a12 * b.a22) + (a.a13 * b.a32) + (a.a14 * b.a42);
        c.a13 = (a.a11 * b.a13) + (a.a12 * b.a23) + (a.a13 * b.a33) + (a.a14 * b.a43);
        c.a14 = (a.a11 * b.a14) + (a.a12 * b.a24) + (a.a13 * b.a34) + (a.a14 * b.a44);
        c.a21 = (a.a21 * b.a11) + (a.a22 * b.a21) + (a.a23 * b.a31) + (a.a24 * b.a41);
        c.a22 = (a.a21 * b.a12) + (a.a22 * b.a22) + (a.a23 * b.a32) + (a.a24 * b.a42);
        c.a23 = (a.a21 * b.a13) + (a.a22 * b.a23) + (a.a23 * b.a33) + (a.a24 * b.a43);
        c.a24 = (a.a21 * b.a14) + (a.a22 * b.a24) + (a.a23 * b.a34) + (a.a24 * b.a44);
        c.a31 = (a.a31 * b.a11) + (a.a32 * b.a21) + (a.a33 * b.a31) + (a.a34 * b.a41);
        c.a32 = (a.a31 * b.a12) + (a.a32 * b.a22) + (a.a33 * b.a32) + (a.a34 * b.a42);
        c.a33 = (a.a31 * b.a13) + (a.a32 * b.a23) + (a.a33 * b.a33) + (a.a34 * b.a43);
        c.a34 = (a.a31 * b.a14) + (a.a32 * b.a24) + (a.a33 * b.a34) + (a.a34 * b.a44);
        c.a41 = (a.a41 * b.a11) + (a.a42 * b.a21) + (a.a43 * b.a31) + (a.a44 * b.a41);
        c.a42 = (a.a41 * b.a12) + (a.a42 * b.a22) + (a.a43 * b.a32) + (a.a44 * b.a42);
        c.a43 = (a.a41 * b.a13) + (a.a42 * b.a23) + (a.a43 * b.a33) + (a.a44 * b.a43);
        c.a44 = (a.a41 * b.a14) + (a.a42 * b.a24) + (a.a43 * b.a34) + (a.a44 * b.a44);
    }

    public static Matrix4x4f getRotationXMatrix(final float angle)
    {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        Matrix4x4f rotationMatrix = new Matrix4x4f();
        rotationMatrix.setIdentity();

        rotationMatrix.a22 = cos;
        rotationMatrix.a32 = -sin;
        rotationMatrix.a23 = sin;
        rotationMatrix.a33 = cos;

        return rotationMatrix;
    }

    public static Matrix4x4f getRotationYMatrix(final float angle)
    {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        Matrix4x4f rotationMatrix = new Matrix4x4f();
        rotationMatrix.setIdentity();

        rotationMatrix.a11 = cos;
        rotationMatrix.a31 = sin;
        rotationMatrix.a13 = -sin;
        rotationMatrix.a33 = cos;

        return rotationMatrix;
    }

    public static Matrix4x4f getRotationZMatrix(final float angle)
    {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        Matrix4x4f rotationMatrix = new Matrix4x4f();
        rotationMatrix.setIdentity();

        rotationMatrix.a11 = cos;
        rotationMatrix.a21 = -sin;
        rotationMatrix.a12 = sin;
        rotationMatrix.a22 = cos;

        return rotationMatrix;
    }

}
