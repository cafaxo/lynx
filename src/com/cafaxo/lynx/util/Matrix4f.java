package com.cafaxo.lynx.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4f
{

    private FloatBuffer components;

    public Matrix4f()
    {
        this.components = BufferUtils.createFloatBuffer(16);
        this.setIdentity();
    }

    public void setIdentity()
    {
        this.components.put(0, 1.F);
        this.components.put(1, 0.F);
        this.components.put(2, 0.F);
        this.components.put(3, 0.F);
        this.components.put(4, 0.F);
        this.components.put(5, 1.F);
        this.components.put(6, 0.F);
        this.components.put(7, 0.F);
        this.components.put(8, 0.F);
        this.components.put(9, 0.F);
        this.components.put(10, 1.F);
        this.components.put(11, 0.F);
        this.components.put(12, 0.F);
        this.components.put(13, 0.F);
        this.components.put(14, 0.F);
        this.components.put(15, 1.F);
    }

    public void translate(final float x, final float y, final float z)
    {
        this.components.put(3, this.components.get(3) + x);
        this.components.put(7, this.components.get(7) + y);
        this.components.put(11, this.components.get(11) + z);
    }

    public void translateTo(final float x, final float y, final float z)
    {
        this.components.put(3, x);
        this.components.put(7, y);
        this.components.put(11, z);
    }

    void rotateX(final float angle)
    {
        final float new5 = (float) ((this.components.get(5) * Math.cos(angle)) + (this.components.get(6) * Math.sin(angle)));
        final float new6 = (float) ((this.components.get(5) * -Math.sin(angle)) + (this.components.get(6) * Math.cos(angle)));
        final float new9 = (float) ((this.components.get(9) * Math.cos(angle)) + (this.components.get(10) * Math.sin(angle)));
        final float new10 = (float) ((this.components.get(9) * -Math.sin(angle)) + (this.components.get(10) * Math.cos(angle)));

        this.components.put(5, new5);
        this.components.put(6, new6);
        this.components.put(9, new9);
        this.components.put(10, new10);
    }

    void rotateY(final float angle)
    {
        final float new0 = (float) ((this.components.get(0) * Math.cos(angle)) + (this.components.get(2) * -Math.sin(angle)));
        final float new2 = (float) ((this.components.get(0) * Math.sin(angle)) + (this.components.get(2) * Math.cos(angle)));
        final float new4 = (float) ((this.components.get(4) * Math.cos(angle)) + (this.components.get(6) * -Math.sin(angle)));
        final float new6 = (float) ((this.components.get(4) * Math.sin(angle)) + (this.components.get(6) * Math.cos(angle)));
        final float new8 = (float) ((this.components.get(8) * Math.cos(angle)) + (this.components.get(10) * -Math.sin(angle)));
        final float new10 = (float) ((this.components.get(8) * Math.sin(angle)) + (this.components.get(10) * Math.cos(angle)));

        this.components.put(0, new0);
        this.components.put(2, new2);
        this.components.put(4, new4);
        this.components.put(6, new6);
        this.components.put(8, new8);
        this.components.put(10, new10);
    }

    void rotateZ(final float angle)
    {
        final float new0 = (float) ((this.components.get(0) * Math.cos(angle)) + (this.components.get(1) * Math.sin(angle)));
        final float new1 = (float) ((this.components.get(0) * -Math.sin(angle)) + (this.components.get(1) * Math.cos(angle)));
        final float new4 = (float) ((this.components.get(4) * Math.cos(angle)) + (this.components.get(5) * Math.sin(angle)));
        final float new5 = (float) ((this.components.get(4) * -Math.sin(angle)) + (this.components.get(5) * Math.cos(angle)));

        this.components.put(0, new0);
        this.components.put(1, new1);
        this.components.put(4, new4);
        this.components.put(5, new5);
    }

    public FloatBuffer getComponents()
    {
        return this.components;
    }

    public static void multiply(Matrix4f left, Matrix4f right, Matrix4f dest)
    {
        for (int i = 0; i < 16; ++i)
        {
            dest.components.put(i, (right.components.get((i / 4) * 4) * left.components.get(i % 4)) + (right.components.get(((i / 4) * 4) + 1) * left.components.get((i % 4) + 4)) + (right.components.get(((i / 4) * 4) + 2) * left.components.get((i % 4) + 8)) + (right.components.get(((i / 4) * 4) + 3) * left.components.get((i % 4) + 12)));
        }
    }

}
