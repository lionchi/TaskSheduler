package com.belova.common;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class StorageTask {
    private Map<Class<? extends Runnable>, ScheduledFuture<?>> storages = new HashMap();

    public StorageTask() {
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

    public void clearAll() {
        storages.values().forEach(scheduledFuture -> {
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        });

        storages.clear();
    }
}
