#include <iostream>
#include <fstream>
#include <limits>
#include <string>

#define SIZE 100

// Cấu trúc PhanCong lưu trữ thông tin về phân công công việc cho mỗi công nhân
struct PhanCong {
    int CN; // Chỉ số công nhân (Worker ID)
    int CV; // Chỉ số công việc (Job ID)
    int TG; // Thời gian làm việc cho công việc đó (Work Time)
};

// Cấu trúc Data lưu trữ thông tin về thời gian và trạng thái phân công của mỗi công việc
struct Data {
    int TG;    // Thời gian để hoàn thành công việc (Time to Complete)
    bool STT;  // Trạng thái phân công: true nếu đã được phân công, false nếu chưa (Status)
};

/**
 * Hàm đọc dữ liệu từ file
 * @param filename: Tên file chứa dữ liệu
 * @param a: Ma trận lưu trữ thời gian làm việc của các công nhân cho các công việc
 * @param n: Số lượng công nhân và công việc
 * @return: Trả về true nếu đọc thành công, ngược lại trả về false
 */
bool ReadData(const std::string& filename, Data a[][SIZE], int& n) {
    std::ifstream file(filename.c_str()); // Tạo đối tượng file để đọc từ file
    if (!file.is_open()) { // Kiểm tra xem file có mở thành công không
        std::cerr << "Lỗi mở file!" << std::endl;
        return false;
    }

    file >> n; // Đọc số lượng công nhân và công việc từ file
    if(n > SIZE) { // Kiểm tra xem số lượng có vượt quá kích thước tối đa cho phép không
        std::cerr << "Số lượng công nhân/công việc vượt quá kích thước tối đa (" << SIZE << ")!" << std::endl;
        file.close(); // Đóng file trước khi thoát
        return false;
    }

    // Đọc ma trận thời gian làm việc và khởi tạo trạng thái chưa phân công
    for(int i = 0; i < n; ++i){
        for(int j = 0; j < n; ++j){
            file >> a[i][j].TG; // Đọc thời gian làm việc cho công nhân i và công việc j
            a[i][j].STT = false; // Khởi tạo trạng thái chưa phân công
        }
    }

    file.close(); // Đóng file sau khi đọc xong
    return true; // Trả về thành công
}

/**
 * Hàm in ma trận thời gian làm việc
 * @param a: Ma trận lưu trữ thời gian làm việc của các công nhân cho các công việc
 * @param n: Số lượng công nhân và công việc
 */
void PrintData(const Data a[][SIZE], int n){
    std::cout << "\nTime Matrix was given:\n";
    for(int i = 0; i < n; ++i){
        for(int j = 0; j < n; ++j){
            std::cout << a[i][j].TG << "\t"; // In thời gian làm việc của công nhân i cho công việc j
        }
        std::cout << "\n"; // Xuống dòng sau khi in xong một hàng
    }
}

/**
 * Hàm cập nhật trạng thái công việc đã được phân công
 * @param a: Ma trận lưu trữ trạng thái phân công của các công việc
 * @param n: Số lượng công nhân và công việc
 * @param j: Chỉ số công việc cần cập nhật trạng thái
 */
void Update_Work(Data a[][SIZE], int n, int j){
    for(int i = 0; i < n; ++i){
        a[i][j].STT = true; // Đánh dấu công việc j đã được phân công cho công nhân i
    }
}

/**
 * Hàm hoàn nguyên trạng thái công việc chưa được phân công
 * @param a: Ma trận lưu trữ trạng thái phân công của các công việc
 * @param n: Số lượng công nhân và công việc
 * @param j: Chỉ số công việc cần hoàn nguyên trạng thái
 */
void Revert_Work(Data a[][SIZE], int n, int j){
    for(int i = 0; i < n; ++i){
        a[i][j].STT = false; // Đánh dấu công việc j chưa được phân công cho công nhân i
    }
}

/**
 * Hàm tìm thời gian tối thiểu của một hàng chưa được phân công
 * @param a: Ma trận lưu trữ thời gian làm việc và trạng thái phân công
 * @param n: Số lượng công nhân và công việc
 * @param i: Chỉ số hàng cần tìm thời gian tối thiểu
 * @return: Thời gian tối thiểu của hàng i
 */
