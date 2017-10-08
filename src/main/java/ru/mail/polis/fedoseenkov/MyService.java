package ru.mail.polis.fedoseenkov;

import com.sun.net.httpserver.HttpServer;

import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MyService implements KVService {
    private static final String PREFIX = "id=";

    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;

    @NotNull
    private static String extractId(@NotNull final String query){
          if (query.startsWith(PREFIX)) {
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

                    switch (http.getRequestMethod()) {
                        case "GET":
                            final byte[] getValue = dao.get(id);
                            http.sendResponseHeaders(200, getValue.length);
                            http.getResponseBody().write(getValue);
                            break;
                        case "DELETE":
                            dao.delete(id);
                            http.sendResponseHeaders(202, 0);
                            break;
                        case "PUT":
                            final int contentLenght =
                                    Integer.valueOf(
                                            http.getRequestHeaders().getFirst("Content-Lenght"));
                            final byte[] putValue = new byte[contentLenght];
                            if (http.getRequestBody().read(putValue) != putValue.length) {
                                throw new IOException("Can't read file at once.");
                            }

                            dao.upsert(id, putValue);
                            http.sendResponseHeaders(201, 0);
                            break;
                        default:
                            http.sendResponseHeaders(404, 0);
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
