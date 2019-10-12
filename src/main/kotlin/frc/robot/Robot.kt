
package frc.robot

import edu.wpi.first.wpilibj.experimental.command.CommandScheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import frc.robot.subsystems.drive.DriveSubsystem
import frc.robot.subsystems.example.ExampleSubsystem
import frc.robot.subsystems.intake.Intake
import frc.robot.subsystems.superstructure.Superstructure
import org.ghrobotics.lib.wrappers.FalconTimedRobot

object Robot : FalconTimedRobot() {

    override fun robotInit() {
        // the + operator just adds the FalconSubsystem to the FalconSubsystemHandler
        +ExampleSubsystem
        +Intake
        +Superstructure
        +DriveSubsystem

        // it's not necessary if the thing isn't a Subsystem or FalconSubsystem
        Controls

        SmartDashboard.putData(CommandScheduler.getInstance())

        super.robotInit()
    }

    override fun robotPeriodic() {

        // update the buttons the driver might have pressed
        Controls.update()

        super.robotPeriodic()
    }

    override fun autonomousInit() {
        TestAuto2()().schedule()
        super.autonomousInit()
    }
}

fun main() {
    Robot.start()
}