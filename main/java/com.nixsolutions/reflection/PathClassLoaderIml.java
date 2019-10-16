package com.nixsolutions.reflection;

import com.nixsolutions.ppp.reflection.PathClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class PathClassLoaderIml extends ClassLoader implements PathClassLoader {

    private Path path;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Class<?> findClass(String name) {
        if (!name.endsWith(".class")) {
            name = name + ".class";
        }
        File file = this.getPath().resolve(name).toAbsolutePath().toFile();
        byte[] bytes = loadDataFromClass(path + file.toString() + name);
        return defineClass(path + file.toString() + name, bytes, 0,
                bytes.length);
    }

    private byte[] loadDataFromClass(String name) {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(name);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int length;
        try {
            if (inputStream != null) {
                while ((length = inputStream.read()) != -1)
                    byteArrayOutputStream.write(length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
