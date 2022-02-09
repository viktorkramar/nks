import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class Lab1 {
    /** test data
     private static final int[] DATA = {644, 1216, 2352, 1386, 1280, 903, 607, 2068,
     4467, 835, 313, 555, 307, 508, 1386, 2895, 583,
     292, 5159, 1107, 181, 18, 1247, 125, 1452, 4211,
     890, 659, 1602, 2425, 214, 68, 21, 1762, 1118,
     45, 1803, 1187, 2154, 19, 1122, 278, 1622, 702,
     1396, 694, 45, 1739, 3483, 1334, 1852, 96, 173,
     7443, 901, 2222, 4465, 18, 1968, 1426, 1424,
     1146, 435, 1390, 246, 578, 281, 455, 609, 854,
     436, 1762, 444, 466, 1934, 681, 4539, 164, 295,
     1644, 711, 245, 740, 18, 474, 623, 462, 605, 187,
     106, 793, 92, 296, 226, 63, 246, 446, 2234, 2491,
     315};
     private static final double GAMMA = 0.9;
     private static final int TIME_ONE = 2000;
     private static final int TIME_TWO = 2000;
     */

    private static final int[] DATA = {25, 80, 157, 39, 372, 45, 108, 549, 1771,
            969, 508, 1134, 90, 382, 413, 444, 329, 158,
            551, 536, 216, 337, 493, 12, 1, 514, 88, 243,
            56, 521, 231, 301, 1120, 528, 513, 95, 79,
            460, 41, 383, 223, 39, 51, 625, 346, 11, 26,
            645, 377, 169, 88, 396, 126, 269, 962, 38,
            850, 2, 80, 73, 65, 253, 180, 80, 553, 150,
            808, 412, 384, 199, 640, 688, 613, 70, 227,
            481, 238, 253, 207, 879, 182, 670, 146, 453,
            502, 206, 94, 7, 28, 17, 31, 34, 136, 659,
            209, 143, 652, 119, 115, 259};
    private static final double GAMMA = 0.75;
    private static final int TIME_ONE = 44;
    private static final int TIME_TWO = 801;

    private static final int INTERVAL_NUMBER = 10;
    private static final double[] intervals = new double[INTERVAL_NUMBER];
    private static final int[] n = new int[INTERVAL_NUMBER];
    private static final double[] f = new double[INTERVAL_NUMBER];
    private static final double[] p = new double[INTERVAL_NUMBER];
    private static final int number = DATA.length;

    public static void main(String[] args) {
        System.out.println("Data:");
        System.out.println(printArray(DATA));

        double tcp = Arrays.stream(DATA).average().orElse(0);
        System.out.printf("Tcp = %s\n", tcp);

        Arrays.sort(DATA);
        System.out.println("Sorted data:");
        System.out.println(printArray(DATA));

        double h = ((double) DATA[number - 1]) / INTERVAL_NUMBER;
        System.out.printf("h = %s\n", h);

        for (int i = 0; i < INTERVAL_NUMBER; i++) {
            intervals[i] = round(((double) DATA[number - 1]) / INTERVAL_NUMBER * (i + 1), 1);
        }
        System.out.println("Intervals:");
        System.out.println(Arrays.toString(intervals));

        for (int value: DATA) {
            int index = (int) (value / h);
            if (value % h < 0.0001) {
                index --;
            }
            n[index] = n[index] + 1;
        }
        System.out.println("n:");
        System.out.println(Arrays.toString(n));

        for (int i = 0; i < INTERVAL_NUMBER; i++) {
            f[i] = round(((double) n[i]) / number / h, 6);
        }
        System.out.println("f:");
        System.out.println(Arrays.toString(f));

        double fSum = 0;
        for (int i = 0; i < INTERVAL_NUMBER; i++) {
            fSum = fSum + f[i];
            p[i] = round(1 - h * fSum, 2);
        }
        System.out.println("p:");
        System.out.println(Arrays.toString(p));

        double d = round((p[0] - GAMMA) / (p[0] - 1), 2);
        System.out.printf("d = %s\n", d);

        double t = round(h * (1 - d), 2);
        System.out.printf("t = %s\n", t);

        System.out.printf("Probability of non failure work = %s\n",
                round(getNotFailureProbability(TIME_ONE, h), 5));
        System.out.printf("Failure rate = %s", round(countLambda(TIME_TWO, h), 6));
    }

    private static double getNotFailureProbability(int time, double h) {
        double p_failure = 0;
        for (int i = 0; i < INTERVAL_NUMBER; i++) {
            if (time > h * (i + 1)) {
                p_failure = p_failure + f[i] * h;
            } else {
                p_failure = p_failure + f[i] * (time - h * i);
                break;
            }
        }
        return 1 - round(p_failure, 6);
    }

    private static double countLambda(int time, double h) {
        int index = (int) (time / h);
        if (time - ((int) (time / h)) * h < 0.0001) {
            index--;
        }
        return f[index] / getNotFailureProbability(time, h);
    }

    private static double round(double value, int scale) {
        return new BigDecimal(Double.toString(value))
                .setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    private static String printArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0 && i % 10 == 0) {
                sb.append("\n");
            }
            sb.append(arr[i]).append(" ");
        }
        sb.append(" ]");
        return sb.toString();
    }
}
