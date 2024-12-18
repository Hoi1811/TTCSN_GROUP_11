package models;

import java.util.*;

public class AssignmentSolver {
    // Lớp Result chứa kết quả của bài toán
    public static class Result {
        private Map<Worker, Job> assignment; // Kết quả ánh xạ Worker -> Job
        private int totalTime; // Tổng thời gian tối ưu cho tất cả Worker

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

    // Lớp State đại diện cho trạng thái trong thuật toán Branch and Bound
    private static class State implements Comparable<State> {
        int level; // Số worker đã được gán công việc
        int cost; // Tổng chi phí tạm thời
        int lowerBound; // Ràng buộc dưới của trạng thái
        boolean[] assignedJobs; // Trạng thái các công việc đã được gán
        List<Integer> assignment; // Lưu công việc đã gán cho từng worker

        public State(int level, int cost, int lowerBound, boolean[] assignedJobs, List<Integer> assignment) {
            this.level = level;
            this.cost = cost;
            this.lowerBound = lowerBound;
            this.assignedJobs = assignedJobs.clone();
            this.assignment = new ArrayList<>(assignment);
        }

        @Override
        public int compareTo(State other) {
            return Integer.compare(this.lowerBound, other.lowerBound);
        }
    }

    public static Result branchAndBound(
            Map<Integer, Map<Integer, Integer>> timeMatrix,
            List<Worker> workers,
            List<Job> jobs
    ) {
        int n = workers.size();
        if (n != jobs.size()) {
            throw new IllegalArgumentException("Số lượng Worker và Job phải bằng nhau");
        }

        // Chuyển đổi ma trận thời gian thành ma trận chi phí
        int[][] costMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            int workerId = workers.get(i).getId();
            Map<Integer, Integer> jobTimes = timeMatrix.get(workerId);
            for (int j = 0; j < n; j++) {
                int jobId = jobs.get(j).getId();
                costMatrix[i][j] = jobTimes.getOrDefault(jobId, Integer.MAX_VALUE);
            }
        }

        PriorityQueue<State> pq = new PriorityQueue<>();
        boolean[] initialAssignedJobs = new boolean[n];
        List<Integer> initialAssignment = new ArrayList<>(Collections.nCopies(n, -1));

        // Tính ràng buộc dưới ban đầu
        int initialLowerBound = calculateLowerBound(costMatrix, initialAssignedJobs);
        State initialState = new State(0, 0, initialLowerBound, initialAssignedJobs, initialAssignment);
        pq.add(initialState);

        int optimalCost = Integer.MAX_VALUE;
        List<Integer> optimalAssignment = null;

        while (!pq.isEmpty()) {
            State currentState = pq.poll();
            // loại bỏ các nhanh co can duoi khong toi uu
            if (currentState.lowerBound >= optimalCost) {
                continue;
            }
            // kiem tra da xet het chua + cap nhat ket qua toi uu
            if (currentState.level == n) {
                optimalCost = currentState.cost;
                optimalAssignment = currentState.assignment;
                continue;
            }

            for (int job = 0; job < n; job++) {
                if (!currentState.assignedJobs[job]) {
                    boolean[] nextAssignedJobs = currentState.assignedJobs.clone();
                    nextAssignedJobs[job] = true;
                    List<Integer> nextAssignment = new ArrayList<>(currentState.assignment);
                    nextAssignment.set(currentState.level, job);

                    int nextCost = currentState.cost + costMatrix[currentState.level][job];
                    // kiem tra ràng buộc dưới
                    int nextLowerBound = calculateLowerBound(costMatrix, nextAssignedJobs) + nextCost;
                    // nếu ràng buộc dưới nhỏ hơn giá trị tối ưu them vao hang doi
                    if (nextLowerBound < optimalCost) {
                        State nextState = new State(
                                currentState.level + 1,
                                nextCost,
                                nextLowerBound,
                                nextAssignedJobs,
                                nextAssignment
                        );
                        pq.add(nextState);
                    }
                }
            }
        }

        Map<Worker, Job> resultAssignment = new HashMap<>();
        for (int i = 0; i < n; i++) {
            resultAssignment.put(workers.get(i), jobs.get(optimalAssignment.get(i)));
        }

        return new Result(resultAssignment, optimalCost);
    }

    private static int calculateLowerBound(int[][] costMatrix, boolean[] assignedJobs) {
        int n = costMatrix.length;  // Số lượng công nhân
        int lowerBound = 0;  // Khởi tạo giá trị ràng buộc dưới

        // Duyệt qua từng công nhân
        for (int i = 0; i < n; i++) {
            int minCost = Integer.MAX_VALUE;  // Tìm chi phí nhỏ nhất cho công nhân i
            for (int j = 0; j < n; j++) {
                if (!assignedJobs[j]) {  // Nếu công việc j chưa được phân công
                    minCost = Math.min(minCost, costMatrix[i][j]);  // Lấy chi phí nhỏ nhất
                }
            }
            lowerBound += minCost;  // Cộng dồn vào ràng buộc dưới
        }

        return lowerBound;
    }

}
