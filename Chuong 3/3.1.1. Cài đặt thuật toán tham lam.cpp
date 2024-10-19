#include <iostream>
#include <vector>
#include <limits>

using namespace std;

// Tìm chỉ số công việc có thời gian ngắn nhất chưa được gán
int findMinIndex(const vector<int>& arr, const vector<bool>& assigned) {
    int minValue = numeric_limits<int>::max();
    int minIndex = -1;
    for (int i = 0; i < arr.size(); ++i) {
        if (!assigned[i] && arr[i] < minValue) {
            minValue = arr[i];
            minIndex = i;
        }
    }
    return minIndex;
}

// Phân công công việc theo thuật toán tham lam
int greedyAssignment(const vector<vector<int>>& cost, vector<int>& assignment) {
    int n = cost.size();
    vector<bool> assigned(n, false); // Đánh dấu công việc đã được gán
    int totalCost = 0; // Biến lưu tổng thời gian

    for (int i = 0; i < n; ++i) {
        // Tìm công việc có thời gian ngắn nhất cho công nhân thứ i
        int job = findMinIndex(cost[i], assigned);
        assignment[i] = job; // Gán công việc đó cho công nhân thứ i
        assigned[job] = true; // Đánh dấu công việc đã được chọn
        totalCost += cost[i][job]; // Cộng dồn thời gian thực hiện công việc
    }

    return totalCost; // Trả về tổng thời gian
}

int main() {
    // Thời gian hoàn thành công việc của các công nhân
    vector<vector<int>> cost = {
        {10, 19, 8, 15},
        {10, 18, 7, 17},
        {13, 16, 9, 14},
        {12, 19, 13, 19}
    };

    int n = cost.size();
    vector<int> assignment(n, -1); // Ghi lại phân công
    int totalTime = greedyAssignment(cost, assignment);

    // Xuất kết quả
    cout << "Cac cong nhan duoc phan cong:" << endl;
    for (int i = 0; i < n; ++i) {
        cout << "Cong nhan " << i + 1 << " -> Cong viec " << assignment[i] + 1
             << " voi thoi gian: " << cost[i][assignment[i]] << endl;
    }

    // Xuất tổng thời gian
    cout << "Tong thoi gian: " << totalTime << endl;

    return 0;
}
