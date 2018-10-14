package com.belova.service;

import com.belova.entity.Task;
import com.belova.entity.User;
import com.belova.entity.enums.Complexity;
import com.belova.entity.enums.Status;
import com.belova.entity.enums.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TasksServiceImpl implements TasksService {

    @Autowired
    private EntityManager entityManager;

    @Override
    @PreAuthorize("hasRole('ROLE_LEAD')")
    public List<Task> getAllDepartmentTasks(Long id) {
        User user = entityManager.find(User.class, id);
        List<Task> tasksDepartments = entityManager.createQuery("select t from Task as t where t.user.department = :department", Task.class)
                .setParameter("department", user.getDepartment())
                .getResultList();
        tasksDepartments.forEach(Task::initProperty);
        return tasksDepartments;
    }

    @Override
    @PreAuthorize("hasPermission(#createdUser, 'create')")
    public void addTask(String name, String description, Status status, Complexity complexity, Type type,
                        User user, LocalDate date, boolean isQuickly, User createdUser) {
        Task newTask = new Task();
        newTask.setUser(user);
        newTask.setType(type);
        newTask.setComplexity(complexity);
        newTask.setStatus(status);
        newTask.setDeadline(java.sql.Date.valueOf(date));
        newTask.setQuickly(isQuickly);
        newTask.setDescription(description);
        newTask.setName(name);
        entityManager.persist(newTask);
    }

    @Override
    @PreAuthorize("hasPermission(#createdUser, 'edit')")
    public void editTask(Long id, String name, String description, Status status, Complexity complexity, Type type,
                         User user, LocalDate date, boolean isQuickly, User createdUser) {
        Task task = entityManager.find(Task.class, id);
        task.setUser(user);
        task.setType(type);
        task.setComplexity(complexity);
        task.setStatus(status);
        task.setDeadline(java.sql.Date.valueOf(date));
        task.setQuickly(isQuickly);
        task.setDescription(description);
        task.setName(name);
    }

    @Override
    public void deleteTask(Task task) {
        Task merge = entityManager.merge(task);
        entityManager.remove(merge);
    }
}
