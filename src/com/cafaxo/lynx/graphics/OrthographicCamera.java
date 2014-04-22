package com.cafaxo.lynx.graphics;

public class OrthographicCamera extends Camera
{

    public OrthographicCamera(float left, float right, float bottom, float top)
    {
        this.projection.getComponents().put(0, 2.f / (right - left));
        this.projection.getComponents().put(5, 2.f / (top - bottom));
        this.projection.getComponents().put(10, -1.F);

        this.projection.getComponents().put(3, -(right + left) / (right - left));
        this.projection.getComponents().put(7, -(top + bottom) / (top - bottom));

        this.isDirty = true;
    }

    public void setPosition(float x, float y)
    {
        this.transformation.translateTo(x, y, 0);
        this.isDirty = true;
    }

    public void translate(float x, float y)
    {
        this.transformation.translate(x, y, 0);
        this.isDirty = true;
    }

    public float getX()
    {
        return this.transformation.getComponents().get(3);
    }

    public float getY()
    {
        return this.transformation.getComponents().get(7);
    }

}
