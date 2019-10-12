package frc.robot.subsystems.superstructure

import edu.wpi.first.wpilibj.experimental.command.WaitCommand
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.sequential

object Superstructure: FalconSubsystem() {

    val kMatchStartToStowed get() = sequential {
        +WaitCommand(4.0).also { it.addRequirements(Superstructure) }
    }

}