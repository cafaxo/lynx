package com.cafaxo.lynx;

public abstract class RenderPass
{

    public enum Type
    {
        STATIC, DYNAMIC, CONFIGURATION;
    }

    protected RenderPassEntity.Type type;

    protected boolean hasChanged;

    public RenderPass()
    {
        this.type = Type.CONFIGURATION;
    }

    public void refresh()
    {
    }

    public void render()
    {
        this.setRenderState();
    }

    public abstract void setRenderState();

    public RenderPass.Type getType()
    {
        return this.type;
    }

    public boolean hasChanged()
    {
        return this.hasChanged;
    }

    public void setHasChanged(boolean hasChanged)
    {
        this.hasChanged = hasChanged;
    }

}
