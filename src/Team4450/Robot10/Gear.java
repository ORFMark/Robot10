package Team4450.Robot10;

import Team4450.Lib.ValveDA;
import Team4450.Robot10.Robot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.*;
import Team4450.Lib.Util;
public class Gear {
	private final Robot robot;
	private final Talon gearIntake = new Talon(1);
	private final ValveDA	gearAcutuation = new ValveDA(1,0);
	private final ValveDA   gearElevator = new ValveDA(6);
	public double gearIntakePower = 0.75; //FIXME Get actual ID
	Gear (Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		this.robot=robot;
		gearIntakeStop();
		gearDown();
		gearElevatorDown();
	}
public void dispose()
{
	if (gearIntake != null) gearIntake.free();
	if (gearAcutuation != null) gearAcutuation.dispose();
	if (gearElevator != null) gearElevator.dispose();
}
public void gearIntakeSet(double power)
{
	Util.consoleLog();
	gearIntake.set(power);
	if (power != 0)
	{
		Util.consoleLog("Gear Intake Motor Active");
		SmartDashboard.putBoolean("GearPickupMotor", true);
	}
	else
	{
		Util.consoleLog("Gear Intake Motor Stopped");
		SmartDashboard.putBoolean("GearPickupMotor", false);
	}
}
public void gearIntakeIn()
{
	Util.consoleLog();
	gearIntakeSet(gearIntakePower);
}
public void gearIntakeOut()
{
	Util.consoleLog();
	gearIntakeSet(-gearIntakePower);
}
public void gearIntakeStop()
{
	Util.consoleLog();
	gearIntakeSet(0);
}
public void gearDown()
{
	Util.consoleLog();
	gearAcutuation.SetA(); //FIXME Get actual ID
	SmartDashboard.putBoolean("GearPickupDown", true);

}
public void gearUp()
{
	Util.consoleLog();
	gearAcutuation.SetB(); //FIXME get actual ID
	SmartDashboard.putBoolean("GearPickupDown", false);
}
public void gearElevatorUp()
{
	Util.consoleLog();
	gearElevator.SetA();
}
public void gearElevatorDown()
{
	Util.consoleLog();
	gearElevator.SetB();
}
}
