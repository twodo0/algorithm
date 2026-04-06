import java.io.*;
import java.util.*;

public class Main {

    static int Q;
    static int N;
    static int[] house = new int[20001];
    static int idx;

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        Q = Integer.parseInt(br.readLine());

        for(int i = 0; i < Q; i++){
            StringTokenizer st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            switch(cmd) {
                case 100:
                    N = Integer.parseInt(st.nextToken());
                    house[0] = 0;
                    idx = N + 1;

                    for(int j = 1; j <= N; j++){
                        house[j] = Integer.parseInt(st.nextToken());
                    }
                    idx = N + 1;
                    break;

                case 200:
                    int p = Integer.parseInt(st.nextToken());
                    construct(p);
                    break;

                case 300:
                    int q = Integer.parseInt(st.nextToken());
                    destroy(q);
                    break;

                case 400:
                    int r = Integer.parseInt(st.nextToken());
                    sb.append(scout(r)).append("\n");
                    break;

            }
        }

    }

    static void construct(int p) {
        house[idx++] = p;
    }

    static void destroy(int q) {
        house[q] = -1;
    }

    static int scout(int ants) {
        int l = 0; // 최소 거리
        int r = 1_000_000_000; // 최대 거리

        while(l < r) {
            int mid = (l + r) / 2;
            int required = placeAnts(mid);

            if(ants < required) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }

        return l;


    }

    static int placeAnts(int dist) {
        int cnt = 1;

        int prevIndex = 0;
        int prevX = 0;

        for(int i = 1; i < idx; i++){

            int x = house[i];

            // 철거된 집일 경우에 고려 X
            if(house[i] == -1){
                continue;
            }

            if(prevIndex == 0) {
                prevIndex = i;
                prevX = house[i];
                continue;
            }

            // 이전 개미가 커버할 수 있는 영역 초과 시 새로운 영역 배치
            if(x - prevX > dist) {
                prevIndex = i;
                prevX = x;
                cnt++;
            }
        }

        return cnt;
    }
}