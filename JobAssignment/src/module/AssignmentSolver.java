package module;

import java.util.*;

public class AssignmentSolver {
    public static class Result {
        private Map<Worker, Job> assignment;
        private int totalTime;

        public Result(Map<Worker, Job> assignment, int totalTime) {
            this.assignment = assignment;
            this.totalTime = totalTime;
        }

        public Map<Worker, Job> getAssignment() {
            return assignment;
        }

        public int getTotalTime() {
            return totalTime;
        }
    }

    public static Result branchAndBound(
            Map<Integer, Map<Integer, Integer>> timeMatrix,
            List<Worker> workers,
            List<Job> jobs
    ) {
        int n = workers.size();
        int m = jobs.size();

        if (n != m) {
            throw new IllegalArgumentException("Số lượng Worker và Job phải bằng nhau");
        }

        // Chuyển đổi thời gian thành ma trận 2D
        int[][] costMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            int workerId = workers.get(i).getId();
            Map<Integer, Integer> jobTimes = timeMatrix.get(workerId);
            for (int j = 0; j < m; j++) {
                int jobId = jobs.get(j).getId();
                costMatrix[i][j] = jobTimes.getOrDefault(jobId, Integer.MAX_VALUE);
            }
        }

        // Thuật toán Branch and Bound
        Map<Integer, Integer> assignment = branchAndBoundAlgorithm(costMatrix);

        // Tính tổng thời gian tối ưu
        int totalTime = 0;

        // Tạo kết quả ánh xạ Worker -> Job
        Map<Worker, Job> resultAssignment = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : assignment.entrySet()) {
            int workerIdx = entry.getKey();
            int jobIdx = entry.getValue();
            totalTime += costMatrix[workerIdx][jobIdx];

            Worker worker = workers.get(workerIdx);
            Job job = jobs.get(jobIdx);
            resultAssignment.put(worker, job);
        }

        return new Result(resultAssignment, totalTime);
    }

    private static Map<Integer, Integer> branchAndBoundAlgorithm(int[][] costMatrix) {
        int n = costMatrix.length;
        Map<Integer, Integer> assignment = new HashMap<>();
        boolean[] assignedJobs = new boolean[n];

        for (int workerIdx = 0; workerIdx < n; workerIdx++) {
            int minCost = Integer.MAX_VALUE;
            int bestJobIdx = -1;

            for (int jobIdx = 0; jobIdx < n; jobIdx++) {
                if (!assignedJobs[jobIdx] && costMatrix[workerIdx][jobIdx] < minCost) {
                    minCost = costMatrix[workerIdx][jobIdx];
                    bestJobIdx = jobIdx;
                }
            }

            if (bestJobIdx != -1) {
                assignedJobs[bestJobIdx] = true;
                assignment.put(workerIdx, bestJobIdx);
            }
        }

        return assignment;
    }
}
