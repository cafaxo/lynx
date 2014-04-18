package com.cafaxo.lynx.util;

import java.util.ArrayList;

import com.cafaxo.lynx.graphics.RenderEntity;

public class InterpolatedLine extends RenderEntity
{

    public ArrayList<Vector2f> pointQueue = new ArrayList<Vector2f>(1000);

    private float thickness = 3.0f;

    public InterpolatedLine(int maxVertexDataSize, int maxIndicesCount)
    {
        super(maxVertexDataSize, maxIndicesCount);

        this.setVisible(false);
    }

    public void addPoint(Vector2f point)
    {
        this.pointQueue.add(point);
        this.setHasChanged(true);

        if (this.pointQueue.size() > 2)
        {
            this.setVisible(true);
        }
    }

    public void removeFirstPoint()
    {
        this.pointQueue.remove(0);
        this.setHasChanged(true);
    }

    public ArrayList<Vector2f> getCurvePoints(final ArrayList<Vector2f> points, float tension, int numOfSegments)
    {
        ArrayList<Vector2f> result = new ArrayList<Vector2f>();
        ArrayList<Vector2f> pointsCopy = new ArrayList<Vector2f>(points);

        float x, y; // our x,y coords
        float t1x, t2x, t1y, t2y; // tension vectors
        float c1, c2, c3, c4; // cardinal points
        float st, t; // steps based on num. of segments
        float pow3, pow2; // cache powers
        float pow32, pow23;
        float p0, p1, p2, p3; // cache points

        pointsCopy.add(0, pointsCopy.get(0)); // copy 1. point and insert at beginning
        pointsCopy.add(pointsCopy.size() - 1, pointsCopy.get(pointsCopy.size() - 1)); // copy last point and append

        // 1. loop goes through point array
        // 2. loop goes through each segment between the two points + one point before and after

        for (int i = 1; i < (pointsCopy.size() - 2); ++i)
        {
            p0 = pointsCopy.get(i).getX();
            p1 = pointsCopy.get(i).getY();
            p2 = pointsCopy.get(i + 1).getX();
            p3 = pointsCopy.get(i + 1).getY();

            // calc tension vectors
            t1x = (p2 - pointsCopy.get(i - 1).getX()) * tension;
            t2x = (pointsCopy.get(i + 2).getX() - p0) * tension;

            t1y = (p3 - pointsCopy.get(i - 1).getY()) * tension;
            t2y = (pointsCopy.get(i + 2).getY() - p1) * tension;

            for (t = 0; t <= numOfSegments; t++)
            {
                // calc step
                st = t / numOfSegments;

                pow2 = (float) Math.pow(st, 2);
                pow3 = pow2 * st;
                pow23 = pow2 * 3;
                pow32 = pow3 * 2;

                // calc cardinals
                c1 = (pow32 - pow23) + 1;
                c2 = pow23 - pow32;
                c3 = (pow3 - (2 * pow2)) + st;
                c4 = pow3 - pow2;

                // calc x and y cords with common control vectors
                x = (c1 * p0) + (c2 * p2) + (c3 * t1x) + (c4 * t2x);
                y = (c1 * p1) + (c2 * p3) + (c3 * t1y) + (c4 * t2y);

                // store points in array
                result.add(new Vector2f(x, y));
            }
        }

        return result;
    }

    @Override
    protected void refreshVertexAndIndexData()
    {
        this.resetVertexAndIndexData();

        ArrayList<Vector2f> result = this.getCurvePoints(this.pointQueue, 0.5f, 6);

        int i = 0;

        for (int j = 0; j < (result.size() - 1); ++j)
        {
            if (result.get(j).equals(result.get(j + 1)))
            {
                result.remove(j);
                j--;
            }
        }

        if (result.size() < 3)
        {
            return;
        }

        Vector2f vec1 = result.get(i);
        Vector2f vec2 = result.get(i + 1);

        Vector2f sub = new Vector2f(vec2.getX() - vec1.getX(), vec2.getY() - vec1.getY());

        Vector2f n1 = new Vector2f(-sub.getY(), sub.getX());
        Vector2f n2 = new Vector2f(sub.getY(), -sub.getX());

        n1 = n1.normalize();
        n2 = n2.normalize();

        n1.scale(this.thickness);
        n2.scale(this.thickness);

        Vector2f res1 = Vector2f.add(vec1, n1);
        Vector2f res2 = Vector2f.add(vec1, n2);

        this.addVertex(res1.getX(), res1.getY(), 0.4f, 0.5f, 0.6f, 1.f);
        this.addVertex(res2.getX(), res2.getY(), 0.4f, 0.5f, 0.6f, 1.f);

        for (i += 1; i < (result.size() - 1); ++i)
        {
            Vector2f previous = result.get(i - 1);
            Vector2f active = result.get(i);
            Vector2f next = result.get(i + 1);

            sub = new Vector2f(next.getX() - previous.getX(), next.getY() - previous.getY());
            sub = sub.normalize();
            n1 = new Vector2f(-sub.getY(), sub.getX());
            n2 = new Vector2f(sub.getY(), -sub.getX());

            n1 = n1.normalize();
            n2 = n2.normalize();

            n1.scale(this.thickness);
            n2.scale(this.thickness);

            res1 = Vector2f.add(active, n1);
            res2 = Vector2f.add(active, n2);

            this.addVertex(res1.getX(), res1.getY(), 0.4f, 0.5f, 0.6f, 1.f);
            this.addVertex(res2.getX(), res2.getY(), 0.4f, 0.5f, 0.6f, 1.f);

            this.addTriangleIndices(i * 2, (i * 2) - 1, (i * 2) - 2);
            this.addTriangleIndices(i * 2, (i * 2) + 1, (i * 2) - 1);
        }

        vec1 = result.get(result.size() - 2);
        vec2 = result.get(result.size() - 1);

        sub = new Vector2f(vec2.getX() - vec1.getX(), vec2.getY() - vec1.getY());

        n1 = new Vector2f(-sub.getY(), sub.getX());
        n2 = new Vector2f(sub.getY(), -sub.getX());

        n1 = n1.normalize();
        n2 = n2.normalize();

        n1.scale(this.thickness);
        n2.scale(this.thickness);

        res1 = Vector2f.add(vec2, n1);
        res2 = Vector2f.add(vec2, n2);

        this.addVertex(res1.getX(), res1.getY(), 0.4f, 0.5f, 0.6f, 1.f);
        this.addVertex(res2.getX(), res2.getY(), 0.4f, 0.5f, 0.6f, 1.f);

        this.addTriangleIndices(i * 2, (i * 2) - 1, (i * 2) - 2);
        this.addTriangleIndices(i * 2, (i * 2) + 1, (i * 2) - 1);

        super.refreshVertexAndIndexData();
    }

    private void addVertex(float x, float y, float r, float g, float b, float a)
    {
        this.addVertexData(x);
        this.addVertexData(y);

        int intBits = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8) | ((int) (255 * r));
        this.addVertexData(Float.intBitsToFloat(intBits & 0xfeffffff));
    }

    private void addTriangleIndices(int i1, int i2, int i3)
    {
        this.addIndexData(i1);
        this.addIndexData(i2);
        this.addIndexData(i3);
    }

}
