package ru.mail.polis.fedoseenkov;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class MyFileDAO implements MyDAO {
    @NotNull
    private final File dir;

    public MyFileDAO(@NotNull final File dir) {
        this.dir = dir;
    }

    @NotNull
    private File getFile(@NotNull final String key) throws IllegalArgumentException {
        return new File(dir, key);
    }

    @NotNull
    @Override
    public byte[] get(@NotNull final String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        return Files.readAllBytes(Paths.get(dir + File.separator + key));
    }

    @NotNull
    @Override
    public void upsert(
            @NotNull final String key,
            @NotNull final byte[] value) throws IllegalArgumentException, IOException {
        try (OutputStream os = new FileOutputStream(getFile(key))) {
            os.write(value);
        }
    }

    @NotNull
    @Override
    public void delete(@NotNull String key) throws IllegalArgumentException, IOException {
        getFile(key).delete();
    }
}