int Min_Of_Row(const Data a[][SIZE], int n, int i){
    int TGMin = std::numeric_limits<int>::max(); // Khởi tạo TGMin với giá trị lớn nhất của int
    for(int k = 0; k < n; ++k){
        if(!a[i][k].STT && a[i][k].TG < TGMin){
            TGMin = a[i][k].TG; // Cập nhật TGMin nếu tìm thấy giá trị nhỏ hơn và chưa được phân công
        }
    }
    return TGMin; // Trả về thời gian tối thiểu tìm được
}

/**
 * Hàm tính cận dưới (Lower Bound) của giải pháp hiện tại
 * @param a: Ma trận lưu trữ thời gian làm việc và trạng thái phân công
 * @param n: Số lượng công nhân và công việc
 * @param TTG: Tổng thời gian hiện tại của giải pháp
 * @param i: Chỉ số công nhân hiện tại
 * @return: Giá trị cận dưới của giải pháp
 */
int Lower_Bound(const Data a[][SIZE], int n, int TTG, int i){
    int CD = TTG; // Khởi tạo cận dưới bằng tổng thời gian hiện tại
    for(int k = i + 1; k < n; ++k){
        CD += Min_Of_Row(a, n, k); // Cộng thêm thời gian tối thiểu của các hàng còn lại
    }
    return CD; // Trả về cận dưới
}

/**
 * Hàm tạo nút gốc (Root Node) cho thuật toán Branch and Bound
 * @param a: Ma trận lưu trữ thời gian làm việc và trạng thái phân công
 * @param n: Số lượng công nhân và công việc
 * @param TTG: Tổng thời gian hiện tại của giải pháp (output)
 * @param CD: Cận dưới của giải pháp (output)
 * @param GiaNNTT: Giá trị tốt nhất hiện tại của giải pháp (output)
 */
void Create_Root_Node(const Data a[][SIZE], int n, int& TTG, int& CD, int& GiaNNTT){
    TTG = 0; // Khởi tạo tổng thời gian hiện tại bằng 0
    GiaNNTT = std::numeric_limits<int>::max(); // Khởi tạo giá trị tốt nhất bằng giá trị lớn nhất của int
    CD = Lower_Bound(a, n, TTG, -1); // Tính cận dưới cho nút gốc
}

/**
 * Hàm cập nhật giải pháp tốt nhất hiện tại
 * @param TTG: Tổng thời gian của giải pháp hiện tại
 * @param GiaNNTT: Giá trị tốt nhất hiện tại của giải pháp (output)
 * @param PA: Mảng lưu trữ giải pháp tốt nhất
 * @param x: Mảng lưu trữ giải pháp tạm thời
 * @param n: Số lượng công nhân và công việc
 */
void Update_Best_Solution(int TTG, int& GiaNNTT, PhanCong PA[], PhanCong x[], int n){
    if(GiaNNTT > TTG){ // Kiểm tra nếu tổng thời gian hiện tại tốt hơn giá trị tốt nhất
        GiaNNTT = TTG; // Cập nhật giá trị tốt nhất
        for(int i = 0; i < n; ++i){
            PA[i] = x[i]; // Sao chép giải pháp tạm thời vào giải pháp tốt nhất
        }
    }
}

/**
 * Hàm thực hiện thuật toán Branch and Bound để tìm giải pháp tối ưu
 * @param a: Ma trận lưu trữ thời gian làm việc và trạng thái phân công
 * @param i: Chỉ số công nhân hiện tại
 * @param TTG: Tổng thời gian hiện tại của giải pháp (output)
 * @param CD: Cận dưới của giải pháp (output)
 * @param GiaNNTT: Giá trị tốt nhất hiện tại của giải pháp (output)
 * @param x: Mảng lưu trữ giải pháp tạm thời
 * @param PA: Mảng lưu trữ giải pháp tốt nhất
 * @param n: Số lượng công nhân và công việc
 */
