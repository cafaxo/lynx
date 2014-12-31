package com.cafaxo.lynx.render.texture;

public class TextureRegion
{

    public final static TextureRegion WHOLE = new TextureRegion();

    static
    {
        WHOLE.u1 = 0.f;
        WHOLE.v1 = 0.f;

        WHOLE.u2 = 1.f;
        WHOLE.v2 = 0.f;

        WHOLE.u3 = 1.f;
        WHOLE.v3 = 1.f;

        WHOLE.u4 = 0.f;
        WHOLE.v4 = 1.f;

        WHOLE.width = 1.f;
        WHOLE.height = 1.f;
    }

    public float u1, v1, u2, v2, u3, v3, u4, v4;

    public float width, height;
}
