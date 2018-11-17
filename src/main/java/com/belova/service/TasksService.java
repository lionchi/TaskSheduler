package com.belova.service;

import com.belova.entity.Task;
import com.belova.entity.User;
import com.belova.entity.enums.*;

import java.time.LocalDate;
import java.util.List;

public interface TasksService {
    List<Task> getAllDepartmentTasks(Long id);

    List<Task> getAllUserTasks(Long id);

    void addTask(String name, String description, Status status, Complexity complexity, Type type, User user,
                 LocalDate date, boolean isQuickly, User createdUser);

    void editTask(Long id, String name, String description, Status status, Complexity complexity, Type type, User user,
                 LocalDate date, boolean isQuickly, User createdUser);

    void deleteTask(Task task, User removingOfUser);

    void changeStatus (Long id, Status newStatus);

    void changeFlagRead (Long taskId);

    Task findNewTask (Long userId);
}
