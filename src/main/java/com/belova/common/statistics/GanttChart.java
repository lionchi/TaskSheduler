package com.belova.common.statistics;

import com.belova.entity.Task;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.category.IntervalCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class GanttChart {


    /**
     * Конструктор примера
     *
     * @param title заголовок примера
     */
    public GanttChart(final String title, List<Task> tasks) {
        DatasetGantt ds = new DatasetGantt();

        URL url = GanttChart.class.getClassLoader().getResource("img/organize.png");
        ImageIcon img = new ImageIcon(url);

        JFreeChart chart = createChart(ds.createDataset(tasks), "График выполнения");

        DateAxis axis = (DateAxis) chart.getCategoryPlot().getRangeAxis();
        DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        axis.setDateFormatOverride(sdf);

        chart.getCategoryPlot().setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 450));

        JFrame jFrame = new JFrame(title);
        jFrame.setTitle(title);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setLayout(new BorderLayout(0, 5));
        jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jFrame.setIconImage(img.getImage());
        jFrame.setAlwaysOnTop(true);
        jFrame.add(chartPanel, BorderLayout.CENTER);
        chartPanel.setMouseWheelEnabled(true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jFrame.add(panel, BorderLayout.SOUTH);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    /**
     * Метод создания диаграммы
     *
     * @param dataset набор данных
     * @return JFreeChart диаграмма Гантта
     */
    private JFreeChart createChart(final IntervalCategoryDataset dataset, final String title) {
        final JFreeChart chart = ChartFactory.createGanttChart(
                title,
                null,
                null,
                dataset,
                true,
                true,
                false
        );
        return chart;
    }
}
