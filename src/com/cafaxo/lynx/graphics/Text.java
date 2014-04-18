package com.cafaxo.lynx.graphics;

import com.cafaxo.lynx.util.ShaderRegistry;
import com.cafaxo.lynx.util.Vector2f;

public class Text extends Vector2f
{

    private FontCache fontCache;

    private String text;

    private Sprite sprites[];

    private int maxLength, offsetForIllegalChars;

    private int width;

    public Text(FontCache fontCache, int maxLength, int offsetForIllegalChars)
    {
        super(0, 0);

        this.fontCache = fontCache;
        this.maxLength = maxLength;
        this.offsetForIllegalChars = offsetForIllegalChars;
        this.sprites = new Sprite[maxLength];

        for (int i = 0; i < maxLength; ++i)
        {
            this.sprites[i] = new Sprite(fontCache.getTextureSheet());
            this.sprites[i].setShaderProgram(ShaderRegistry.instance.get("sprite"));
        }
    }

    public void setText(String text)
    {
        if (text.length() > this.maxLength)
        {
            throw new RuntimeException("text size exceeds specified limit");
        }

        this.text = text;

        for (int i = 0; i < this.sprites.length; ++i)
        {
            if (i < text.length())
            {
                TextureSheetNode node = this.fontCache.getTextureSheetNode(text.charAt(i));

                if (node != null)
                {
                    this.sprites[i].setTextureRegion(node.textureRegion);
                    this.sprites[i].setSize(node.textureRegion.width, node.textureRegion.height);
                    this.sprites[i].setVisible(true);
                }
                else
                {
                    this.sprites[i].setVisible(false);
                }
            }
            else
            {
                this.sprites[i].setVisible(false);
            }
        }

        this.rebuildVertices();
    }

    public void rebuildVertices()
    {
        float xOffset = this.x;

        for (int i = 0; i < this.text.length(); ++i)
        {
            if (!this.sprites[i].isVisible())
            {
                xOffset += this.offsetForIllegalChars;
            }
            else
            {
                this.sprites[i].setPosition(xOffset, this.y);
                xOffset += this.sprites[i].getTextureRegion().width;
            }
        }

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
        for (Sprite sprite : this.sprites)
        {
            sprite.setColor(r, g, b, a);
        }
    }

    public void setDepth(int depth)
    {
        for (Sprite sprite : this.sprites)
        {
            sprite.setDepth(depth);
        }
    }

    public Sprite[] getSprites()
    {
        return this.sprites;
    }

    public int getWidth()
    {
        return this.width;
    }

}
