package frc.robot

import edu.wpi.first.wpilibj.experimental.command.*
import frc.robot.subsystems.drive.DriveSubsystem
import frc.robot.subsystems.intake.Intake
import frc.robot.subsystems.intake.IntakeHatchCommand
import frc.robot.subsystems.superstructure.Superstructure
import org.ghrobotics.lib.commands.sequential
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.volt
import org.ghrobotics.lib.mathematics.units.second

class TestAuto: AutoRoutine() {

    override val duration: SIUnit<Second>
        get() = 1.second

    override val routine: Command
        get() = sequential {

            +PrintCommand("Starting")
            +InstantCommand(Runnable { DriveSubsystem.lowGear = false })

            +org.ghrobotics.lib.commands.parallel {
                +DriveSubsystem.followTrajectory(
                    5.second
                )
                +(sequential {
//                    +DriveSubsystem.notWithinRegion(0)
                    +WaitCommand(0.5)
                    +Superstructure.kMatchStartToStowed
                }).beforeStarting { Intake.hatchMotorOutput = 6.volt }.whenFinished { Intake.hatchMotorOutput = 0.volt }
            }

            +TurnInPlaceCommand {
                1.second
            }

            +super.followVisionAssistedTrajectory(
                    2.second
            )

            // Reorient position on field based on Vision alignment.
            +relocalize(
            )

            val spline3 = DriveSubsystem.followTrajectory(
                    2.second
            )
            val spline4 = super.followVisionAssistedTrajectory(
                6.second
            )

            // Part 2: Place hatch and go to loading station.
            +org.ghrobotics.lib.commands.parallel {
                // Follow the trajectory with vision correction to the loading station.
                +sequential {
                    +spline3
                    +spline4
                }
                // Take the superstructure to pickup position and arm hatch intake 3 seconds before arrival.
                +sequential {
                    // Place hatch panel.
                    +IntakeHatchCommand(true).withTimeout(1.second)
                    +WaitCommand(3.0)
                    +org.ghrobotics.lib.commands.parallel {
                        //                        +Superstructure.kHatchLow
                        +IntakeHatchCommand(false).withExit { spline4.isFinished }
                    }
                }
            }

            // Reorient position on field based on Vision alignment.
            +relocalize(
            )

            // Part 3: Pickup hatch and go to the near side of the rocket.
            +org.ghrobotics.lib.commands.parallel {
                val path = DriveSubsystem.followTrajectory(
                    4.second
                )
                +path
                // Make sure the intake is holding the hatch panel.
                +IntakeHatchCommand(false).withTimeout(4.0.second).withExit { path.isFinished }
            }
            // turn to face the goal
            +TurnInPlaceCommand {
                1.second
            }
            +followVisionAssistedTrajectory(
                3.second
            )

            +org.ghrobotics.lib.commands.parallel {
                +IntakeHatchCommand(true).withTimeout(1.0)
                +RunCommand(Runnable { DriveSubsystem.tankDrive(-0.3, -0.3) }).withTimeout(1.0)
            }
            +PrintCommand("Ending auto.....")
        }

}