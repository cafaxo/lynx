package com.cafaxo.lynx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ObjFileParser
{

    class ModelProperties
    {
        int positions;

        int texels;

        int normals;

        int faces;
    }

    public class ModelData
    {
        public float[] positions;

        public float[] texels;

        public float[] normals;

        public int[] faces;

        public ModelData(ModelProperties properties)
        {
            this.positions = new float[properties.positions * 3];
            this.texels = new float[properties.texels * 2];
            this.normals = new float[properties.normals * 3];
            this.faces = new int[properties.faces * 9];
        }
    }

    private ResourceLocation objFile;

    private ModelProperties modelProperties = new ModelProperties();

    private ModelData modelData;

    public ObjFileParser(ResourceLocation objFile)
    {
        this.objFile = objFile;
    }

    public void parse()
    {
        FileInputStream fis = null;
        BufferedReader br = null;

        try
        {
            fis = new FileInputStream(new File(this.objFile.getURI()));

            br = new BufferedReader(new InputStreamReader(fis));

            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("v "))
                {
                    this.modelProperties.positions++;
                }
                else if (line.startsWith("vt"))
                {
                    this.modelProperties.texels++;
                }
                else if (line.startsWith("vn"))
                {
                    this.modelProperties.normals++;
                }
                else if (line.startsWith("f "))
                {
                    this.modelProperties.faces++;
                }
            }

            fis.getChannel().position(0);

            br = new BufferedReader(new InputStreamReader(fis));

            this.modelData = new ModelData(this.modelProperties);

            ModelProperties counter = new ModelProperties();

            while ((line = br.readLine()) != null)
            {
                String split[] = line.split(" ");

                if (line.startsWith("v "))
                {
                    this.modelData.positions[counter.positions * 3] = Float.parseFloat(split[1]);
                    this.modelData.positions[(counter.positions * 3) + 1] = Float.parseFloat(split[2]);
                    this.modelData.positions[(counter.positions * 3) + 2] = Float.parseFloat(split[3]);

                    counter.positions++;
                }
                else if (line.startsWith("vt"))
                {
                    this.modelData.texels[counter.texels * 2] = Float.parseFloat(split[1]);
                    this.modelData.texels[(counter.texels * 2) + 1] = Float.parseFloat(split[2]);

                    counter.texels++;
                }
                else if (line.startsWith("vn"))
                {
                    this.modelData.normals[counter.normals * 3] = Float.parseFloat(split[1]);
                    this.modelData.normals[(counter.normals * 3) + 1] = Float.parseFloat(split[2]);
                    this.modelData.normals[(counter.normals * 3) + 2] = Float.parseFloat(split[3]);

                    counter.normals++;
                }
                else if (line.startsWith("f "))
                {
                    String subsplit0[] = split[1].split("/");
                    this.modelData.faces[counter.faces * 9] = Integer.parseInt(subsplit0[0]);
                    this.modelData.faces[(counter.faces * 9) + 1] = Integer.parseInt(subsplit0[1]);
                    this.modelData.faces[(counter.faces * 9) + 2] = Integer.parseInt(subsplit0[2]);

                    String subsplit1[] = split[2].split("/");
                    this.modelData.faces[(counter.faces * 9) + 3] = Integer.parseInt(subsplit1[0]);
                    this.modelData.faces[(counter.faces * 9) + 4] = Integer.parseInt(subsplit1[1]);
                    this.modelData.faces[(counter.faces * 9) + 5] = Integer.parseInt(subsplit1[2]);

                    String subsplit2[] = split[3].split("/");
                    this.modelData.faces[(counter.faces * 9) + 6] = Integer.parseInt(subsplit2[0]);
                    this.modelData.faces[(counter.faces * 9) + 7] = Integer.parseInt(subsplit2[1]);
                    this.modelData.faces[(counter.faces * 9) + 8] = Integer.parseInt(subsplit2[2]);

                    counter.faces++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    public ModelData getModelData()
    {
        return this.modelData;
    }
}
