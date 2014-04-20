package com.cafaxo.lynx.graphics;

import com.cafaxo.lynx.util.Color4f;
import com.cafaxo.lynx.util.ShaderRegistry;

public class Text extends RenderEntity
{

    private FontCache fontCache;

    private String text;

    private int maxLength, offsetForIllegalChars;

    private int width;

    private Color4f color = new Color4f(1.f, 1.f, 1.f, 1.f);

    public Text(FontCache fontCache, int maxLength, int offsetForIllegalChars)
    {
        super(ShaderRegistry.instance.get("sprite"), maxLength * 20, maxLength * 6);

        this.fontCache = fontCache;
        this.maxLength = maxLength;
        this.offsetForIllegalChars = offsetForIllegalChars;

        this.setTexture(this.fontCache.getTextureSheet());
    }

    public void setText(String text)
    {
        if (text.length() > this.maxLength)
        {
            throw new RuntimeException("text size exceeds specified limit");
        }

        this.text = text;

        this.rebuildVertices();
    }

    public void rebuildVertices()
    {
        this.resetVertexAndIndexData();

        float xOffset = this.x;
        Sprite tmp = new Sprite(null);
        int indexDataOffset = 0;

        for (int i = 0; i < this.text.length(); ++i)
        {
            TextureSheetNode node = this.fontCache.getTextureSheetNode(this.text.charAt(i));

            if (node != null)
            {
                tmp.setTextureRegion(node.textureRegion);
                tmp.setSize(node.textureRegion.width, node.textureRegion.height);
                tmp.setPosition(xOffset, this.y);
                tmp.setColor(this.color.r, this.color.g, this.color.b, this.color.a);

                xOffset += tmp.getTextureRegion().width;

                this.addVertexData(tmp.getVertexData());

                for (int indexData : tmp.getIndexData())
                {
                    this.addIndexData(indexDataOffset + indexData);
                }

                indexDataOffset += 4;
            }
            else
            {
                xOffset += this.offsetForIllegalChars;
            }
        }

        this.refreshVertexAndIndexData();

        this.width = (int) (xOffset - this.x);
    }

    @Override
    public void translate(float x, float y)
    {
        super.translate(x, y);

        this.rebuildVertices();
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);

        this.rebuildVertices();
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.color.r = r;
        this.color.g = g;
        this.color.b = b;
        this.color.a = a;

        this.rebuildVertices();
    }

    public int getWidth()
    {
        return this.width;
    }

}
