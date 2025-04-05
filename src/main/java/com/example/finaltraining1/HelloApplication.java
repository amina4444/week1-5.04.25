package com.example.finaltraining1;

import javafx.application.Application;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.HLineTo;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.application.Application.launch;

public class HelloApplication extends Application {
    private static ArrayList<Product> products = new ArrayList<>();
    private ComboBox<Integer> yearSelector = new ComboBox<>();
    private LineChart<String, Number> chart;

    @Override
    public void start( Stage stage) throws IOException {
        VBox root = new VBox();
         Button button = new Button("загрузите файл");
         yearSelector.setPromptText("Выберите год");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Месяц");
        //xAxis.setTickLabelRotation(45);
        //xAxis.setTickLabelFont(Font.font("System",10 ));
        yAxis.setLabel("Прибыль");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Продажи по месяцам");


         button.setOnAction(e -> {
             FileChooser fileChooser = new FileChooser();
             fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
             fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel File", "*.xlsx"));
             File selectedFile = fileChooser.showOpenDialog(stage);
             if (selectedFile != null) {
             try {
                 readExel(selectedFile);
                 updateYearSelector();
             } catch (IOException ex) {
                 ex.printStackTrace();
             }
         }
         });
        yearSelector.setOnAction(e -> {
            Integer selectedYear = yearSelector.getValue();
            if (selectedYear != null) {
                updateChart(selectedYear);
            }
        });
        root.getChildren().addAll(button, yearSelector, chart);

        Scene scene = new Scene(root,600,360);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    private void updateYearSelector() {
        Set<Integer> years = products.stream()
                .map(p -> LocalDate.parse(p.getDate()))
                .map(LocalDate::getYear)
                .collect(Collectors.toSet());

        List<Integer> sortedYears = new ArrayList<>(years);
        Collections.sort(sortedYears);
        yearSelector.getItems().setAll(sortedYears);

        if (!sortedYears.isEmpty()) {
            yearSelector.setValue(sortedYears.get(0));
            updateChart(sortedYears.get(0));
        }
    }
    private void updateChart(int year) {
        Map<Integer, Double> monthlyProfit = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyProfit.put(i, 0.0);
        }
        for (Product product : products) {
            LocalDate date = LocalDate.parse(product.getDate());
            if (date.getYear() == year) {
                int month = date.getMonthValue();
                monthlyProfit.put(month, monthlyProfit.get(month) + product.getFinalPrice());
            }
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("profit" + year);

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault());

        for (int i = 1; i <= 12; i++) {
            String monthName = LocalDate.of(year, i, 1).format(monthFormatter);
            double profit = monthlyProfit.get(i);
            series.getData().add(new XYChart.Data<>(monthName, profit));
        }

        chart.getData().clear();
        chart.getData().add(series);
        chart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

    }


    public static void readExel(File file) throws IOException, InvalidFormatException {
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        products.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            int id = (int) row.getCell(0).getNumericCellValue();
            String name = row.getCell(1).getStringCellValue();
            double price = row.getCell(2).getNumericCellValue();
            int quantity = (int) row.getCell(3).getNumericCellValue();
            double finalPrice = row.getCell(4).getNumericCellValue();

            Cell dateCell = row.getCell(5);
            String dateStr;
            if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                LocalDate localDate = dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                dateStr = localDate.toString();
            } else {
                dateStr = row.getCell(5).getStringCellValue();
            }


            Product product = new Product(id, name, price, quantity, finalPrice,dateStr);
            products.add(product);
        }
        workbook.close();
    }

    public static void main(String[] args) {
        launch();
    }

}