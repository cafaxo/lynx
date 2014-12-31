package com.cafaxo.lynx.render.texture;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.cafaxo.lynx.util.ResourceLocation;

public class TextureSheet extends Texture
{

    private ArrayList<TextureSheetNode> nodes = new ArrayList<TextureSheetNode>();

    public TextureSheet()
    {
        super(2048, 2048);
    }

    public TextureSheetNode add(ResourceLocation location)
    {
        return this.add(new Image(location));
    }

    public TextureSheetNode add(Image image)
    {
        TextureSheetNode imageContainer = new TextureSheetNode(image);
        this.nodes.add(imageContainer);

        return imageContainer;
    }

    public void create()
    {
        this.upload(); // bind texture and allocate memory

        Packer packer = new Packer(2048, 2048);
        packer.fit(this.nodes);

        for (TextureSheetNode imageContainer : this.nodes)
        {
            if (imageContainer.node != null)
            {
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, imageContainer.node.x, imageContainer.node.y, imageContainer.image.width, imageContainer.image.height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageContainer.image.imageData);
                imageContainer.textureRegion = this.getTextureRegion(imageContainer.node.x, imageContainer.node.y, imageContainer.image.width, imageContainer.image.height);
            }
        }
    }

}
