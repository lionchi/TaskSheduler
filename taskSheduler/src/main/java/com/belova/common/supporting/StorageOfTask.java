package com.belova.common.supporting;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Component
public class StorageOfTask {
    private Map<Class<? extends Runnable>, ScheduledFuture<?>> storages = new HashMap();

    public StorageOfTask() {
    }

    public void put(Class<? extends Runnable> clazz, ScheduledFuture<?> scheduledFuture) {
        storages.put(clazz, scheduledFuture);
    }

    public ScheduledFuture<?> getValue(Class<? extends Runnable> clazz) {
        return storages.get(clazz);
    }

    public boolean contains(Class<? extends Runnable> clazz) {
        return storages.containsKey(clazz);
    }

    public void remove(Class<? extends Runnable> clazz) {
        storages.remove(clazz);
    }

    public void clearAllOfClass(Class<? extends Runnable> clazz) {
        Map<Class<? extends Runnable>, ScheduledFuture<?>> temp = new HashMap();
        for (Map.Entry<Class<? extends Runnable>, ScheduledFuture<?>> entry : storages.entrySet()) {
            if (entry.getKey().equals(clazz)) {
                temp.put(entry.getKey(), entry.getValue());
            }
        }

        temp.values().forEach(scheduledFuture -> {
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        });

        for (Map.Entry<Class<? extends Runnable>, ScheduledFuture<?>> entry : temp.entrySet()) {
            storages.remove(entry.getKey(), entry.getValue());
        }
    }

    public void clearAll() {
        storages.values().forEach(scheduledFuture -> {
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        });
        storages.clear();
    }
}
