package ru.mail.polis.fedoseenkov;

import com.sun.net.httpserver.HttpServer;

import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MyService implements KVService {
    private static final String PREFIX = "id=";

    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;

    @NotNull
    private static String extractId(@NotNull final String query) {
          if (!query.startsWith(PREFIX)) {
            throw new IllegalArgumentException("bad string");
        }

        return query.substring(PREFIX.length());
    }

    public MyService(
            int port,
            @NotNull final MyDAO dao) throws IOException {
        this.server =
                HttpServer.create(
                        new InetSocketAddress(port),
                        0);
        this.dao = dao;

        this.server.createContext(
                "/v0/status",
                http -> {
                    final String response = "ONLINE";
                    http.sendResponseHeaders(200, response.length());
                    http.getResponseBody().write(response.getBytes());
                    http.close();
                });

        this.server.createContext(
                "/v0/entity",
                http -> {
                    final String id = extractId(http.getRequestURI().getQuery());
                    if ("".equals(id)) http.sendResponseHeaders(400, 0);
                    else {
                        switch (http.getRequestMethod()) {
                            case "GET":
                                final byte[] getValue;
                                try {
                                    getValue = dao.get(id);
                                } catch (NoSuchElementException | IOException e) {
                                    http.sendResponseHeaders(404, 0);
                                    break;
                                }
                                http.sendResponseHeaders(200, getValue.length);
                                http.getResponseBody().write(getValue);
                                break;
                            case "DELETE":
                                dao.delete(id);
                                http.sendResponseHeaders(202, 0);
                                break;
                            case "PUT":
                                final int contentLenght = http.getRequestBody().available();
                                if (contentLenght == 0) {
                                    dao.upsert(id, new byte[0]);
                                    http.sendResponseHeaders(201, 0);
                                    break;
                                }
                                else {
                                    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                                         InputStream in = http.getRequestBody()) {
                                        byte[] buffer = new byte[1024];
                                        while (true) {
                                            int readBytes = in.read(buffer);
                                            if (readBytes < 0) {
                                                break;
                                            }
                                            out.write(buffer, 0, readBytes);
                                        }

                                        dao.upsert(id, out.toByteArray());
                                        http.sendResponseHeaders(201, 0);
                                    } catch (IOException e) {
                                        http.sendResponseHeaders(500, 0);
                                    }
                                    break;
                                }
                            default:
                                http.sendResponseHeaders(405, 0);
                                break;
                        }
                    }
                    http.close();
                });
    }

    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        this.server.stop(0);
    }
}
