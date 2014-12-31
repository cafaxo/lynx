package com.cafaxo.lynx.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cafaxo.lynx.math.Vector3f;

public class IcoSphereCreator extends GeometryProvider<Vector3f>
{

    private int index;

    private HashMap<Long, Integer> middlePointIndexCache = new HashMap<Long, Integer>();

    public void create(int recursionLevel)
    {
        // create 12 vertices of a icosahedron
        float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);

        this.addVertex(new Vector3f(-1, t, 0));
        this.addVertex(new Vector3f(1, t, 0));
        this.addVertex(new Vector3f(-1, -t, 0));
        this.addVertex(new Vector3f(1, -t, 0));

        this.addVertex(new Vector3f(0, -1, t));
        this.addVertex(new Vector3f(0, 1, t));
        this.addVertex(new Vector3f(0, -1, -t));
        this.addVertex(new Vector3f(0, 1, -t));

        this.addVertex(new Vector3f(t, 0, -1));
        this.addVertex(new Vector3f(t, 0, 1));
        this.addVertex(new Vector3f(-t, 0, -1));
        this.addVertex(new Vector3f(-t, 0, 1));

        // create 20 triangles of the icosahedron

        // 5 faces around point 0
        this.faces.add(new TriangleIndices(0, 11, 5));
        this.faces.add(new TriangleIndices(0, 5, 1));
        this.faces.add(new TriangleIndices(0, 1, 7));
        this.faces.add(new TriangleIndices(0, 7, 10));
        this.faces.add(new TriangleIndices(0, 10, 11));

        // 5 adjacent faces
        this.faces.add(new TriangleIndices(1, 5, 9));
        this.faces.add(new TriangleIndices(5, 11, 4));
        this.faces.add(new TriangleIndices(11, 10, 2));
        this.faces.add(new TriangleIndices(10, 7, 6));
        this.faces.add(new TriangleIndices(7, 1, 8));

        // 5 faces around point 3
        this.faces.add(new TriangleIndices(3, 9, 4));
        this.faces.add(new TriangleIndices(3, 4, 2));
        this.faces.add(new TriangleIndices(3, 2, 6));
        this.faces.add(new TriangleIndices(3, 6, 8));
        this.faces.add(new TriangleIndices(3, 8, 9));

        // 5 adjacent faces
        this.faces.add(new TriangleIndices(4, 9, 5));
        this.faces.add(new TriangleIndices(2, 4, 11));
        this.faces.add(new TriangleIndices(6, 2, 10));
        this.faces.add(new TriangleIndices(8, 6, 7));
        this.faces.add(new TriangleIndices(9, 8, 1));

        // refine triangles
        for (int i = 0; i < recursionLevel; i++)
        {
            List<TriangleIndices> faces2 = new ArrayList<TriangleIndices>();

            for (TriangleIndices tri : this.faces)
            {
                // replace triangle by 4 triangles
                int a = this.getMiddlePoint(tri.v1, tri.v2);
                int b = this.getMiddlePoint(tri.v2, tri.v3);
                int c = this.getMiddlePoint(tri.v3, tri.v1);

                faces2.add(new TriangleIndices(tri.v1, a, c));
                faces2.add(new TriangleIndices(tri.v2, b, a));
                faces2.add(new TriangleIndices(tri.v3, c, b));
                faces2.add(new TriangleIndices(a, b, c));
            }

            this.faces = faces2;
        }
    }

    // add vertex to mesh, fix position to be on unit sphere, return index
    private int addVertex(Vector3f p)
    {
        p.normalize();
        this.vertices.add(p);
        return this.index++;
    }

    // return index of point in the middle of p1 and p2
    private int getMiddlePoint(int p1, int p2)
    {
        // first check if we have it already
        boolean firstIsSmaller = p1 < p2;
        long smallerIndex = firstIsSmaller ? p1 : p2;
        long greaterIndex = firstIsSmaller ? p2 : p1;
        long key = (smallerIndex << 32) + greaterIndex;

        Integer ret = this.middlePointIndexCache.get(key);

        if (ret != null)
        {
            return ret;
        }

        // not in cache, calculate it
        Vector3f point1 = this.vertices.get(p1);
        Vector3f point2 = this.vertices.get(p2);

        Vector3f middle = new Vector3f((point1.x + point2.x) / 2.0f, (point1.y + point2.y) / 2.0f, (point1.z + point2.z) / 2.0f);

        // add vertex makes sure point is on unit sphere
        int i = this.addVertex(middle);

        // store it, return index
        this.middlePointIndexCache.put(key, i);
        return i;
    }
}
