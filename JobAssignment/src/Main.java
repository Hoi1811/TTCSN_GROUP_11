import module.AssignmentSolver;
import module.ExcelReader;
import module.Job;
import module.Worker;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "D:\\Downloads\\2024HAUI\\TTCSN\\TTCSN_GROUP_11\\Book1.xlsx";

        // Danh sách worker và job
        List<Worker> workers = Arrays.asList(
                new Worker(1, "Worker A"),
                new Worker(2, "Worker B"),
                new Worker(3, "Worker C"),
                new Worker(4, "Worker D")
        );

        List<Job> jobs = Arrays.asList(
                new Job(101, "A"),
                new Job(102, "B"),
                new Job(103, "C"),
                new Job(104, "D")
        );

        // Đọc dữ liệu từ file Excel
        Map<Integer, Map<Integer, Integer>> timeMatrix = ExcelReader.readExcel(filePath, workers, jobs);

        // Phân chia công việc bằng Branch and Bound
        AssignmentSolver.Result result = AssignmentSolver.branchAndBound(timeMatrix, workers, jobs);

        // Hiển thị kết quả
        System.out.println("Phân chia công việc tối ưu:");
        result.getAssignment().forEach((worker, job) -> System.out.println(worker.getName() + " -> Job " + job.getName()));

        System.out.println("Tổng thời gian tối ưu: " + result.getTotalTime());
    }
}
