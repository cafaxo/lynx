package com.cafaxo.lynx.test.lighting2d;

import org.lwjgl.opengl.GL11;

import com.cafaxo.lynx.ShaderProgram;
import com.cafaxo.lynx.util.ResourceLocation;
import com.cafaxo.lynx.util.ShaderRegistry;

public class LightingPipelineShaders
{

    public static class SpriteShaderProgram extends ShaderProgram
    {

        public SpriteShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/sprite.vsh"), new ResourceLocation("/assets/lightingdemo/shader/sprite.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 20, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 20, 8);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 20, 16);
        }

    }

    public static class PolygonShaderProgram extends ShaderProgram
    {

        public PolygonShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/polygon.vsh"), new ResourceLocation("/assets/lightingdemo/shader/polygon.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 12, 0);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 12, 8);
        }

    }

    public static class BlurShaderProgram extends ShaderProgram
    {
        public BlurShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/sprite.vsh"), new ResourceLocation("/assets/lightingdemo/shader/blur.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 20, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 20, 8);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 20, 16);
        }

    }

    public static class ShadowMapInfoShaderProgram extends ShaderProgram
    {

        public ShadowMapInfoShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/sprite.vsh"), new ResourceLocation("/assets/lightingdemo/shader/shadowMapInfo.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 20, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 20, 8);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 20, 16);
        }

    }

    public static class ShadowMapShaderProgram extends ShaderProgram
    {

        public ShadowMapShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/sprite.vsh"), new ResourceLocation("/assets/lightingdemo/shader/shadowMap.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 20, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 20, 8);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 20, 16);
        }

    }

    public static class FinalShaderProgram extends ShaderProgram
    {

        public FinalShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/sprite.vsh"), new ResourceLocation("/assets/lightingdemo/shader/final.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 20, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 20, 8);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 20, 16);
        }

    }

    public static class FxaaShaderProgram extends ShaderProgram
    {

        public FxaaShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo/shader/sprite.vsh"), new ResourceLocation("/assets/lightingdemo/shader/fxaa.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(2, GL11.GL_FLOAT, false, 20, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 20, 8);
            this.getAttribute("color").define(4, GL11.GL_UNSIGNED_BYTE, true, 20, 16);
        }

    }

    public static void init()
    {
        ShaderRegistry.instance.register("sprite", new SpriteShaderProgram());
        ShaderRegistry.instance.register("polygon", new PolygonShaderProgram());
        ShaderRegistry.instance.register("blur", new BlurShaderProgram());
        ShaderRegistry.instance.register("shadowMapInfo", new ShadowMapInfoShaderProgram());
        ShaderRegistry.instance.register("shadowMap", new ShadowMapShaderProgram());
        ShaderRegistry.instance.register("final", new FinalShaderProgram());
        ShaderRegistry.instance.register("fxaa", new FxaaShaderProgram());
    }

}
