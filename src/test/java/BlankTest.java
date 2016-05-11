import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;


public class BlankTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

 @Test
 public void runBlank_CheckifPageIsBlank_true() {
   Blank myBlank = new Blank();
   assertEquals(FirstArgument, SecondArgument());
 }
}
