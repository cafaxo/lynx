package com.cafaxo.lynx.render;

import com.cafaxo.lynx.math.Vector2f;
import com.cafaxo.lynx.render.texture.TextureSheetNode;
import com.cafaxo.lynx.util.Color4f;
import com.cafaxo.lynx.util.FontCache;

public class MeshText extends Mesh
{

    private FontCache fontCache;

    private String text;

    private Vector2f position = new Vector2f(0.f, 0.f);

    private int maxLength, offsetForIllegalChars;

    private int width;

    private Color4f color = new Color4f(1.f, 1.f, 1.f, 1.f);

    public MeshText(FontCache fontCache, int maxLength, int offsetForIllegalChars)
    {
        super(maxLength * 20, maxLength * 6);

        this.fontCache = fontCache;
        this.maxLength = maxLength;
        this.offsetForIllegalChars = offsetForIllegalChars;
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
        this.vertexData.clear();
        this.indexData.clear();

        float xOffset = this.position.x;
        MeshSprite tmp = new MeshSprite();
        int indexDataOffset = 0;

        for (int i = 0; i < this.text.length(); ++i)
        {
            TextureSheetNode node = this.fontCache.getTextureSheetNode(this.text.charAt(i));

            if (node != null)
            {
                tmp.setTextureRegion(node.textureRegion);
                tmp.setSize(node.textureRegion.width, node.textureRegion.height);
                tmp.setPosition(xOffset, this.position.y);
                tmp.setColor(this.color.r, this.color.g, this.color.b, this.color.a);

                xOffset += tmp.getTextureRegion().width;

                this.vertexData.put(tmp.vertexData);

                for (int j = 0; j < tmp.indexData.limit(); ++j)
                {
                    this.addIndexData(tmp.indexData.get(i) + indexDataOffset);
                }

                indexDataOffset += 4;
            }
            else
            {
                xOffset += this.offsetForIllegalChars;
            }
        }

        this.width = (int) (xOffset - this.position.x);
    }

    public int getWidth()
    {
        return this.width;
    }

}
