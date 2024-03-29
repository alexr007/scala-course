package interview.general;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JavaInterviewQuestion {

  int min(List<Integer> xs) {
    throw new IllegalArgumentException();
  }

  int div(int a, int b) {
    return a / b;
  }

  public static void main(String[] args) {

    // 1. Predicate
    Predicate<Integer> p = x -> x % 2 == 0;

    // 2. filter via flatMap
    List<Integer> collect = IntStream.range(1, 10).boxed()
        .filter(x -> x % 2 == 0)
        .collect(Collectors.toList());

    // Why Optional // 2 + 4 + 6 + 8

    BinaryOperator<Integer> adder = (a, b) -> a + b;

    Optional<Integer> reduced1 = IntStream.range(1, 10).boxed()
        .filter(x -> x % 2 == 0)
        .reduce(adder);

    Integer reduced2 = IntStream.range(1, 10).boxed()
        .filter(x -> x % 2 == 0)
        .reduce(0, adder);

    List<Integer> a1 = IntStream.range(1, 10).boxed()
      .filter(p)
      .collect(Collectors.toList());
    List<Integer> f2 = IntStream.range(1, 10).boxed()
      .flatMap(x -> x % 2 == 0 ? Stream.of(x) : Stream.empty())
      .collect(Collectors.toList());

    // 3.1. Implement collector
    List<Integer> b1 = IntStream.range(1, 10).boxed()
      .collect(null);

    // 3.2. Implement collector
    Map<Integer, List<Integer>> b2 = IntStream.range(1, 10).boxed()
      .collect(null);

    // 5. Implement Option<A> from scratch

  }

}