void Branch_And_Bound(Data a[][SIZE], int i, int& TTG, int& CD, int& GiaNNTT, 
                      PhanCong x[], PhanCong PA[], int n){
    if(i >= n){ // Nếu đã phân công xong tất cả công nhân
        Update_Best_Solution(TTG, GiaNNTT, PA, x, n); // Cập nhật giải pháp tốt nhất
        return; // Kết thúc nhánh hiện tại
    }

    for(int j = 0; j < n; ++j){ // Xét tất cả các công việc
        if(!a[i][j].STT){ // Nếu công việc j chưa được phân công
            TTG += a[i][j].TG; // Cộng thời gian làm việc của công nhân i cho công việc j vào tổng thời gian hiện tại
            CD = Lower_Bound(a, n, TTG, i); // Tính cận dưới cho giải pháp hiện tại

            if(CD < GiaNNTT){ // Nếu cận dưới còn khả thi (tốt hơn giá trị tốt nhất hiện tại)
                x[i].CN = i + 1; // Gán chỉ số công nhân
                x[i].CV = j + 1; // Gán chỉ số công việc
                x[i].TG = a[i][j].TG; // Gán thời gian làm việc cho công việc j

                Update_Work(a, n, j); // Cập nhật trạng thái công việc j đã được phân công

                Branch_And_Bound(a, i + 1, TTG, CD, GiaNNTT, x, PA, n); // Đệ quy cho công nhân tiếp theo

                Revert_Work(a, n, j); // Hoàn nguyên trạng thái công việc j
                TTG -= a[i][j].TG; // Trừ thời gian làm việc của công việc j khỏi tổng thời gian hiện tại
            }
            else{
                TTG -= a[i][j].TG; // Trừ thời gian làm việc của công việc j khỏi tổng thời gian hiện tại vì cắt tỉa nhánh này
            }
        }
    }
}

/**
 * Hàm in ra giải pháp tốt nhất tìm được
 * @param PA: Mảng lưu trữ giải pháp tốt nhất
 * @param n: Số lượng công nhân và công việc
 */
void Print_Solution(const PhanCong PA[], int n){
    int sum = 0; // Khởi tạo tổng thời gian làm việc
    std::cout << "\nBranch and Bound Algorithm Result:\n";
    std::cout << "Worker\tWork\tWork Time\n";
    for(int i = 0; i < n; ++i){
        std::cout << PA[i].CN << "\t" << PA[i].CV << "\t" << PA[i].TG << "\n"; // In thông tin phân công
        sum += PA[i].TG; // Cộng thời gian làm việc của từng công nhân
    }
    std::cout << "Total time is: " << sum << "\n"; // In tổng thời gian làm việc
}

int main(){
    Data a[SIZE][SIZE]; // Ma trận lưu trữ thời gian làm việc và trạng thái phân công
    int n, TTG, CD, GiaNNTT; // n: Số lượng công nhân và công việc, TTG: Tổng thời gian hiện tại, CD: Cận dưới, GiaNNTT: Giá trị tốt nhất hiện tại

    std::string filename = "PC_Lao_Dong.txt"; // Tên file chứa dữ liệu
    if(!ReadData(filename, a, n)){ // Gọi hàm đọc dữ liệu từ file
        return 1; // Kết thúc chương trình nếu đọc file thất bại
    }

    PhanCong PA[SIZE]; // Mảng lưu trữ giải pháp tốt nhất
    PhanCong x[SIZE];  // Mảng lưu trữ giải pháp tạm thời

    // Khởi tạo PA và x với giá trị mặc định
    for(int i = 0; i < n; ++i){
        PA[i].CN = 0; // Gán công nhân 0 (chưa được phân công)
        PA[i].CV = 0; // Gán công việc 0 (chưa được phân công)
        PA[i].TG = 0; // Gán thời gian làm việc 0

        x[i].CN = 0; // Gán công nhân 0 (chưa được phân công)
        x[i].CV = 0; // Gán công việc 0 (chưa được phân công)
        x[i].TG = 0; // Gán thời gian làm việc 0
    }

    PrintData(a, n); // In ma trận thời gian làm việc
    Create_Root_Node(a, n, TTG, CD, GiaNNTT); // Tạo nút gốc cho thuật toán Branch and Bound
    Branch_And_Bound(a, 0, TTG, CD, GiaNNTT, x, PA, n); // Gọi hàm Branch and Bound để tìm giải pháp tối ưu
    Print_Solution(PA, n); // In ra giải pháp tốt nhất tìm được

    return 0; // Kết thúc chương trình
}
