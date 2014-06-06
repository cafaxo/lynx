package com.cafaxo.lightingdemo3d;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.cafaxo.lynx.graphics.RenderManager;
import com.cafaxo.lynx.graphics.Texture;
import com.cafaxo.lynx.util.ResourceLocation;
import com.cafaxo.lynx.util.ShaderRegistry;

public class LightingDemo
{

    private LightingPipeline lightingPipeline;

    private RenderManager renderManager;

    private Model modelEntity;

    public LightingDemo()
    {
        try
        {
            Display.setTitle("lightingdemo");
            Display.setDisplayMode(new DisplayMode(720, 480));
            Display.setVSyncEnabled(true);

            Display.create();

            Mouse.setGrabbed(true);
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();
        }

        LightingPipelineShaders.init();

        this.renderManager = new RenderManager(10000 * 4, 10000 * 6);

        this.lightingPipeline = new LightingPipeline(this.renderManager, 720, 480);

        ObjFileParser parser = new ObjFileParser(new ResourceLocation("/assets/lightingdemo3d/cube.obj"));
        parser.parse();

        ResourceLocation textureLocation = new ResourceLocation("/assets/lightingdemo3d/cube.png");

        Texture cubeTexture = new Texture(textureLocation);
        cubeTexture.upload();

        this.modelEntity = new Model(ShaderRegistry.instance.get("model"), parser.getModelData(), cubeTexture);

        this.lightingPipeline.diffusePass.addEntity(this.modelEntity);

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

    private void render()
    {
        this.lightingPipeline.getCamera().pitch(Mouse.getDY() / 200.f);
        this.lightingPipeline.getCamera().yaw(Mouse.getDX() / 200.f);
        this.lightingPipeline.getCamera().update();

        if (Keyboard.isKeyDown(Keyboard.KEY_W))
        {
            this.lightingPipeline.getCamera().walk(-0.03f);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A))
        {
            this.lightingPipeline.getCamera().strafe(-0.03f);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S))
        {
            this.lightingPipeline.getCamera().walk(0.03f);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            this.lightingPipeline.getCamera().strafe(0.03f);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        this.renderManager.begin();

        this.lightingPipeline.render();

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
