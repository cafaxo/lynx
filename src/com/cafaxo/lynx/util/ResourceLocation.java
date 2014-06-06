package com.cafaxo.lynx.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class ResourceLocation
{

    private final String path;

    public ResourceLocation(String path)
    {
        this.path = path;
    }

    public InputStream getInputStream()
    {
        return ResourceLocation.class.getResourceAsStream(this.path);
    }

    public URI getURI()
    {
        try
        {
            return ResourceLocation.class.getResource(this.path).toURI();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public String getString()
    {
        try
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int bytesRead;
            byte[] data = new byte[16384];

            InputStream inputStream = this.getInputStream();

            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1)
            {
                buffer.write(data, 0, bytesRead);
            }

            return new String(buffer.toByteArray(), Charset.forName("UTF-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public String getPath()
    {
        return this.path;
    }
}
