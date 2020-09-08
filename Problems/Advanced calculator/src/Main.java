
/* Please, do not rename it */
class Problem {

    public static void main(String[] args) {
        String operator = args[0];
        int[] numbers = new int[args.length - 1];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(args[i+1]);
        }

        long result = 0;
        // write your code here
        switch (operator) {
            case "MAX":
                result = max(numbers);
                break;
            case "MIN":
                result = min(numbers);
                break;
            case "SUM":
                result = sum(numbers);
                break;
        }
        System.out.println(result);
    }

    public static int max(int[] array) {
        int max = array[0];
        for (int j : array) {
            max = Math.max(max, j);
        }
        return max;
    }

    public static int min(int[] array) {
        int min = array[0];
        for (int j : array) {
            min = Math.min(min, j);
        }
        return min;
    }

    public static long sum(int[] array) {
        long sum = 0;
        for (int j : array) {
            sum += j;
        }
        return sum;
    }
}