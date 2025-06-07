package ru.otus.java.basic.june.http.server.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemsRepository {
    private List<Item> items;

    public ItemsRepository() {
        this.items = new ArrayList<>(Arrays.asList(
                new Item(1L, "Milk", BigDecimal.valueOf(80)),
                new Item(2L, "Bread", BigDecimal.valueOf(38))
        ));
    }

    public List<Item> getAll() {
        return Collections.unmodifiableList(items);
    }

    public Item getById(Long id) {
        return items.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }

    public Item create(Item item) {

        Object lock = new Object();

        synchronized (lock) {
            Long newId = 1L;
            for (Item i : items) {
                if (newId <= i.getId()) {
                    newId = i.getId() + 1L;
                }
            }
            item.setId(newId);
            items.add(item);
        }

        return item;
    }
}
