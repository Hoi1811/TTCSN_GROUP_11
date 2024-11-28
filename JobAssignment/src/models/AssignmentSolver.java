package models;

import java.util.*;

public class AssignmentSolver {
    // Lớp Result chứa kết quả của bài toán
    public static class Result {
        private Map<Worker, Job> assignment; // Kết quả ánh xạ Worker -> Job
        private int totalTime; // Tổng thời gian tối ưu cho tất cả Worker

        // Constructor: Lưu kết quả ánh xạ và tổng thời gian
        public Result(Map<Worker, Job> assignment, int totalTime) {
            this.assignment = assignment;
            this.totalTime = totalTime;
        }

        // Getter để lấy ánh xạ Worker -> Job
        public Map<Worker, Job> getAssignment() {
            return assignment;
        }

        // Getter để lấy tổng thời gian tối ưu
        public int getTotalTime() {
            return totalTime;
        }
    }

    /**
     * Phương thức giải quyết bài toán phân công công việc (Assignment Problem)
     * bằng cách sử dụng thuật toán Branch and Bound.
     *
     * @param timeMatrix Ma trận thời gian công việc (Worker ID -> Job ID -> Time)
     * @param workers Danh sách Worker
     * @param jobs Danh sách Job
     * @return Kết quả chứa ánh xạ Worker -> Job và tổng thời gian tối ưu
     */
    public static Result branchAndBound(
            Map<Integer, Map<Integer, Integer>> timeMatrix,
            List<Worker> workers,
            List<Job> jobs
    ) {
        int n = workers.size(); // Số lượng Worker
        int m = jobs.size();   // Số lượng Job

        // Kiểm tra nếu số Worker và Job không bằng nhau
        if (n != m) {
            throw new IllegalArgumentException("Số lượng Worker và Job phải bằng nhau");
        }

        // Chuyển đổi ma trận thời gian thành ma trận 2D `costMatrix`
        int[][] costMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            int workerId = workers.get(i).getId(); // Lấy ID của Worker
            Map<Integer, Integer> jobTimes = timeMatrix.get(workerId); // Lấy thời gian tương ứng Worker -> Job
            for (int j = 0; j < m; j++) {
                int jobId = jobs.get(j).getId(); // Lấy ID của Job
                // Lấy thời gian từ `timeMatrix` hoặc gán giá trị vô cực nếu không tìm thấy
                costMatrix[i][j] = jobTimes.getOrDefault(jobId, Integer.MAX_VALUE);
            }
        }

        // Gọi thuật toán Branch and Bound để tìm ánh xạ tối ưu
        Map<Integer, Integer> assignment = branchAndBoundAlgorithm(costMatrix);

        // Tính tổng thời gian tối ưu và tạo ánh xạ Worker -> Job
        int totalTime = 0;
        Map<Worker, Job> resultAssignment = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : assignment.entrySet()) {
            int workerIdx = entry.getKey(); // Chỉ số Worker trong mảng
            int jobIdx = entry.getValue(); // Chỉ số Job trong mảng

            // Cộng thời gian tối ưu
            totalTime += costMatrix[workerIdx][jobIdx];

            // Ánh xạ Worker -> Job
            Worker worker = workers.get(workerIdx);
            Job job = jobs.get(jobIdx);
            resultAssignment.put(worker, job);
        }

        // Trả về kết quả gồm ánh xạ và tổng thời gian
        return new Result(resultAssignment, totalTime);
    }

    /**
     * Thuật toán Branch and Bound đơn giản để phân công công việc tối ưu.
     * Chọn công việc có chi phí nhỏ nhất cho mỗi Worker.
     *
     * @param costMatrix Ma trận chi phí (n x n)
     * @return Ánh xạ Worker Index -> Job Index
     */
    private static Map<Integer, Integer> branchAndBoundAlgorithm(int[][] costMatrix) {
        int n = costMatrix.length; // Số Worker/Job
        Map<Integer, Integer> assignment = new HashMap<>(); // Lưu ánh xạ Worker -> Job
        boolean[] assignedJobs = new boolean[n]; // Đánh dấu công việc đã được phân công

        // Lặp qua từng Worker để gán công việc tối ưu
        for (int workerIdx = 0; workerIdx < n; workerIdx++) {
            int minCost = Integer.MAX_VALUE; // Chi phí nhỏ nhất cho Worker hiện tại
            int bestJobIdx = -1; // Chỉ số Job có chi phí nhỏ nhất

            // Tìm Job với chi phí nhỏ nhất chưa được gán
            for (int jobIdx = 0; jobIdx < n; jobIdx++) {
                if (!assignedJobs[jobIdx] && costMatrix[workerIdx][jobIdx] < minCost) {
                    minCost = costMatrix[workerIdx][jobIdx];
                    bestJobIdx = jobIdx;
                }
            }

            // Gán Job cho Worker
            if (bestJobIdx != -1) {
                assignedJobs[bestJobIdx] = true; // Đánh dấu Job đã được gán
                assignment.put(workerIdx, bestJobIdx); // Lưu ánh xạ Worker -> Job
            }
        }

        return assignment; // Trả về ánh xạ tối ưu
    }
}
