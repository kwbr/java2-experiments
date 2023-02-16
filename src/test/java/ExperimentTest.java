import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ExperimentTest {

  @Test
  void completed() throws ExecutionException, InterruptedException {
    var answer = CompletableFuture.completedFuture(42);
    assertThat(answer.get()).isEqualTo(42);
  }

  @Test
  void test() {
    var executor = Executors.newFixedThreadPool(10);
    var start = System.currentTimeMillis();
    var futureCategories = Stream.of(
            new Transaction("1", "description 1"),
            new Transaction("2", "description 2"),
            new Transaction("3", "description 3"),
            new Transaction("4", "description 4"),
            new Transaction("5", "description 5"),
            new Transaction("6", "description 6"),
            new Transaction("7", "description 7"),
            new Transaction("8", "description 8"),
            new Transaction("9", "description 9"),
            new Transaction("10", "description 10")
        )
        .map(transaction -> CompletableFuture.supplyAsync(
            () -> CategorizationService.categorizeTransaction(transaction), executor)
        )
        .toList();

    var categories = futureCategories.stream()
        .map(CompletableFuture::join)
        .toList();
    long end = System.currentTimeMillis();

    System.out.printf("The operation took %s ms%n", end - start);
    System.out.println("Categories are: " + categories);
  }

  public static class CategorizationService {

    public static Category categorizeTransaction(Transaction transaction) {
      delay();
      return new Category("Category_" + transaction.getId());
    }

    public static void delay() {
      try {
        Thread.sleep(1000L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class Category {

    private final String category;

    public Category(String category) {
      this.category = category;
    }

    @Override
    public String toString() {
      return "Category{" +
          "category='" + category + '\'' +
          '}';
    }
  }


  public static class Transaction {

    private String id;
    private String description;

    public Transaction(String id, String description) {
      this.id = id;
      this.description = description;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }
}
