package frc.robot.subsystems.intake

import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.units.derived.volt

class IntakeHatchCommand(val releasing: Boolean): FalconCommand(Intake) {
    override fun initialize() {
        Intake.hatchMotorOutput = if(releasing) (-1).volt else 1.volt
    }
}