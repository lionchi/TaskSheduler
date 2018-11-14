package com.belova.common;

import com.belova.entity.Task;
import com.belova.service.TasksServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Notification implements Runnable {
    @Autowired
    private UserSession userSession;
    @Autowired
    private TasksServiceImpl tasksService;

    @Override
    public void run() {
        Task newTask = tasksService.findNewTask(userSession.getId());
    }
}
