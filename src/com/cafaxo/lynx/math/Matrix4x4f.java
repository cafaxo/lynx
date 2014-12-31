package com.cafaxo.lynx.math;

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
        else
        {
            this.buffer.clear();
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

    public Matrix4x4f getInverse()
    {
        Matrix4x4f m = new Matrix4x4f();

        float s0 = (this.a11 * this.a22) - (this.a21 * this.a12);
        float s1 = (this.a11 * this.a23) - (this.a21 * this.a13);
        float s2 = (this.a11 * this.a24) - (this.a21 * this.a14);
        float s3 = (this.a12 * this.a23) - (this.a22 * this.a13);
        float s4 = (this.a12 * this.a24) - (this.a22 * this.a14);
        float s5 = (this.a13 * this.a24) - (this.a23 * this.a14);

        float c5 = (this.a33 * this.a44) - (this.a43 * this.a34);
        float c4 = (this.a32 * this.a44) - (this.a42 * this.a34);
        float c3 = (this.a32 * this.a43) - (this.a42 * this.a33);
        float c2 = (this.a31 * this.a44) - (this.a41 * this.a34);
        float c1 = (this.a31 * this.a43) - (this.a41 * this.a33);
        float c0 = (this.a31 * this.a42) - (this.a41 * this.a32);

        // TODO: should check for 0 determinant

        float invdet = 1 / (((((s0 * c5) - (s1 * c4)) + (s2 * c3) + (s3 * c2)) - (s4 * c1)) + (s5 * c0));

        m.a11 = (((this.a22 * c5) - (this.a23 * c4)) + (this.a24 * c3)) * invdet;
        m.a12 = (((-this.a12 * c5) + (this.a13 * c4)) - (this.a14 * c3)) * invdet;
        m.a13 = (((this.a42 * s5) - (this.a43 * s4)) + (this.a44 * s3)) * invdet;
        m.a14 = (((-this.a32 * s5) + (this.a33 * s4)) - (this.a34 * s3)) * invdet;

        m.a21 = (((-this.a21 * c5) + (this.a23 * c2)) - (this.a24 * c1)) * invdet;
        m.a22 = (((this.a11 * c5) - (this.a13 * c2)) + (this.a14 * c1)) * invdet;
        m.a23 = (((-this.a41 * s5) + (this.a43 * s2)) - (this.a44 * s1)) * invdet;
        m.a24 = (((this.a31 * s5) - (this.a33 * s2)) + (this.a34 * s1)) * invdet;

        m.a31 = (((this.a21 * c4) - (this.a22 * c2)) + (this.a24 * c0)) * invdet;
        m.a32 = (((-this.a11 * c4) + (this.a12 * c2)) - (this.a14 * c0)) * invdet;
        m.a33 = (((this.a41 * s4) - (this.a42 * s2)) + (this.a44 * s0)) * invdet;
        m.a34 = (((-this.a31 * s4) + (this.a32 * s2)) - (this.a34 * s0)) * invdet;

        m.a41 = (((-this.a21 * c3) + (this.a22 * c1)) - (this.a23 * c0)) * invdet;
        m.a42 = (((this.a11 * c3) - (this.a12 * c1)) + (this.a13 * c0)) * invdet;
        m.a43 = (((-this.a41 * s3) + (this.a42 * s1)) - (this.a43 * s0)) * invdet;
        m.a44 = (((this.a31 * s3) - (this.a32 * s1)) + (this.a33 * s0)) * invdet;

        return m;
    }

    public static Matrix4x4f multiply(Matrix4x4f a, Matrix4x4f b)
    {
        Matrix4x4f c = new Matrix4x4f();

        Matrix4x4f.multiply(c, a, b);

        return c;
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

    public static Matrix4x4f fromArray(float[] array)
    {
        Matrix4x4f mat = new Matrix4x4f();

        mat.a11 = array[0];
        mat.a12 = array[1];
        mat.a13 = array[2];
        mat.a14 = array[3];
        mat.a21 = array[4];
        mat.a22 = array[5];
        mat.a23 = array[6];
        mat.a24 = array[7];
        mat.a31 = array[8];
        mat.a32 = array[9];
        mat.a33 = array[10];
        mat.a34 = array[11];
        mat.a41 = array[12];
        mat.a42 = array[13];
        mat.a43 = array[14];
        mat.a44 = array[15];

        return mat;
    }

    public static Matrix4x4f fromArrayTransposed(float[] array)
    {
        Matrix4x4f mat = new Matrix4x4f();

        mat.a11 = array[0];
        mat.a21 = array[1];
        mat.a31 = array[2];
        mat.a41 = array[3];
        mat.a12 = array[4];
        mat.a22 = array[5];
        mat.a32 = array[6];
        mat.a42 = array[7];
        mat.a13 = array[8];
        mat.a23 = array[9];
        mat.a33 = array[10];
        mat.a43 = array[11];
        mat.a14 = array[12];
        mat.a24 = array[13];
        mat.a34 = array[14];
        mat.a44 = array[15];

        return mat;
    }

    public static Matrix4x4f lookAt(Vector3f eye, Vector3f center, Vector3f up)
    {
        Matrix4x4f mat = new Matrix4x4f();
        mat.setIdentity();

        Vector3f f = center.sub(eye);
        f.normalize();

        Vector3f s = Vector3f.cross(f, up);
        s.normalize();

        Vector3f u = Vector3f.cross(s, f);

        mat.a11 = s.x;
        mat.a21 = s.y;
        mat.a31 = s.z;

        mat.a12 = u.x;
        mat.a22 = u.y;
        mat.a32 = u.z;

        mat.a13 = -f.x;
        mat.a23 = -f.y;
        mat.a33 = -f.z;

        mat.a41 = -Vector3f.dotProduct(s, eye);
        mat.a42 = -Vector3f.dotProduct(u, eye);
        mat.a43 = Vector3f.dotProduct(f, eye);

        return mat;
    }

    public static Matrix4x4f getTranslation(Vector3f position)
    {
        Matrix4x4f mat = new Matrix4x4f();
        mat.setIdentity();

        mat.a41 = position.x;
        mat.a42 = position.y;
        mat.a43 = position.z;

        return mat;
    }

}
