import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lab2 {
    /** example from manual
    private static final double[] probability = {0.5, 0.6, 0.7, 0.8, 0.85, 0.9, 0.92, 0.94};
    private static final int [][] links = {
            {0, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 1},
            {0, 0, 0, 0, 1, 1, 0, 1},
            {0, 0, 0, 0, 0, 1, 1, 0},
            {0, 0, 0 ,0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}};
    private static final List<Integer> workingStates = new ArrayList<>();
    private static final List<Integer> start = List.of(1);
    private static final List<Integer> finish = List.of(7, 8);
     */

    private static final double[] probability = {0.74, 0.14, 0.56, 0.35, 0.20, 0.21};
    private static final int [][] links = {
            {0, 0, 1, 1, 1, 0},
            {0, 0, 1, 1, 0, 1},
            {0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0 ,0, 0, 0}};
    private static final List<Integer> workingStates = new ArrayList<>();
    private static final List<Integer> start = List.of(1, 2);
    private static final List<Integer> finish = List.of(5, 6);

    private static final int size = probability.length;

    public static void main(String[] args) {
        for (int i = 0; i < start.size(); i++) {
            process(i + 1, links[start.get(i) - 1], "");
        }

        printWorkingConditions();
    }

    private static void process(int rowNumber, int[] row, String way) {
        if (finish.contains(rowNumber)) {
            way = way + "E" + rowNumber;
            System.out.println(way);
            String tmp = way.replace(" -> ", "").replace("E","");
            int state = 0;
            for (int i = 1; i <= size; i++) {
                state = state * 2;
                if (tmp.contains(String.valueOf(i))) {
                    state = state + 1;
                }
            }
            workingStates.add(state);
        } else {
            way = way + "E" + rowNumber + " -> ";
            for (int i = 0; i < row.length; i++) {
                if (row[i] == 1) {
                    process(i + 1, links[i], way);
                }
            }
        }
    }

    private static void printWorkingConditions() {
        StringBuffer sb = new StringBuffer("\n");
        sb.append("+----".repeat(Math.max(0, size)))
                .append("+----------+\n|");
        for (int i = 1; i <= size; i++) {
            sb.append(" E")
                    .append(i)
                    .append(" |");
        }
        sb.append("  Pstate  |\n");
        double pStateTotal = 0;
        DecimalFormat df = new DecimalFormat("#.######");
        String pattern = Stream.generate(() -> "0").limit(size).collect(Collectors.joining());
        for (int i = 0; i < 1<<size; i++) {
            for (Integer workingState : workingStates) {
                if (((i & workingState) == workingState)) {
                    sb.append("+----".repeat(Math.max(0, size)))
                            .append("+----------+\n");
                    String str = Integer.toBinaryString(i);
                    str = (pattern + str).substring(str.length());
                    double pState = 1;
                    for (int j = 0; j < size; j++) {
                        char c = str.charAt(j);
                        if (c == '1') {
                            pState *= probability[j];
                        } else {
                            pState *= (1 - probability[j]);
                        }
                    }
                    pState = round(pState, 6);
                    pStateTotal += pState;
                    sb.append(str.replace("0", "| -  ")
                            .replace("1", "| +  "))
                            .append("| ")
                            .append((df.format(pState) + "  ").substring(0, 8))
                            .append(" | \n");
                    break;
                }
            }
        }
        sb.append("+----".repeat(Math.max(0, size)))
                .append("+----------+\nPsystem = ")
                .append(df.format(pStateTotal));
        System.out.println(sb);
    }

    private static double round(double value, int scale) {
        return new BigDecimal(Double.toString(value))
                .setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}