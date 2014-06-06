package com.cafaxo.lightingdemo;

import java.nio.FloatBuffer;

import com.cafaxo.lynx.util.CameraOrthographic;
import com.cafaxo.lynx.util.IDepthContainer;
import com.cafaxo.lynx.util.Vector2f;

public class Light implements IDepthContainer
{

    public static final int SIZE = 11;

    private Vector2f position, transformedPosition;

    protected float radius;

    private float coneStart, coneSize;

    private float r, g, b;

    private float constant, linear, quadratic;

    private int depth;

    private boolean visible;

    public Light()
    {
        this.position = new Vector2f(0.f, 0.f);
        this.transformedPosition = new Vector2f(0.f, 0.f);

        this.coneSize = 1.f;
        this.constant = 1.f;
        this.visible = true;
    }

    public Vector2f getPosition()
    {
        return this.position;
    }

    public void setSize(float radius)
    {
        this.linear = 4.5f / radius;
        this.quadratic = 75.0f / (radius * radius);
        this.radius = radius;
    }

    public float getSize()
    {
        return this.radius;
    }

    public void setIntensity(float intensity)
    {
        this.constant = 1.f / intensity;
    }

    public void setCone(float angle, float size)
    {
        if (angle < 0.f)
        {
            angle += 2.f * (float) Math.PI;
        }

        this.coneStart = (1.f - (angle / (2.f * (float) Math.PI)));
        this.coneSize = size * 0.5f;
    }

    public void setColor(float r, float g, float b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void refreshTransformedPosition(CameraOrthographic camera)
    {
        this.transformedPosition.setPosition(this.position.x + camera.getX(), this.position.y + camera.getY());
    }

    public void writeToFloatBuffer(FloatBuffer floatBuffer)
    {
        floatBuffer.put(this.transformedPosition.x);
        floatBuffer.put(this.transformedPosition.y);
        floatBuffer.put(this.radius);
        floatBuffer.put(this.coneStart);
        floatBuffer.put(this.coneSize);
        floatBuffer.put(this.r);
        floatBuffer.put(this.g);
        floatBuffer.put(this.b);
        floatBuffer.put(this.constant);
        floatBuffer.put(this.linear);
        floatBuffer.put(this.quadratic);
    }

    @Override
    public int getDepth()
    {
        return this.depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

}
