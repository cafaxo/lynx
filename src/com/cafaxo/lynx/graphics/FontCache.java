package com.cafaxo.lynx.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class FontCache
{

    private HashMap<Character, TextureSheetNode> characterToLocationMap = new HashMap<Character, TextureSheetNode>();

    private TextureSheet textureSheet;

    public FontCache(TextureSheet textureSheet, Font font)
    {
        this.textureSheet = textureSheet;

        BufferedImage fontImage = new BufferedImage(8048, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gt = (Graphics2D) fontImage.getGraphics();

        gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gt.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gt.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gt.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        gt.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        gt.setFont(font);
        gt.setColor(Color.WHITE);

        int xOffset = 0;

        for (char chr = 33; chr < 128; ++chr)
        {
            gt.drawString(String.valueOf(chr), xOffset, gt.getFontMetrics().getAscent());
            Rectangle2D rect = gt.getFontMetrics().getStringBounds(String.valueOf(chr), gt);

            Image image = new Image(null, fontImage, xOffset, 0, (int) rect.getWidth(), (int) rect.getHeight());
            this.characterToLocationMap.put(chr, textureSheet.add(image));

            xOffset += rect.getWidth() + 5;
        }
    }

    public TextureSheetNode getTextureSheetNode(char chr)
    {
        return this.characterToLocationMap.get(chr);
    }

    public TextureSheet getTextureSheet()
    {
        return this.textureSheet;
    }

}
