package com.belova.service;

import com.belova.entity.Task;
import com.belova.entity.User;
import com.belova.entity.enums.Complexity;
import com.belova.entity.enums.Status;
import com.belova.entity.enums.Type;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Task> getAllUserTasks(Long id) {
        List<Task> tasksDepartments = entityManager.createQuery("select t from Task as t where t.user.id = :id", Task.class)
                .setParameter("id", id)
                .getResultList();
        tasksDepartments.forEach(Task::initProperty);
        return tasksDepartments;
    }

    @Override
    @PreAuthorize("hasPermission(#createdUser, 'create')")
    public void addTask(String name, String description, Status status, Complexity complexity, Type type,
                        User user, LocalDate date, boolean isQuickly, User createdUser) {
        Task newTask = new Task();
        newTask.setUser(entityManager.find(User.class, user.getId()));
        newTask.setType(type);
        newTask.setComplexity(complexity);
        newTask.setStatus(status);
        newTask.setDeadline(java.sql.Date.valueOf(date));
        newTask.setQuickly(isQuickly);
        newTask.setDescription(description);
        newTask.setName(name);
        newTask.setCreateDate(new Date());
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
    @PreAuthorize("hasPermission(#removingOfUser, 'remove')")
    public void deleteTask(Task task, User removingOfUser) {
        Task merge = entityManager.merge(task);
        entityManager.remove(merge);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public void changeStatus(Long id, Status newStatus) {
        Task task = entityManager.find(Task.class, id);
        task.setStatus(newStatus);
    }

    @Override
    public void changeFlagRead(Long taskId) {
        Task task = entityManager.find(Task.class, taskId);
        task.setRead(true);
    }

    @Override
    public Task findNewTask(Long userId) {
        User user = entityManager.find(User.class, userId);
        Optional<Task> findTask = user.getTasks()
                .stream()
                .filter(task -> !task.isRead() && compareToDate(task.getCreateDate(), new Date()))
                .findAny();
        return findTask.orElse(null);
    }

    private boolean compareToDate(Date createDate, Date currentDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formatToCreateDateFull = simpleDateFormat.format(createDate);
        String formatToCurrentDateFull = simpleDateFormat.format(currentDate);
        if (formatToCreateDateFull.equals(formatToCurrentDateFull)) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
            String formatToCreateDate = simpleDateFormat.format(createDate);
            String formatToCurrentDate = simpleDateFormat.format(currentDate);

            String[] splitToFormatCreateDate = formatToCreateDate.split(":");
            Integer hourOfTheCreateDate = Integer.valueOf(splitToFormatCreateDate[0]);
            Integer minuteOfTheCreateDate = Integer.valueOf(splitToFormatCreateDate[1]);

            String[] splitToFormatCurrentDate = formatToCurrentDate.split(":");
            Integer hourOfTheCurrentDate = Integer.valueOf(splitToFormatCurrentDate[0]);
            Integer minuteOfTheCurrentDate = Integer.valueOf(splitToFormatCurrentDate[1]);

            if (hourOfTheCreateDate.equals(hourOfTheCurrentDate)) {
                int difference = minuteOfTheCurrentDate - minuteOfTheCreateDate;
                return difference < 30;
            } else {
                return false;
            }
        }
        return false;
    }
}
