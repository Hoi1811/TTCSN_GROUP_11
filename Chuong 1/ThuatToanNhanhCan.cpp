#include <stdio.h>
#define size 100

typedef struct{
	int CN,CV, TG;
}phan_cong;

typedef struct{
	int TG, STT;
}Data;

void ReadData( Data a[][size], int *n){
	FILE *f;
	f= fopen("PC_Lao_Dong.txt", "r");
	if(f==NULL){
		printf("Open file error!");
		return;
	}
	fscanf(f, "%d", n);
	int i, j;
	for(int i = 0; i < *n; i++){
		for(j = 0; j < *n;j++){
			fscanf(f, "%d", &a[i][j].TG);
			a[i][j].STT = 0;
		}
	}
	fclose(f);
}
void PrintData(Data a[size][size], int n){
    int i, j;
    printf("\nTime Matrix was given:\n");
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){ 
            printf("%3d ", a[i][j].TG);
        }
        printf("\n");  
    }
}


void Update_Work(Data a[][size], int n, int j){
	int i;
	for(i = 0 ; i < n; i++){
		a[i][j].STT = 1;
	}
}

void Revert_Work(Data a[][size], int n, int j){
	int i;
	for(i = 0; i < n; i++){
		a[i][j].STT = 0;
	}
}

int Min_Of_Row(Data a[][size], int n, int i){
	int TGMin = 32767, k;
	for(k = 0; k < n;k++){
		if(a[i][k].STT == 0 && a[i][k].TG < TGMin){
			TGMin = a[i][k].TG;
		}
	}
	return TGMin;
}
int Lower_Bound(Data a[][size], int n, int TTG, int i){
	int CD = TTG, k;
	for(k = i + 1; k < n; k++){
		CD = CD + Min_Of_Row(a, n, k);
	}
	return CD;
}

void Create_Root_Node(Data a[][size], int n, int *TTG, int *CD, int *GiaNNTT){
	*TTG = 0;
	*GiaNNTT = 32767;
	*CD = Lower_Bound(a, n, *TTG, - 1);
}

void Update_Best_Solution(int TTG, int *GiaNNTT, phan_cong x[], phan_cong PA[], int n){
	int i;
	if(*GiaNNTT > TTG){
		*GiaNNTT = TTG;
		for(i = 0; i < n; i++){
			PA[i] = x[i];
		}
	}
}

void Branch_And_Bound(Data a[][size], int i, int *TTG, int *CD, int *GiaNNTT, phan_cong x[], phan_cong PA[], int n){
    int j; // j công vi?c du?c ch?n
    for(j = 0; j < n; j++){ // Xét t?t c? các công vi?c
        if(a[i][j].STT == 0){
            *TTG += a[i][j].TG;
            *CD = Lower_Bound(a, n, *TTG, i);
            if(*CD < *GiaNNTT){ // N?u có CD < GiaNNTT thì ti?p t?c
                x[i].CN = i + 1;
                x[i].CV = j + 1;
                x[i].TG = a[i][j].TG;
                Update_Work(a, n, j);
                if(i == n - 1){
                    Update_Best_Solution(*TTG, GiaNNTT, PA, x, n); // C?p nh?t gi?i pháp t?t nh?t
                }
                else{
                    Branch_And_Bound(a, i + 1, TTG, CD, GiaNNTT, x, PA, n); // Ð? quy cho công vi?c ti?p theo
                }
                // Quay lui d? xét công vi?c khác
                Revert_Work(a, n, j);
                *TTG -= a[i][j].TG;
            }
            else{
                // Không làm gì khi c?t t?a
                *TTG -= a[i][j].TG;
                Revert_Work(a, n, j);
            }
        }
    }
}

void Print_Solution(phan_cong PA[], int n){
	int i, sum = 0;
	printf("\nBranch and Bound Algorithm Result: \n");
	printf("Worker  Work  Work Time\n");
	for(i = 0 ; i < n; i++){
		printf("%4d  %4d%15d\n", PA[i].CN, PA[i].CV, PA[i].TG);
		sum += PA[i].TG;
	}
	printf("Total time is: %d\n", sum);
}
int main(){
	Data a[size][size];
	int n, TTG, CD, GiaNNTT;
	ReadData(a, &n);
	phan_cong PA[n], x[n];
	PrintData(a, n);
	Create_Root_Node(a,n,&TTG, &CD, &GiaNNTT);
	Branch_And_Bound(a,0,&TTG, &CD, &GiaNNTT, x, PA, n);
	Print_Solution(PA, n);
	
	return 0;
}	
