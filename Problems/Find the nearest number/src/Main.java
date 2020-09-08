import java.util.*;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in);
        List<Integer> numbers = new ArrayList<>();

        for (String s : scanner.nextLine().split("\\s+")) {
            numbers.add(Integer.parseInt(s));
        }
        int n = scanner.nextInt();

        numbers.sort((o1, o2) -> {
            int abs1 = Math.abs(o1 - n);
            int abs2 = Math.abs(o2 - n);
            return abs1 < abs2 ? -1 : abs1 == abs2 ? Integer.compare(o1, o2) : 1;
        });

        int minDifference = Math.abs(numbers.get(0) - n);
        for (int i = 0; i < numbers.size() && minDifference == Math.abs(numbers.get(i) - n); i++) {
            System.out.print(numbers.get(i) + " ");
        }

    }
}