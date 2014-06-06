package com.cafaxo.lightingdemo3d;

import org.lwjgl.opengl.GL11;

import com.cafaxo.lynx.graphics.ShaderProgram;
import com.cafaxo.lynx.util.ResourceLocation;
import com.cafaxo.lynx.util.ShaderRegistry;

public class LightingPipelineShaders
{

    public static class ModelShaderProgram extends ShaderProgram
    {

        public ModelShaderProgram()
        {
            super(new ResourceLocation("/assets/lightingdemo3d/shader/model.vsh"), new ResourceLocation("/assets/lightingdemo3d/shader/model.fsh"));
        }

        @Override
        public void defineAttributes()
        {
            this.getAttribute("position").define(3, GL11.GL_FLOAT, false, 32, 0);
            this.getAttribute("textureCoords").define(2, GL11.GL_FLOAT, false, 32, 12);
            this.getAttribute("normal").define(3, GL11.GL_FLOAT, false, 32, 20);
        }

    }

    public static void init()
    {
        ShaderRegistry.instance.register("model", new ModelShaderProgram());
    }

}
