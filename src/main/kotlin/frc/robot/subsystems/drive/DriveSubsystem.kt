package frc.robot.subsystems.drive

import edu.wpi.first.wpilibj.experimental.command.InstantCommand
import edu.wpi.first.wpilibj.experimental.command.WaitCommand
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.mathematics.twodim.control.RamseteTracker
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTrackerOutput
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derived.velocity
import org.ghrobotics.lib.simulation.SimFalconMotor
import org.ghrobotics.lib.subsystems.drive.TrajectoryTrackerDriveBase

object DriveSubsystem: FalconSubsystem(), TrajectoryTrackerDriveBase {

    var lowGear: Boolean = false
    override val leftMotor = SimFalconMotor<Meter>()
    override val rightMotor = SimFalconMotor<Meter>()

    override val robotPosition = Pose2d()
    override val trajectoryTracker = RamseteTracker(2.0, 0.7)
    override fun setOutput(output: TrajectoryTrackerOutput) {

    }

    fun followTrajectory(duration: SIUnit<Second>) = object: WaitCommand(duration.second) {
        init {
            addRequirements(DriveSubsystem)
        }
    }

    fun turnInPlace(duration: SIUnit<Second>) = object: WaitCommand(duration.second) {
        init {
            addRequirements(DriveSubsystem)
        }
    }

    fun notWithinRegion(kHabitatL1Platform: Any) = InstantCommand()
    fun tankDrive(left: Double, right: Double) {
        leftMotor.setVelocity(left.meter.velocity)
        rightMotor.setVelocity(right.meter.velocity)
    }

}