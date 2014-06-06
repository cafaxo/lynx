package com.cafaxo.lightingdemo;

import java.awt.Font;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.cafaxo.lynx.graphics.FontCache;
import com.cafaxo.lynx.graphics.Polygon;
import com.cafaxo.lynx.graphics.RenderManager;
import com.cafaxo.lynx.graphics.RenderPass;
import com.cafaxo.lynx.graphics.RenderPassEntity;
import com.cafaxo.lynx.graphics.ShaderProgram;
import com.cafaxo.lynx.graphics.Sprite;
import com.cafaxo.lynx.graphics.Text;
import com.cafaxo.lynx.graphics.TextureSheet;
import com.cafaxo.lynx.graphics.TextureSheetNode;
import com.cafaxo.lynx.util.ResourceLocation;
import com.cafaxo.lynx.util.ShaderRegistry;

public class LightingDemo
{

    private LightingPipeline lightingPipeline;

    private Sprite sprite;

    private RenderManager renderManager;

    private RenderPassEntity diffusePass, occlusionPass;

    private RenderPassEntity uiPass;

    private Polygon polygon;

    public LightingDemo()
    {
        try
        {
            Display.setTitle("lightingdemo");
            Display.setDisplayMode(new DisplayMode(720, 480));
            Display.setVSyncEnabled(true);

            Display.create();
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();
        }

        LightingPipelineShaders.init();

        this.renderManager = new RenderManager(10000 * 4, 10000 * 6);

        this.lightingPipeline = new LightingPipeline(this.renderManager, 720, 480, true);

        this.diffusePass = new RenderPassEntity(this.renderManager, RenderPass.Type.DYNAMIC)
        {

            @Override
            public void setRenderState()
            {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            @Override
            public void setPostRenderState()
            {
                GL11.glDisable(GL11.GL_BLEND);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingDemo.this.lightingPipeline.getCamera().getBuffer());
            }

        };

        this.occlusionPass = new RenderPassEntity(this.renderManager, RenderPass.Type.DYNAMIC)
        {

            @Override
            public void setRenderState()
            {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            @Override
            public void setPostRenderState()
            {
                GL11.glDisable(GL11.GL_BLEND);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingDemo.this.lightingPipeline.getCamera().getBuffer());
            }

        };

        this.uiPass = new RenderPassEntity(this.renderManager, RenderPass.Type.DYNAMIC)
        {

            @Override
            public void setRenderState()
            {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            @Override
            public void setPostRenderState()
            {
                GL11.glDisable(GL11.GL_BLEND);
            }

            @Override
            public void setUniforms(ShaderProgram shaderProgram)
            {
                GL20.glUniformMatrix4(shaderProgram.getUniform("camera"), false, LightingDemo.this.lightingPipeline.getCamera().getBuffer());
            }

        };

        ResourceLocation terrainLocation = new ResourceLocation("/assets/lightingdemo/texture/terrain.png");

        this.polygon = Polygon.fromRectangle(ShaderRegistry.instance.get("polygon"), 720, 480, 1.f, 1.f, 1.f, 1.f);
        this.polygon.setPosition(0, 0);
        this.polygon.setDepth(0);

        TextureSheet textureSheet = new TextureSheet();
        TextureSheetNode terrainNode = textureSheet.add(terrainLocation);

        FontCache fontcache = new FontCache(textureSheet, new Font("Times New Roman", Font.BOLD, 24));
        textureSheet.create();

        Text text = new Text(fontcache, 15, 8);
        text.setText("hello world");
        text.setPosition((720 - text.getWidth()) / 2, 390);
        text.setDepth(2);
        text.setColor(0.4f, 0.5f, 0.6f, 1.f);

        Text uiText = new Text(fontcache, 15, 8);
        uiText.setText("hello world");
        uiText.setPosition((720 - uiText.getWidth()) / 2, 425);
        uiText.setDepth(2);
        uiText.setColor(0.4f, 0.5f, 0.6f, 1.f);

        this.uiPass.addEntity(uiText);

        this.sprite = new Sprite(ShaderRegistry.instance.get("sprite"), textureSheet, terrainNode.textureRegion);
        this.sprite.setDepth(1);
        this.sprite.setPosition(0, 0);

        this.diffusePass.addEntity(this.sprite);

        this.diffusePass.addEntity(text);
        this.diffusePass.addEntity(this.polygon);

        this.occlusionPass.addEntity(this.sprite);
        this.occlusionPass.addEntity(text);

        this.lightingPipeline.getDiffusePasses().add(this.diffusePass);

        this.lightingPipeline.getOcclusionPasses().add(this.occlusionPass);

        this.run();
    }

    public long getTime()
    {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public void run()
    {
        int frames = 0;
        long start = this.getTime();

        while (!Display.isCloseRequested())
        {
            frames++;

            this.tick();
            this.render();

            Display.sync(60);

            long now = this.getTime();

            if ((now - start) >= 1000)
            {
                System.out.println("fps: " + frames);
                frames = 0;
                start = now;
            }
        }

        Display.destroy();
    }

    public void addLight(float x, float y)
    {
        Light light = new Light();

        light.getPosition().setPosition(x, y);
        light.setColor(1f - ((float) Math.random() * 0.4f), 1f - ((float) Math.random() * 0.4f), 1f - ((float) Math.random() * 0.4f));
        light.setSize(400.f);
        light.setIntensity(1.5f);

        this.lightingPipeline.getLights().add(light);
    }

    private void render()
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        while (Mouse.next())
        {
            if (Mouse.getEventButtonState() && (Mouse.getEventButton() == 0))
            {
                int x = Mouse.getEventX();
                int y = Mouse.getEventY();

                this.addLight(x - this.lightingPipeline.getCamera().getX(), y - this.lightingPipeline.getCamera().getY());
            }
        }

        this.lightingPipeline.getCamera().translate(1f, 0);

        this.polygon.setPosition(-this.lightingPipeline.getCamera().getX(), -this.lightingPipeline.getCamera().getY());

        this.lightingPipeline.getAmbientLightColor().set(0.15f, 0.15f, 0.25f, 1.f);

        this.lightingPipeline.getViewSource().setPosition(Mouse.getX(), Mouse.getY());

        this.renderManager.begin();

        this.lightingPipeline.render(this.renderManager);

        this.renderManager.render(this.uiPass);

        this.renderManager.end();

        Display.update();
    }

    private void tick()
    {
    }

    public static void main(String[] args)
    {
        new LightingDemo();
    }

}
