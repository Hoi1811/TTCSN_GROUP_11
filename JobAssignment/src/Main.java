import models.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class                        Main {
    public static void main(String[] args) throws IOException {
        String filePath = "D:\\Downloads\\2024HAUI\\TTCSN\\TTCSN_GROUP_11\\Book1.xlsx";
//        String filePath = "D:\\Project\\Book1.xlsx";
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

        // Lựa chọn thuật toán để giải bài toán
        Scanner scanner = new Scanner(System.in);
        System.out.println("Chọn thuật toán để giải bài toán phân công công việc:");
        System.out.println("1. Branch and Bound");
        System.out.println("2. Thuật toán Tham lam (Greedy Algorithm)");
        System.out.print("Nhập lựa chọn của bạn: ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            // Giải bằng Branch and Bound
            AssignmentSolver.Result result = AssignmentSolver.branchAndBound(timeMatrix, workers, jobs);

            // Hiển thị kết quả
            System.out.println("\nPhân công tối ưu sử dụng Branch and Bound:");
            result.getAssignment().forEach((worker, job) ->
                    System.out.println(worker.getName() + " -> " + job.getName())
            );
            System.out.println("Tổng thời gian tối ưu: " + result.getTotalTime());
        } else if (choice == 2) {
            // Giải bằng thuật toán tham lam
            GreedyAlgorithm.Result result = GreedyAlgorithm.solve(timeMatrix, workers, jobs);

            // Hiển thị kết quả
            System.out.println("\nPhân công công việc sử dụng Thuật toán Tham lam:");
            result.getAssignment().forEach((worker, job) ->
                    System.out.println(worker.getName() + " -> " + job.getName())
            );
            System.out.println("Tổng thời gian sử dụng Greedy: " + result.getTotalTime());
        } else {
            System.out.println("Lựa chọn không hợp lệ. Thoát chương trình.");
        }
    }
}
