import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

public class Main {

    static int N, K, L;

    static int[][] A; // 먼지/물건
    static int[][] B; // 로봇 점유

    static class Robot {
        int id, r, c;
        Robot(int id, int r, int c) {
            this.id = id;
            this.r = r;
            this.c = c;
        };
    }

    static List<Robot> robots = new ArrayList<>();

    // 4방 (좌, 상, 우, 하) + (자기칸)
    static int[] dx4 = {0, -1, 0, 1};
    static int[] dy4 = {-1, 0, 1, 0};
    static int[] sx = {0, -1, 0, 1, 0}; // 청소 계산용 (자기칸 포함)
    static int[] sy = {-1, 0, 1, 0, 0};

    static boolean inRange(int r, int c) {
        return r >= 0 && r < N && c >= 0 && c < N;
    }

    // 로봇 이동: 현재 칸이 오염이면 이동하지 않음. 아니면 BFS로 최단 오염칸으로.
    static void move(Robot rb) {
        if(A[rb.r][rb.c] > 0) return;

        Deque<int[]> q = new ArrayDeque<>();
        int[][] dist = new int[N][N];
        for(int[] row : dist) Arrays.fill(row, -1);

        q.add(new int[] {rb.r, rb.c});
        dist[rb.r][rb.c] = 0;

        int bestDist = -1;
        int bestR = -1, bestC = -1;

        while(!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1];

            if(bestDist != -1 && dist[r][c] > bestDist) break; // 최단거리 초과 시 탐색 종료

            for (int d = 0; d < 4; d++) {
                int nr = r + dx4[d], nc = c + dy4[d];
                if(!inRange(nr, nc)) continue;
                if(dist[nr][nc] != -1) continue; // 이미 방문
                if(A[nr][nc] < 0) continue; // 물건
                if(B[nr][nc] != -1) continue; // 다른 로봇

                dist[nr][nc] = dist[r][c] + 1;
                q.add(new int[]{nr, nc});

                if(A[nr][nc] > 0) {
                    if(bestDist == -1 || bestDist == dist[nr][nc]) {
                        if(bestDist == -1) {
                            bestDist = dist[nr][nc];
                            bestR = nr; bestC = nc;
                        } else {
                            // (행, 열) 사전순 최소 유지
                            if(nr < bestR || (nr == bestR && nc < bestC)) {
                                bestR = nr; bestC = nc;
                            }
                        }
                    }
                }
            }
        }

        // 목적지 확정 시 B 갱신
        if(bestDist != -1) {
            B[rb.r][rb.c] = -1;
            rb.r = bestR; rb.c = bestC;
            B[rb.r][rb.c] = rb.id;
        }
    }

    // 청소: 4방 중 1방향 제외 → 자기칸 + 나머지 3방의 먼지를 각 칸 최대 20만큼 제거
    static void cleanRobot(Robot rb) {
        int bestNoDir = -1;
        int bestSum = 0;

        for(int no = 0; no < 4; no++) {
            int s = 0;
            for (int k = 0; k < 5; k++) {
                int rr = rb.r + sx[k], cc = rb.c + sy[k];
                if(!inRange(rr, cc)) continue;
                if(k < 4 && k == no) continue; // 제외 방향
                if(A[rr][cc] > 0) s += Math.min(20, A[rr][cc]);
            }
            if(bestNoDir == -1 || s > bestSum) {
                bestSum = s;
                bestNoDir = no;
            }
        }

        if(bestSum > 0 && bestNoDir != -1) {
            for(int k = 0; k < 5; k++) {
                int rr = rb.r + sx[k], cc = rb.c + sy[k];
                if(!inRange(rr, cc)) continue;
                if(k < 4 && k == bestNoDir) continue;
                if(A[rr][cc] > 0) A[rr][cc] = Math.max(0, A[rr][cc] - 20);
            }
        }
    }

    // 먼지 축적: A > 0 에 +5
    static void increase() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(A[i][j] > 0) A[i][j] += 5;
            }
        }
    }

    // 먼지 확산 (동시 적용)
    static void spread() {
        int[][] temp = new int[N][N];

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(A[i][j] == 0) { // 깨끗한 칸으로만 확산
                    int s = 0;
                    for(int k = 0; k < 4; k++) {
                        int nr = i + dx4[k], nc = j + dy4[k];
                        if(!inRange(nr, nc)) continue;
                        if(A[nr][nc] > 0) s += A[nr][nc];
                    }
                    temp[i][j] = s / 10;
                }
            }
        }

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                A[i][j] += temp[i][j];
            }
        }
    }

    static long totalDust() {
        long s = 0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(A[i][j] > 0) s += A[i][j];
            }
        }
        return s;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        L = Integer.parseInt(st.nextToken());

        A = new int[N][N];
        B = new int[N][N];

        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                A[i][j] = Integer.parseInt(st.nextToken());
                B[i][j] = -1; // 초기엔 로봇 없음
            }
        }

        for(int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            robots.add(new Robot(i, r, c));
            B[r][c] = i;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < L; i++) {
            for(Robot rb : robots) move(rb);       // 이동
            for(Robot rb : robots) cleanRobot(rb); // 청소
            increase();                            // 축적
            spread();                              // 확산
            sb.append(totalDust()).append("\n");
        }

        System.out.println(sb);
    }
}
