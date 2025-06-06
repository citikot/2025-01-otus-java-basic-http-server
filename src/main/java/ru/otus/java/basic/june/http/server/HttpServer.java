package ru.otus.java.basic.june.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("java:S2189") // SonarQube warning suppress
public class HttpServer {

    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);
    private static final int MAX_THREADS = 4;

    private final int port;
    private final Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port);
             ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS)
        ) {

            log.info("Сервер запущен на порту {}. Ожидаем подключения", port);

            while (true) {
                Socket socket = serverSocket.accept();
                log.info("Новое соединение от {}", socket.getRemoteSocketAddress());
                executor.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            log.error("Oбщая ошибка сервера: {}", e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (socket) {
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            if (n < 1) {
                return;
            }
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.info(true);

            dispatcher.execute(request, socket.getOutputStream());

        } catch (IOException e) {
            log.error("Ошибка при обработке клиента {}", socket.getRemoteSocketAddress(), e);
        }
    }

}
