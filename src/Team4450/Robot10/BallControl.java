package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Lib.JoyStick.JoyStickButtonIDs;
import Team4450.Lib.LaunchPad.LaunchPadControlIDs;
import Team4450.Robot10.Robot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.*;

public class BallControl {
	private final Robot robot;
	private final CANTalon	intakeMotor = new CANTalon(0); //TODO get Port numbers;
	private final CANTalon shooterMotor1 = new CANTalon(1);
	private final CANTalon shooterMotor2 = new CANTalon (2);
	public double Intake_Power, Shooter_Power;
	BallControl (Robot robot, Teleop teleop)
	
	{
		Util.consoleLog();
		
		this.robot = robot;
		Intake_Power= 0.75; //TODO Get true power readouts
		Shooter_Power = 0.75;
		robot.InitializeCANTalon(intakeMotor);
		robot.InitializeCANTalon(shooterMotor1);
		robot.InitializeCANTalon(shooterMotor2);
		ceaseFire();
		intakeStop();
	}
	public void dispose()
	{
		if (shooterMotor1 != null) shooterMotor1.delete();
		if (shooterMotor2 != null) shooterMotor1.delete();
		if (intakeMotor != null) intakeMotor.delete();
	}
	public void intakeSet(double power)
	{
		Util.consoleLog("%f", power);
		intakeMotor.set(power);
	}
	public void shooterSet(double power)
	{
		Util.consoleLog("%f", power);
		shooterMotor1.set(power);
		shooterMotor2.set(power);
	}
	public void intakeIn()
	{
		Util.consoleLog();
		intakeSet(Intake_Power);
	}
	public void intakeOut()
	{
		Util.consoleLog();
		intakeSet(-Intake_Power);
	}
	public void intakeStop()
	{
		Util.consoleLog();
		intakeSet(0);
	}
	public void fire()
	{
		Util.consoleLog();
		shooterSet(Shooter_Power);
	}
	public void ceaseFire()
	{
		Util.consoleLog();
		shooterSet(0);
	}
}
