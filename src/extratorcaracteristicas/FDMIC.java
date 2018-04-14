/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extratorcaracteristicas;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Felipe
 */
public class FDMIC {

    public static final int RMAX = 5;
    public static final int RQUADMAX = RMAX * RMAX + 1;
    public static final int NUM_LABELS = 3;
    public static final int NUM_CAMADAS = 96;
    public static final int NQ = 46000;
    public static final int INFINITO = 2000000000;

    public final int atribRaioMax[] = {0, 1, 4, 8, 14, 22, 31, 42, 54, 69, 85};
    public final int raiosPossiveis[] = {0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1};
    public final int bit[] = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824};
    public int quadrado[];
    public double contador[][];

    public int A[][][];
    public int V[][][];

    public FDMIC() {
        this.quadrado = new int[NQ];
        contador = new double[NUM_LABELS][RQUADMAX];

        for (int i = 0; i < FDMIC.NQ; i++) {
            this.quadrado[i] = i * i;
        }

    }

    public double[] fdmicMain(Mat imagem) {

        int i, j, k, Nvox, auxFor;
        int u;
        int altura = imagem.height(); //altura
        int largura = imagem.width(); // largura
        int L = imagem.channels(); //num de cores

        int camadas = NUM_CAMADAS;

        double X[], Y[], Z[];
        double rotulos[], dim_in[], Vretorno[];

        //[altura, largura, L] = size(Imagem);
        X = new double[altura * largura * L];
        Y = new double[altura * largura * L];
        Z = new double[altura * largura * L];
        rotulos = new double[altura * largura * L];

        u = 0;
        for (i = 0; i < altura; i++) {
            for (j = 0; j < largura; j++) {
                double data[] = imagem.get(i, j);
                for (k = 0; k < L; k++) {
                    X[u] = i;
                    Y[u] = j;
                    Z[u] = data[2 - k] * (camadas - 1) / 255.0; //Imagem[i][j][k] / 255.0;

                    rotulos[u] = 1 << (k);//bitshift
                    u++;
                }
            }
        }

        double menor = Double.MAX_VALUE;
        double maior = -Double.MAX_VALUE;

        //Multiplica vetor por escalar
        for (auxFor = 0; auxFor < Z.length; auxFor++) {

            if (Z[auxFor] < menor) {
                menor = Z[auxFor];
            }

            if (Z[auxFor] > maior) {
                maior = Z[auxFor];
            }

        }

        for (auxFor = 0; auxFor < Z.length; auxFor++) {
            Z[auxFor] = Z[auxFor] - menor;
        }

        camadas = (int) maior - (int) menor + 1;

        Nvox = X.length;

        this.transformadaDistancia(X, Y, Z, rotulos, Nvox, altura, largura, camadas);

        Vretorno = new double[NUM_LABELS * 22];

        u = 0;
        for (i = 0; i < NUM_LABELS; i++) {
            for (j = 0; j < RQUADMAX; j++) {
                if (raiosPossiveis[j] != 0) {
                    Vretorno[u] = contador[i][j];
                    u++;
                }
            }
        }

        return Vretorno;
    }

    public void transformadaDistancia(double X[], double Y[], double Z[],
            double rotulos[], int Nvox, int M, int N, int L) {

        M = M + (2 * RMAX);
        N = N + (2 * RMAX);
        L = L + (2 * RMAX);

        this.V = new int[M][N][L];
        this.A = new int[M][N][L];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < L; k++) {
                    A[i][j][k] = Integer.MAX_VALUE;
                    V[i][j][k] = 0;
                }
            }
        }

        this.inicializa(X, Y, Z, rotulos, Nvox, A, V);
        this.stepOne(M, N, L);
        this.stepTwo(M, N, L);
        this.stepThree(M, N, L);
        this.contagem(M, N, L);

    }

    public void inicializa(double X[], double Y[], double Z[],
            double rotulos[], int Nvox, int A[][][], int V[][][]) {

        for (int u = 0; u < Nvox; u++) {

            A[(int) X[u] + RMAX][(int) Y[u] + RMAX][(int) Z[u] + RMAX] = 0;
            V[(int) X[u] + RMAX][(int) Y[u] + RMAX][(int) Z[u] + RMAX] |= (int) rotulos[u]; // bit or
        }
    }

    public void stepOne(int M, int N, int L) {

        int i, k, j;
        int x, d, label;

        for (i = 0; i < M; i++) {
            for (j = 0; j < N; j++) {

                x = -1;
                label = V[i][j][0];

                for (k = 0; k < L; k++) {

                    if (A[i][j][k] == 0) {
                        x = k;
                        label = V[i][j][k];
                    } else if (x > 0) {
                        V[i][j][k] = label;
                        A[i][j][k] = quadrado[Math.abs(k - x)]; //ABS
                    }
                }

                x = -1;
                label = V[i][j][L - 1];
                for (k = L - 1; k >= 0; k--) {

                    if (A[i][j][k] == 0) {
                        x = k;
                        label = V[i][j][k];
                    } else if (x > 0) {
                        d = quadrado[Math.abs(k - x)];
                        if (d < A[i][j][k]) {
                            V[i][j][k] = label;
                            A[i][j][k] = d;
                        } else if (d == A[i][j][k]) {
                            V[i][j][k] |= label;
                        }
                    }
                }
            }
        }
    }

    public void stepTwo(int M, int N, int L) {

        int j, k, i, a, b, m, n;
        int buff[] = new int[N];
        int buffV[] = new int[N];

        for (i = 0; i < M; i++) {
            for (k = 0; k < L; k++) {
                for (j = 0; j < N; j++) {
                    buff[j] = A[i][j][k];
                    buffV[j] = V[i][j][k];
                }

                a = 0;
                for (j = 1; j < N; j++) {
                    if (a > 0) {
                        a--;
                    }

                    if (buff[j] > buff[j - 1]) {
                        b = (buff[j] - buff[j - 1]) / 2;
                        if (b >= N - j) {
                            b = (N - 1) - j;
                        }

                        for (n = a; n <= b; n++) {
                            m = buff[j - 1] + quadrado[Math.abs(n + 1)];
                            if (buff[j + n] < m) {
                                break;
                            }

                            if (A[i][j + n][k] > m) {
                                A[i][j + n][k] = m;
                                V[i][j + n][k] = buffV[j - 1];
                            } else if (m == A[i][j + n][k]) {
                                V[i][j + n][k] |= buffV[j - 1];
                            }
                        }
                        a = b;
                    } else {
                        a = 0;
                    }
                }

                a = 0;
                for (j = N - 2; j >= 0; j--) {
                    if (a > 0) {
                        a--;
                    }

                    if (buff[j] > buff[j + 1]) {
                        b = (buff[j] - buff[j + 1]) / 2;
                        if (j - b < 0) {
                            b = j;
                        }
                        for (n = a; n <= b; n++) {
                            m = buff[j + 1] + quadrado[Math.abs(n + 1)];
                            if (buff[j - n] < m) {
                                break;
                            }

                            if (A[i][j - n][k] > m) {
                                A[i][j - n][k] = m;
                                V[i][j - n][k] = buffV[j + 1];
                            } else if (m == A[i][j - n][k]) {
                                V[i][j - n][k] |= buffV[j + 1];
                            }
                        }
                        a = b;
                    } else {
                        a = 0;
                    }
                }
            }
        }
    }

    public void stepThree(int M, int N, int L) {

        int j, k, i, a, b, m, n;
        int buff[] = new int[M];
        int buffV[] = new int[M];

        for (k = 0; k < L; k++) {
            for (j = 0; j < N; j++) {
                for (i = 0; i < M; i++) {
                    buff[i] = A[i][j][k];
                    buffV[i] = V[i][j][k];
                }

                a = 0;
                for (i = 1; i < M; i++) {
                    if (a > 0) {
                        a--;
                    }

                    if (buff[i] > buff[i - 1]) {
                        b = (buff[i] - buff[i - 1]) / 2;
                        if (b >= M - i) {
                            b = (M - 1) - i;
                        }

                        for (n = a; n <= b; n++) {
                            m = buff[i - 1] + quadrado[Math.abs(n + 1)];
                            if (buff[i + n] < m) {
                                break;
                            }

                            if (A[i + n][j][k] > m) {
                                A[i + n][j][k] = m;
                                V[i + n][j][k] = buffV[i - 1];
                            } else if (m == A[i + n][j][k]) {
                                V[i + n][j][k] |= buffV[i - 1];
                            }
                        }
                        a = b;
                    } else {
                        a = 0;
                    }
                }

                //BACKWARD
                a = 0;
                for (i = M - 2; i >= 0; i--) {
                    if (a > 0) {
                        a--;
                    }

                    if (buff[i] > buff[i + 1]) {
                        b = (buff[i] - buff[i + 1]) / 2;
                        if (i - b < 0) {
                            b = i;
                        }

                        for (n = a; n <= b; n++) {
                            m = buff[i + 1] + quadrado[Math.abs(n + 1)];
                            if (buff[i - n] < m) {
                                break;
                            }

                            if (A[i - n][j][k] > m) {
                                A[i - n][j][k] = m;
                                V[i - n][j][k] = buffV[i + 1];
                            } else if (m == A[i - n][j][k]) {
                                V[i - n][j][k] |= buffV[i + 1];
                            }
                        }
                        a = b;
                    } else {
                        a = 0;
                    }
                }
            }
        }
    }

    public void contagem(int M, int N, int L) {

        int i, j, k, u;

        for (i = 0; i < NUM_LABELS; i++) {
            for (j = 0; j < RQUADMAX; j++) {
                contador[i][j] = 0;
            }
        }

        for (k = 0; k < L; k++) {
            for (i = 0; i < M; i++) {
                for (j = 0; j < N; j++) {
                    if (A[i][j][k] < RQUADMAX) {
                        for (u = 0; u < NUM_LABELS; u++) {
                            if ((V[i][j][k] & bit[u]) != 0) {
                                contador[u][A[i][j][k]]++;
                            }
                        }
                    }
                }
            }
        }

        for (i = 0; i < NUM_LABELS; i++) {
            for (j = 1; j < RQUADMAX; j++) {
                contador[i][j] += contador[i][j - 1];
            }
        }
    }
}
