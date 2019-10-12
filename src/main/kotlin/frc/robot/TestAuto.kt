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
//                        path1,
//                        Autonomous.isStartingOnLeft
                    5.second
                )
                +(sequential {
//                    +DriveSubsystem.notWithinRegion(0)
                    +WaitCommand(0.5)
                    +Superstructure.kMatchStartToStowed
                }).beforeStarting { Intake.hatchMotorOutput = 6.volt }.whenFinished { Intake.hatchMotorOutput = 0.volt }
            }

            +TurnInPlaceCommand {
//                Pose2d().mirror
////                val goal = TrajectoryWaypoints.kRocketF.translation.let { if(Autonomous.isStartingOnLeft()) it.mirror else it }
//                val goalTarget = TargetTracker.getBestTarget(true)
//                if(goalTarget != null) {
//                    val goal = goalTarget.averagedPose2d.translation
//                    val error = (goal - DriveSubsystem.robotPosition.translation)
//                    Rotation2d(error.x.meter, error.y.meter, true)
//                } else {
//                    -151.degree.toRotation2d()
//                }
////                -151.degree.toRotation2d()
                1.second
            }

            +super.followVisionAssistedTrajectory(
//                    path2,
//                    Autonomous.isStartingOnLeft,
//                    10.feet,
//                    true
                    2.second
            )

            // Reorient position on field based on Vision alignment.
            +relocalize(
//                    TrajectoryWaypoints.kRocketF,
//                    true,
//                    Autonomous.isStartingOnLeft,
//                    isStowed = true
            )

            val spline3 = DriveSubsystem.followTrajectory(
//                    path3,
//                    Autonomous.isStartingOnLeft
                    2.second
            )
            val spline4 = super.followVisionAssistedTrajectory(
//                    path4,
//                    Autonomous.isStartingOnLeft,
//                    7.feet, false
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
//                    TrajectoryWaypoints.kLoadingStationReversed,
//                    true,
//                    Autonomous.isStartingOnLeft,
//                    isStowed = true
            )

            // Part 3: Pickup hatch and go to the near side of the rocket.
            +org.ghrobotics.lib.commands.parallel {
                val path = DriveSubsystem.followTrajectory(
//                        path5, Autonomous.isStartingOnLeft
                    4.second
                )
                +path
                // Make sure the intake is holding the hatch panel.
                +IntakeHatchCommand(false).withTimeout(4.0.second).withExit { path.isFinished }
                // Follow the trajectory with vision correction to the near side of the rocket.
//                +WaitCommand(0.5)
//                +Superstructure.kStowed
            }
            // turn to face the goal
            +TurnInPlaceCommand {
                //                val goal = TrajectoryWaypoints.kRocketN.translation.let { if(Autonomous.isStartingOnLeft()) it.mirror else it }
//                val error = (goal - DriveSubsystem.robotPosition.translation)
//                Rotation2d(error.x.meter, error.y.meter, true)
//                (-28.75).degree.toRotation2d()
                1.second
            }
            +followVisionAssistedTrajectory(
//                    path6,
//                    Autonomous.isStartingOnLeft,
//                    4.feet
                3.second
            )

            +org.ghrobotics.lib.commands.parallel {
                +IntakeHatchCommand(true).withTimeout(1.0)
                +RunCommand(Runnable { DriveSubsystem.tankDrive(-0.3, -0.3) }).withTimeout(1.0)
            }
        }

}