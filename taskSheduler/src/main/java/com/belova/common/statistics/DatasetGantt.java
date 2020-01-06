package com.belova.common.statistics;

import com.belova.entity.Task;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DatasetGantt {
    private SimpleDateFormat sdf;

    public IntervalCategoryDataset createDataset(List<Task> tasks) {
        final TaskSeries taskSeries1 = new TaskSeries("Планирование");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String dateStart = simpleDateFormat.format(task.getCreateDate());
            String dateEnd = simpleDateFormat.format(task.getDeadline());
            taskSeries1.add(new org.jfree.data.gantt.Task(task.getName(), new SimpleTimePeriod(gettingDateFromString(dateStart), gettingDateFromString(dateEnd))));
        }

        final TaskSeries taskSeries2 = new TaskSeries("Выполнение");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String dateStart = simpleDateFormat.format(task.getCreateDate());
            taskSeries2.add(new org.jfree.data.gantt.Task(tasks.get(i).getName(), new SimpleTimePeriod(gettingDateFromString(dateStart), gettingDateFromString(currentDate))));
        }

        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(taskSeries1);
        collection.add(taskSeries2);

        return collection;
    }

    private Date gettingDateFromString(final String stringDate) {
        if (sdf == null)
            sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
