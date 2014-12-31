package com.cafaxo.lynx.util;

import java.util.ArrayList;

import com.cafaxo.lynx.math.Vector2f;

public class InterpolatedLine extends GeometryProvider<Vector2f>
{

    public ArrayList<Vector2f> pointQueue = new ArrayList<Vector2f>(1000);

    public void addPoint(Vector2f point)
    {
        this.pointQueue.add(point);
    }

    public void removeFirstPoint()
    {
        this.pointQueue.remove(0);
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
            p0 = pointsCopy.get(i).x;
            p1 = pointsCopy.get(i).y;
            p2 = pointsCopy.get(i + 1).x;
            p3 = pointsCopy.get(i + 1).y;

            // calc tension vectors
            t1x = (p2 - pointsCopy.get(i - 1).x) * tension;
            t2x = (pointsCopy.get(i + 2).x - p0) * tension;

            t1y = (p3 - pointsCopy.get(i - 1).y) * tension;
            t2y = (pointsCopy.get(i + 2).y - p1) * tension;

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

    protected void refresh(float thickness)
    {
        this.vertices.clear();
        this.faces.clear();

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

        Vector2f sub = new Vector2f(vec2.x - vec1.x, vec2.y - vec1.y);

        Vector2f n1 = new Vector2f(-sub.y, sub.x);
        Vector2f n2 = new Vector2f(sub.y, -sub.x);

        n1 = n1.normalize();
        n2 = n2.normalize();

        n1.scale(thickness);
        n2.scale(thickness);

        Vector2f res1 = Vector2f.add(vec1, n1);
        Vector2f res2 = Vector2f.add(vec1, n2);

        this.vertices.add(res1);
        this.vertices.add(res2);

        for (i += 1; i < (result.size() - 1); ++i)
        {
            Vector2f previous = result.get(i - 1);
            Vector2f active = result.get(i);
            Vector2f next = result.get(i + 1);

            sub = new Vector2f(next.x - previous.x, next.y - previous.y);
            sub = sub.normalize();
            n1 = new Vector2f(-sub.y, sub.x);
            n2 = new Vector2f(sub.y, -sub.x);

            n1 = n1.normalize();
            n2 = n2.normalize();

            n1.scale(thickness);
            n2.scale(thickness);

            res1 = Vector2f.add(active, n1);
            res2 = Vector2f.add(active, n2);

            this.vertices.add(res1);
            this.vertices.add(res2);

            this.faces.add(new TriangleIndices(i * 2, (i * 2) - 1, (i * 2) - 2));
            this.faces.add(new TriangleIndices(i * 2, (i * 2) + 1, (i * 2) - 1));
        }

        vec1 = result.get(result.size() - 2);
        vec2 = result.get(result.size() - 1);

        sub = new Vector2f(vec2.x - vec1.x, vec2.y - vec1.y);

        n1 = new Vector2f(-sub.y, sub.x);
        n2 = new Vector2f(sub.y, -sub.x);

        n1 = n1.normalize();
        n2 = n2.normalize();

        n1.scale(thickness);
        n2.scale(thickness);

        res1 = Vector2f.add(vec2, n1);
        res2 = Vector2f.add(vec2, n2);

        this.vertices.add(res1);
        this.vertices.add(res2);

        this.faces.add(new TriangleIndices(i * 2, (i * 2) - 1, (i * 2) - 2));
        this.faces.add(new TriangleIndices(i * 2, (i * 2) + 1, (i * 2) - 1));
    }

}
