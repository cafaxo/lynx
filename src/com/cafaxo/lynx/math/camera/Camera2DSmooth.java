package com.cafaxo.lynx.math.camera;

public class Camera2DSmooth extends Camera2D
{

    private static class Velocity
    {
        float v = 0.f;
    }

    private Velocity vx = new Velocity();

    private Velocity vy = new Velocity();

    public Camera2DSmooth(float left, float right, float bottom, float top)
    {
        super(left, right, bottom, top);
    }

    public void follow(float targetX, float targetY, float smoothTime, float maxSpeed, float deltaTime)
    {
        float newX = smoothDamp(this.getX(), -targetX, this.vx, smoothTime, maxSpeed, deltaTime);
        float newY = smoothDamp(this.getY(), -targetY, this.vy, smoothTime, maxSpeed, deltaTime);

        this.setPosition(newX, newY);
    }

    private static float clamp(float val, float min, float max)
    {
        return Math.max(min, Math.min(max, val));
    }

    private static float smoothDamp(float current, float target, Velocity currentVelocity, float smoothTime, float maxSpeed, float deltaTime)
    {
        smoothTime = Math.max(0.0001f, smoothTime);
        float num = 2f / smoothTime;
        float num2 = num * deltaTime;
        float num3 = 1f / (1f + num2 + (0.48f * num2 * num2) + (0.235f * num2 * num2 * num2));
        float num4 = current - target;
        float num5 = target;
        float num6 = maxSpeed * smoothTime;
        num4 = clamp(num4, -num6, num6);
        target = current - num4;
        float num7 = (currentVelocity.v + (num * num4)) * deltaTime;
        currentVelocity.v = (currentVelocity.v - (num * num7)) * num3;
        float num8 = target + ((num4 + num7) * num3);

        if (((num5 - current) > 0f) == (num8 > num5))
        {
            num8 = num5;
            currentVelocity.v = (num8 - num5) / deltaTime;
        }

        return num8;
    }

}
