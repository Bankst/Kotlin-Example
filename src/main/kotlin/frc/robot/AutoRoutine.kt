package frc.robot

import edu.wpi.first.wpilibj.experimental.command.*
import frc.robot.subsystems.drive.DriveSubsystem
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.commands.sequential
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.duration
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map

abstract class AutoRoutine : SequentialCommandGroup(), Source<Command> {

    abstract val duration: SIUnit<Second>
    abstract val routine: Command

    override fun invoke() = sequential {
        +InstantCommand(Runnable {
            println("[AutoRoutine] Starting routine...")
//            DriveSubsystem.localization.reset(Autonomous.startingPosition().pose)
        })
        +routine
    }.raceWith(WaitUntilCommand { Robot.emergencyActive }) as SendableCommandBase

    fun followVisionAssistedTrajectory(
            originalTrajectory: TimedTrajectory<Pose2dWithCurvature>,
            pathMirrored: BooleanSource,
            radiusFromEnd: SIUnit<Meter>,
            useAbsoluteVision: Boolean = false
    ) = DriveSubsystem.followTrajectory(
//            pathMirrored.map(originalTrajectory.mirror(), originalTrajectory),
//            radiusFromEnd,
//            useAbsoluteVision
            originalTrajectory.duration
    )

    fun followVisionAssistedTrajectory(duration: SIUnit<Second>) = DriveSubsystem.followTrajectory(duration)

    fun TurnInPlaceCommand(duration: SIUnit<Second>) = DriveSubsystem.followTrajectory(duration)

    fun TurnInPlaceCommand(duration: () -> SIUnit<Second>) = DriveSubsystem.followTrajectory(duration())


    protected fun relocalize(position: Pose2d, forward: Boolean, pathMirrored: BooleanSource, isStowed: Boolean = false) = InstantCommand(Runnable {
        val newPosition = Pose2d(
                pathMirrored.map(position.mirror, position)().translation // if pathMirrored is true, mirror the pose
                // otherwise, don't. Use that translation2d for the new position
//                DriveSubsystem.localization().rotation
        ) //+ if (forward) (if (isStowed) Constants.kForwardIntakeStowedToCenter else Constants.kForwardIntakeToCenter) else Constants.kBackwardIntakeToCenter
        println("RESETTING LOCALIZATION TO ${newPosition.asString()}")
//        DriveSubsystem.localization.reset(newPosition)
    })

    protected fun relocalize() = InstantCommand(Runnable {
//        val newPosition = Pose2d(
//                pathMirrored.map(position.mirror, position)().translation, // if pathMirrored is true, mirror the pose
//                // otherwise, don't. Use that translation2d for the new position
////                DriveSubsystem.localization().rotation
//        ) //+ if (forward) (if (isStowed) Constants.kForwardIntakeStowedToCenter else Constants.kForwardIntakeToCenter) else Constants.kBackwardIntakeToCenter
        println("RESETTING LOCALIZATION TO ${Pose2d().asString()}")
//        DriveSubsystem.localization.reset(newPosition)
    })

    protected fun executeFor(time: SIUnit<Second>, command: FalconCommand) = sequential {
        +command
        +WaitCommand(100.0)
    }.withTimeout(time)

    private fun Pose2d.asString() = "Pose X:${translation.x / kFeetToMeter}\' Y:${translation.y / kFeetToMeter}' Theta:${rotation.degree}deg"

    fun notWithinRegion(region: Rectangle2d) = object : SendableCommandBase() {
        override fun isFinished() = !region.contains(DriveSubsystem.robotPosition.translation)
    }

    operator fun Command.unaryPlus() {
        addCommands(this@unaryPlus)
    }
}

fun Command.withExit(exit: BooleanSource): Command = this.interruptOn(exit)

// fun Command.withExit(exit: BooleanSource) = parallelRace {
//    +this@withExit
//    +WaitUntilCommand(exit)
// }

fun Command.withTimeout(second: SIUnit<Second>): Command = this.withTimeout(second.second)

val Translation2d.mirror get() = Translation2d(this.x, 27.0.feet - this.y)
