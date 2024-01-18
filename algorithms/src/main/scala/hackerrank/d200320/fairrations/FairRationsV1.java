package hackerrank.d200320.fairrations;

import java.util.Arrays;
import java.util.Optional;

/**
 * https://www.hackerrank.com/challenges/fair-rations/problem
 */
public class FairRationsV1 {
  // all even
  static boolean isDone(int[] amount) {
    return Arrays.stream(amount).allMatch(n -> (n & 1) == 0);
  }

  static boolean isWrongState(int[] amount) {
    return Arrays.stream(amount).filter(n -> (n & 1) == 1).count() == 1;
  }

  static int findTheOdd(int[] bread) {
    for (int i = 0; i < bread.length; i++) {
      if ((bread[i]&1) == 1) return i;
    }
    throw new IllegalArgumentException("must be!");
  }

  static Optional<Integer> fairRations(int[] bread) {
    int step=0;
    while (!isDone(bread) && !isWrongState(bread)) {
      int idx = findTheOdd(bread);
      bread[idx]++;
      bread[idx+1]++;
      step++;
    }
    if (isDone(bread)) return Optional.of(step*2);
    return Optional.empty();
  }

  public static void main(String[] args) {
    Optional.of(1).map(n -> Integer.toString(n)).orElse("NO");
    int[] d = {2, 3, 4, 5, 6};
    System.out.println(fairRations(d));
  }

}
