import edu.wpi.first.wpilibj.experimental.command.CommandScheduler
import frc.robot.TestAuto
import org.junit.Test

class AutoTest {

    @Test
    fun testRoutine() {
        val routine = TestAuto()()

        val scheduler = CommandScheduler.getInstance()
        scheduler.run()
        routine.schedule()
        scheduler.run()
        assert(routine.isScheduled)

        

    }

}