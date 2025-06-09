package ru.otus.java.basic.june.http.server.processors;

import com.google.gson.Gson;
import ru.otus.java.basic.june.http.server.HttpRequest;
import ru.otus.java.basic.june.http.server.app.Item;
import ru.otus.java.basic.june.http.server.app.ItemsRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GetItemsRequestProcessor implements RequestProcessor {

    private final ItemsRepository itemsRepository;

    public GetItemsRequestProcessor(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        Gson gson = new Gson();
        String itemsJson;

        if (request.getParameter("id") != null) {
            Long id = Long.parseLong(request.getParameter("id"));
            Item item = itemsRepository.getById(id);
            if (item == null) {
                Path filePath = Paths.get("static/404.html");
                byte[] fileData = Files.readAllBytes(filePath);
                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n" +
                        "<html><body>" +
                        "<h1>ITEM NOT FOUND!!!!!!!!!!!!!!!</h1>" +
                        "</body></html>";
                output.write(response.getBytes(StandardCharsets.UTF_8));
                output.write(fileData);
                return;
            }
            itemsJson = gson.toJson(item);
        } else {
            List<Item> items = itemsRepository.getAll();
            itemsJson = gson.toJson(items);
        }

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                itemsJson;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
