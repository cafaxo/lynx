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

    public void setPosition(int x, int y)
    {
        this.transformation.translateTo(x, y, 0);
        this.isDirty = true;
    }

    public void translate(int x, int y)
    {
        this.transformation.translate(x, y, 0);
        this.isDirty = true;
    }

    public int getX()
    {
        return (int) this.transformation.getComponents().get(3);
    }

    public int getY()
    {
        return (int) this.transformation.getComponents().get(7);
    }

}
