import java.io.*;
import java.util.*;


public class Main {

    static int N;
    static int max;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        N = Integer.parseInt(br.readLine());

        int[][] board = new int [N][N];
        for(int i = 0; i < N; i++){
            StringTokenizer st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++){
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        dfs(0, board);
        System.out.println(max);
    }

    static void dfs(int cnt, int[][] board) {

        if(cnt >= 5) {
            max = Math.max(max, getmax(board));
            return;
        }

        // 4방향 (상하좌우)
        for(int i = 0; i < 4; i++){

            int[][] newBoard = copyBoard(board);
            move(i, newBoard);
            dfs(cnt+1, newBoard);

        }
    }
    static void  move(int dir, int[][] b) {
        for (int i = 0; i < N; i++) {
            Deque<Integer> q = new ArrayDeque<>();

            // 수집


            if (dir == 0) { // 상
                for (int j = 0; j < N; j++)
                    if (b[j][i] != 0) {
                        q.offer(b[j][i]);
                        b[j][i] = 0;
                    }
            } else if (dir == 1) { // 하
                for (int j = N - 1; j >= 0; j--)
                    if (b[j][i] != 0) {
                        q.offer(b[j][i]);
                        b[j][i] = 0;
                    }
            } else if (dir == 2) { // 좌
                for (int j = 0; j < N; j++)
                    if (b[i][j] != 0) {
                        q.offer(b[i][j]);
                        b[i][j] = 0;
                    }
            } else if (dir == 3) { // 우
                for (int j = N - 1; j >= 0; j--)
                    if (b[i][j] != 0) {
                        q.offer(b[i][j]);
                        b[i][j] = 0;
                    }
            }


            // 합치기
            List<Integer> merged = new ArrayList<>();
            while(!q.isEmpty()) {
                int cur = q.poll();
                if(!q.isEmpty() && cur == q.peek()) {
                    merged.add(cur * 2);
                    q.poll();
                } else {
                     merged.add(cur);
                }
            }

            // 채우기
            int idx = 0;

            if(dir == 0) { // 상
                for (int v : merged) {
                    b[idx++][i] = v;
                }
            }
            else if (dir == 1) {
                for (int v : merged) {
                    b[N - idx++ - 1][i] = v;
                }
            }
            else if (dir == 2) {
                for(int v : merged) {
                    b[i][idx++] = v;
                }
            }
            else if (dir == 3) {
                for(int v : merged) {
                    b[i][N - idx++ - 1] = v;
                }
            }




        }
    }

    static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    static int getmax(int[][] b) {
        int max = 0;
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                if(b[i][j] > max) {
                    max = b[i][j];
                }
            }
        }

        return max;
    }
}
