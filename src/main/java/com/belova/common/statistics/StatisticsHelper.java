package com.belova.common.statistics;

import com.haulmont.yarg.formatters.factory.DefaultFormatterFactory;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.SqlDataLoader;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import com.haulmont.yarg.reporting.Reporting;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import com.haulmont.yarg.structure.ReportOutputType;
import com.haulmont.yarg.structure.impl.BandBuilder;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportTemplateBuilder;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsHelper {

    private static FileSystemView fw;

    static {
        JFileChooser fr = new JFileChooser();
        fw = fr.getFileSystemView();
    }

    public static void generateStatisticForAdmin(DataSource dataSource, String templateUrl) {
        try {
            ReportBuilder reportBuilder = new ReportBuilder();
            String date = dateToString();
            ReportTemplateBuilder reportTemplateBuilder = new ReportTemplateBuilder()
                    .documentPath(templateUrl)
                    .documentName(String.format("Statistics %s.xls", date))
                    .outputType(ReportOutputType.xls)
                    .readFileFromPath();
            reportBuilder.template(reportTemplateBuilder.build());

            BandBuilder bandBuilder = new BandBuilder();
            ReportBand header = bandBuilder.name("Header")
                    .build();
            ReportBand staff = bandBuilder.name("Statistics")
                    .query("Statistics", "select fio, department, quantity_fulfilled, quantity_unfulfilled from user_and_task", "sql")
                    .build();
            ReportBand header2 = bandBuilder.name("Header2")
                    .build();
            ReportBand debtor = bandBuilder.name("Debtor")
                    .query("Debtor", "select fio, quantity_unfulfilled from user_and_task where quantity_unfulfilled > 0", "sql")
                    .build();
            reportBuilder.band(header).band(staff).band(header2).band(debtor);
            Report report = reportBuilder.build();

            Reporting reporting = new Reporting();
            reporting.setFormatterFactory(new DefaultFormatterFactory());
            reporting.setLoaderFactory(
                    new DefaultLoaderFactory().setSqlDataLoader(new SqlDataLoader(dataSource)));

            ReportOutputDocument reportOutputDocument = reporting.runReport(
                    new RunParams(report), new FileOutputStream(String.format("%s\\Statistics %s.xls", fw.getDefaultDirectory(), date)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void generateStatisticForLeader(DataSource dataSource, String department, String templateUrl) {
        try {
            ReportBuilder reportBuilder = new ReportBuilder();
            String date = dateToString();
            ReportTemplateBuilder reportTemplateBuilder = new ReportTemplateBuilder()
                    .documentPath(templateUrl)
                    .documentName(String.format("Statistics %s.xls", date))
                    .outputType(ReportOutputType.xls)
                    .readFileFromPath();
            reportBuilder.template(reportTemplateBuilder.build());

            BandBuilder bandBuilder = new BandBuilder();
            ReportBand header = bandBuilder.name("Header")
                    .build();
            ReportBand staff = bandBuilder.name("Statistics")
                    .query("Statistics", String.format("select fio, department, quantity_fulfilled, " +
                            "quantity_unfulfilled from user_and_task where department = '%s'", department), "sql")
                    .build();
            ReportBand header2 = bandBuilder.name("Header2")
                    .build();
            ReportBand debtor = bandBuilder.name("Debtor")
                    .query("Debtor", String.format("select fio, quantity_unfulfilled from user_and_task where quantity_unfulfilled > 0 and department = '%s'", department), "sql")
                    .build();
            reportBuilder.band(header).band(staff).band(header2).band(debtor);
            Report report = reportBuilder.build();

            Reporting reporting = new Reporting();
            reporting.setFormatterFactory(new DefaultFormatterFactory());
            reporting.setLoaderFactory(
                    new DefaultLoaderFactory().setSqlDataLoader(new SqlDataLoader(dataSource)));

            ReportOutputDocument reportOutputDocument = reporting.runReport(
                    new RunParams(report), new FileOutputStream(String.format("%s\\Statistics %s.xls", fw.getDefaultDirectory(), date)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String dateToString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}
