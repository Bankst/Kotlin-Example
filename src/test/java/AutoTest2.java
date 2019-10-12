import edu.wpi.first.wpilibj.experimental.command.CommandScheduler;
import edu.wpi.first.wpilibj.experimental.command.SendableCommandBase;
import frc.robot.TestAuto;
import org.junit.Test;

public class AutoTest2 {

  @Test
  public void test() {

    SendableCommandBase routine = (new TestAuto()).invoke();
    var scheduler = CommandScheduler.getInstance();

  }

}
