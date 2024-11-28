package module;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {
    public static Map<Integer, Map<Integer, Integer>> readExcel(String filePath,
                                                                List<Worker> workers, List<Job> jobs) throws IOException {
        Map<Integer, Map<Integer, Integer>> timeMatrix = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            // Lấy danh sách Worker IDs (cột A)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int workerId = (int) row.getCell(0).getNumericCellValue();
                    if (workers.stream().noneMatch(w -> w.getId() == workerId)) {
                        throw new IllegalArgumentException("Worker ID " + workerId + " is missing in List<Worker>");
                    }
                    timeMatrix.put(workerId, new HashMap<>());
                }
            }

            // Lấy danh sách Job IDs (hàng 1)
            Row jobRow = sheet.getRow(0);
            if (jobRow != null) {
                for (int j = 1; j < jobRow.getLastCellNum(); j++) {
                    int jobId = (int) jobRow.getCell(j).getNumericCellValue();
                    if (jobs.stream().noneMatch(job -> job.getId() == jobId)) {
                        throw new IllegalArgumentException("Job ID " + jobId + " is missing in List<Job>");
                    }
                }
            }

            // Đọc ma trận thời gian làm việc (từ B2)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int workerId = (int) row.getCell(0).getNumericCellValue();
                    for (int j = 1; j < row.getLastCellNum(); j++) {
                        int jobId = (int) jobRow.getCell(j).getNumericCellValue();
                        int time = (int) row.getCell(j).getNumericCellValue();
                        timeMatrix.get(workerId).put(jobId, time);
                    }
                }
            }
        }

        return timeMatrix;
    }
}

