package models;

import java.util.*;

public class GreedyAlgorithm {

    // Lớp kết quả (Result) chứa thông tin về kết quả phân công công việc
    public static class Result {
        private final Map<Worker, Job> assignment; // Bản đồ lưu trữ phân công công việc (Worker -> Job)
        private final int totalTime;              // Tổng thời gian hoàn thành các công việc

        public Result(Map<Worker, Job> assignment, int totalTime) {
            this.assignment = assignment;
            this.totalTime = totalTime;
        }

        // Trả về bản đồ phân công công việc
        public Map<Worker, Job> getAssignment() {
            return assignment;
        }

        // Trả về tổng thời gian
        public int getTotalTime() {
            return totalTime;
        }
    }

    /**
     * Phương thức giải bài toán phân công công việc bằng thuật toán tham lam (Greedy Algorithm).
     * @param timeMatrix Ma trận thời gian, trong đó timeMatrix[workerId][jobId] là thời gian mà worker thực hiện job.
     * @param workers Danh sách công nhân (Worker).
     * @param jobs Danh sách công việc (Job).
     * @return Kết quả phân công công việc và tổng thời gian hoàn thành.
     */
    public static Result solve(Map<Integer, Map<Integer, Integer>> timeMatrix, List<Worker> workers, List<Job> jobs) {
        Map<Worker, Job> assignment = new HashMap<>(); // Lưu trữ kết quả phân công công việc
        Set<Integer> assignedJobs = new HashSet<>();  // Tập hợp các công việc đã được gán
        int totalTime = 0;                            // Biến lưu tổng thời gian thực hiện các công việc

        // Bước 1: Duyệt qua từng công nhân để phân công công việc
        for (Worker worker : workers) {
            int minTime = Integer.MAX_VALUE; // Lưu thời gian nhỏ nhất cho công việc phù hợp nhất
            Job chosenJob = null;           // Công việc được chọn cho công nhân này

            // Bước 2: Duyệt qua tất cả các công việc để tìm công việc phù hợp nhất
            for (Job job : jobs) {
                // Kiểm tra nếu công việc chưa được gán cho bất kỳ công nhân nào
                if (!assignedJobs.contains(job.getId())) {
                    int time = timeMatrix.get(worker.getId()).get(job.getId()); // Lấy thời gian thực hiện công việc
                    if (time < minTime) { // Nếu thời gian nhỏ hơn thời gian hiện tại, cập nhật lựa chọn
                        minTime = time;
                        chosenJob = job;
                    }
                }
            }

            // Bước 3: Gán công việc được chọn cho công nhân nếu tìm thấy
            if (chosenJob != null) {
                assignment.put(worker, chosenJob);         // Gán công việc cho công nhân
                assignedJobs.add(chosenJob.getId());       // Đánh dấu công việc này đã được gán
                totalTime += minTime;                     // Cộng thời gian thực hiện công việc vào tổng thời gian
            }
        }

        // Bước 4: Trả về kết quả gồm bản đồ phân công công việc và tổng thời gian
        return new Result(assignment, totalTime);
    }
}

