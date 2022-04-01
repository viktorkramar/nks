import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lab3 {

    /** example from manual
    private static final double[] PROBABILITY = {0.5, 0.6, 0.7, 0.8, 0.85, 0.9, 0.92, 0.94};
    private static final int [][] LINKS = {
    {0, 1, 1, 0, 0, 0, 0, 0},
    {0, 0, 0, 1, 1, 0, 0, 0},
    {0, 0, 0, 1, 0, 1, 0, 1},
    {0, 0, 0, 0, 1, 1, 0, 1},
    {0, 0, 0, 0, 0, 1, 1, 0},
    {0, 0, 0 ,0, 0, 0, 1, 1},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0}};
    private static final List<Integer> WORKING_STATES = new ArrayList<>();
    private static final List<Integer> START = List.of(1);
    private static final List<Integer> FINISH = List.of(7, 8);
    private static final int TIME = 1000;
    private static final int K1 = 1;
    private static final ReservationType K1_RESERVATION_TYPE = ReservationType.General;
    private static final LoadType K1_LOAD_TYPE = LoadType.Unloaded;
    private static final int K2 = 1;
    private static final ReservationType K2_RESERVATION_TYPE = ReservationType.Separate;
    private static final LoadType K2_LOAD_TYPE = LoadType.Loaded;
    */

    private static final double[] PROBABILITY = {0.74, 0.14, 0.56, 0.35, 0.20, 0.21};
    private static final int [][] LINKS = {
            {0, 0, 1, 1, 1, 0},
            {0, 0, 1, 1, 0, 1},
            {0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0 ,0, 0, 0}};
    private static final List<Integer> WORKING_STATES = new ArrayList<>();
    private static final List<Integer> START = List.of(1, 2);
    private static final List<Integer> FINISH = List.of(5, 6);
    private static final int TIME = 2840;
    private static final int K1 = 3;
    private static final ReservationType K1_RESERVATION_TYPE = ReservationType.General;
    private static final LoadType K1_LOAD_TYPE = LoadType.Loaded;
    private static final int K2 = 2;
    private static final ReservationType K2_RESERVATION_TYPE = ReservationType.General;
    private static final LoadType K2_LOAD_TYPE = LoadType.Unloaded;

    private static final int SIZE = PROBABILITY.length;

    public static void main(String[] args) {
        double pSystem = calculateProbability(LINKS, PROBABILITY);
        calculateReservedProbability(K1, pSystem, K1_RESERVATION_TYPE, K1_LOAD_TYPE);
        calculateReservedProbability(K2, pSystem, K2_RESERVATION_TYPE, K2_LOAD_TYPE);
    }

    private static void printResult(double pSystem, double pReservedSystem) {
        double qSystem = 1 - pSystem;
        double tSystem = round(-TIME / Math.log(pSystem), 4);
        double qReservedSystem = round(1 - pReservedSystem, 4);
        double tReservedSystem = round(-TIME / Math.log(pReservedSystem), 4);
        double gP = round(pReservedSystem / pSystem, 4);
        double gQ = round(qReservedSystem / qSystem, 4);
        double gT = round(tReservedSystem / tSystem, 4);
        System.out.printf("Ймовірність безвідмовної роботи системи без резервування Psystem(%d) = %f\n", TIME, pSystem);
        System.out.printf("Ймовірність відмови системи без резервування Qsystem(%d) = %f\n", TIME, qSystem);
        System.out.printf("Середній наробіток на відмову для системи без резервування Tsystem(%d) = %f\n", TIME,
                tSystem);
        System.out.printf("Ймовірність безвідмовної роботи системи з резервуванням Preservedsystem(%d) = %f\n", TIME,
                pReservedSystem);
        System.out.printf("Ймовірність відмови системи з резервуванням Qreservedsystem(%d) = %f\n", TIME,
                qReservedSystem);
        System.out.printf("Середній наробіток на відмову для системи з резервуванням Treservedsystem(%d) = %f\n", TIME,
                tReservedSystem);
        System.out.printf("Виграш надійності за ймовірністю безвідмовної роботи Gp(%d) = %f\n", TIME, gP);
        System.out.printf("Виграш надійності за ймовірністю відмов Gq(%d) = %f\n", TIME, gQ);
        System.out.printf("Виграш надійності за середнім часом безвідмовної роботи Gt(%d) = %f\n\n", TIME, gT);
    }

    private static double calculateProbability(int [][] links, double[] probability ) {
        for (int i = 0; i < START.size(); i++) {
            process(i + 1, links[START.get(i) - 1], "");
        }
        double pStateTotal = 0;
        String pattern = Stream.generate(() -> "0").limit(SIZE).collect(Collectors.joining());
        for (int i = 0; i < 1<< SIZE; i++) {
            for (Integer workingState : WORKING_STATES) {
                if (((i & workingState) == workingState)) {
                    String str = Integer.toBinaryString(i);
                    str = (pattern + str).substring(str.length());
                    double pState = 1;
                    for (int j = 0; j < SIZE; j++) {
                        char c = str.charAt(j);
                        if (c == '1') {
                            pState *= probability[j];
                        } else {
                            pState *= (1 - probability[j]);
                        }
                    }
                    pState = round(pState, 6);
                    pStateTotal += pState;
                    break;
                }
            }
        }
        return round(pStateTotal, 5);
    }

    private static void process(int rowNumber, int[] row, String way) {
        if (FINISH.contains(rowNumber)) {
            way = way + "E" + rowNumber;
            String tmp = way.replace(" -> ", "").replace("E","");
            int state = 0;
            for (int i = 1; i <= SIZE; i++) {
                state = state * 2;
                if (tmp.contains(String.valueOf(i))) {
                    state = state + 1;
                }
            }
            WORKING_STATES.add(state);
        } else {
            way = way + "E" + rowNumber + " -> ";
            for (int i = 0; i < row.length; i++) {
                if (row[i] == 1) {
                    process(i + 1, LINKS[i], way);
                }
            }
        }
    }

    private static void calculateReservedProbability (int k, double pSystem, ReservationType reservationType,
                                                        LoadType loadType) {
        double p = reservationType == ReservationType.General ?
                calculateGeneralLoad(pSystem, k, loadType) :
                calculateSeparatedLoad(k, loadType);
        System.out.printf("1. Розрахунок для %s %s резервування з кратністю K = %d\n\n",
                K1_RESERVATION_TYPE == ReservationType.General ? "загального" : "роздільного",
                K1_LOAD_TYPE == LoadType.Loaded ? "навантаженого" : "ненавантаженого", k);
        printResult(pSystem, p);
    }

    private static double calculateGeneralLoad(double probability, int k, LoadType loadType) {
        return loadType == LoadType.Loaded ? 1 - (1 - Math.pow(1 - probability, k + 1))
                : 1 - (1 - probability) / factorial(k + 1);
    }

    private static double calculateSeparatedLoad(int k, LoadType loadType) {
        double[] updatedProbability = new double[PROBABILITY.length];
        if (loadType == LoadType.Loaded) {
            for (int i = 0; i < updatedProbability.length; i++) {
                updatedProbability[i] = 1 - Math.pow(1 - PROBABILITY[i], k + 1);
            }
        } else {
            for (int i = 0; i < updatedProbability.length; i++) {
                updatedProbability[i] = 1 - (1 - PROBABILITY[i]) / factorial(k + 1);
            }
        }
        return calculateProbability(LINKS, updatedProbability);
    }

    private static double round(double value, int scale) {
        return new BigDecimal(Double.toString(value))
                .setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    private static int factorial(int n) {
        if (n <= 2) {
            return n;
        }
        return n * factorial(n - 1);
    }

    private enum  ReservationType {
        General, Separate
    }

    private enum  LoadType {
        Loaded, Unloaded
    }
}
