
package frc.robot

import frc.robot.subsystems.example.ExampleSubsystem
import org.ghrobotics.lib.wrappers.FalconTimedRobot

object Robot : FalconTimedRobot() {

    override fun robotInit() {
        // the + operator just adds the FalconSubsystem to the FalconSubsystemHandler
        +ExampleSubsystem

        // it's not necessary if the thing isn't a Subsystem or FalconSubsystem
        Controls

        super.robotInit()
    }

    override fun robotPeriodic() {

        // update the buttons the driver might have pressed
        Controls.update()

        super.robotPeriodic()
    }

    override fun autonomousInit() {
        TestAuto()().schedule()
        super.autonomousInit()
    }
}

fun main() {
    Robot.start()
}