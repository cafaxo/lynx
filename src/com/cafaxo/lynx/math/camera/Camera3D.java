package com.cafaxo.lynx.math.camera;

import com.cafaxo.lynx.math.Matrix4x4f;
import com.cafaxo.lynx.math.Vector3f;

public class Camera3D extends Camera
{

    public Vector3f position = new Vector3f(0.0f, -5.0f, 0.0f);

    public Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    public Vector3f along = new Vector3f(1.0f, 0.0f, 0.0f);

    public Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f);

    public float tanFovY;

    public float tanFovX;

    public Vector3f yAxis;

    public Vector3f xAxis;

    private float zNear;

    private float zFar;

    public Camera3D(float fovy, float aspect, float zNear, float zFar)
    {
        this.zNear = zNear;
        this.zFar = zFar;

        float f = (float) (1. / Math.tan(fovy / 2.f));

        this.getView().setIdentity();

        this.getProjection().a11 = f / aspect;
        this.getProjection().a22 = f;
        this.getProjection().a33 = (zFar + zNear) / (zNear - zFar);
        this.getProjection().a43 = (2 * zFar * zNear) / (zNear - zFar);
        this.getProjection().a34 = -1.f;

        this.tanFovY = (float) Math.tan((fovy) / 2.0f);

        float width = 720.f;
        float height = 480.f;

        this.tanFovX = (width * this.tanFovY) / height;
    }

    public void update()
    {
        float x = Vector3f.dotProduct(this.along, this.position);

        float y = Vector3f.dotProduct(this.up, this.position);

        float z = Vector3f.dotProduct(this.forward, this.position);

        this.getView().a11 = this.along.x;
        this.getView().a21 = this.along.y;
        this.getView().a31 = this.along.z;

        this.getView().a12 = this.up.x;
        this.getView().a22 = this.up.y;
        this.getView().a32 = this.up.z;

        this.getView().a13 = -this.forward.x;
        this.getView().a23 = -this.forward.y;
        this.getView().a33 = -this.forward.z;

        this.getView().a41 = -x;
        this.getView().a42 = -y;
        this.getView().a43 = z;

        this.xAxis = Vector3f.cross(this.up, this.forward);
        this.xAxis.normalize();
        this.yAxis = Vector3f.cross(new Vector3f(-1f * this.forward.x, -1f * this.forward.y, -1f * this.forward.z), this.xAxis);

        //System.out.println(this.xAxis);
    }

    public void lookAt(Vector3f eye, Vector3f center, Vector3f up)
    {
        this.view = Matrix4x4f.lookAt(eye, center, up);
    }

    public void walk(float delta)
    {
        this.position.translate(-this.forward.x * delta, -this.forward.y * delta, -this.forward.z * delta);
    }

    public void strafe(float delta)
    {
        this.position.translate(this.along.x * delta, this.along.y * delta, this.along.z * delta);
    }

    public void yaw(float angle)
    {
        this.up.rotateY(angle);
        this.forward.rotateY(angle);
        this.along = Vector3f.cross(this.forward, this.up);
    }

    public void pitch(float angle)
    {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        this.forward.setPosition((this.forward.x * cos) + (this.up.x * sin), (this.forward.y * cos) + (this.up.y * sin), (this.forward.z * cos) + (this.up.z * sin));

        this.forward.normalize();

        this.up = Vector3f.cross(this.forward, this.along);

        this.up.x *= -1f;
        this.up.y *= -1f;
        this.up.z *= -1f;
    }

    public float getNear()
    {
        return this.zNear;
    }

    public float getFar()
    {
        return this.zFar;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public void reset()
    {
        this.view = new Matrix4x4f();
        this.view.setIdentity();

        this.position = new Vector3f(0.0f, 5.0f, 0.0f);

        this.up = new Vector3f(0.0f, 1.0f, 0.0f);

        this.along = new Vector3f(1.0f, 0.0f, 0.0f);

        this.forward = new Vector3f(0.0f, 0.0f, -1.0f);
    }

}
