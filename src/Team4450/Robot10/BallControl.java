package Team4450.Robot10;

import Team4450.Lib.Util;
import Team4450.Robot10.Robot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class BallControl {
	private final Robot robot;
	private final Spark	intakeMotor = new Spark(0); //TODO get Port numbers;
	final Talon shooterMotor1 = new Talon(2);
	private final Talon ShooterIndexer = new Talon (1);
	private final Talon shooterFeederMotor = new Talon(3);
	final Encoder encoder = new Encoder(3, 4, true, EncodingType.k4X);
	public Counter tlEncoder = new Counter(0);
	public double Intake_Power, Shooter_HIGHPower, Shooter_LOWPower, Shooter_HIGHRPM, Shooter_LOWRPM, Indexer_Power, Feeder_Power, PVALUE, IVALUE, DVALUE;
	private final PIDController shooterPidController;
	public ShooterSpeedSource shooterSpeedSource = new ShooterSpeedSource(tlEncoder);
	
	BallControl (Robot robot)

	{
		Util.consoleLog();
		this.robot = robot;
		Intake_Power= 0.8; //TODO Get true power readouts
		Indexer_Power = -0.25;
		Feeder_Power =0.8;
		intakeStop();
		choke();
		encoder.reset();
		tlEncoder.reset();
		tlEncoder.setDistancePerPulse(1);
		tlEncoder.setPIDSourceType(PIDSourceType.kRate);
		shooterPidController = new PIDController(0.0, 0.0, 0.0, shooterSpeedSource, shooterMotor1);
		ceaseFire();
		Util.consoleLog("Is ShooterPID Null: " + (shooterPidController == null));
		Util.consoleLog("Is Counter Null: " + (tlEncoder == null));
		if (robot.isComp)
		{
			Shooter_LOWPower = .50;
			Shooter_HIGHPower = .45;
			Shooter_LOWRPM = 2500;
			Shooter_HIGHRPM = 3200;
			PVALUE = .0025;
			IVALUE = .0025;
			DVALUE = .005;  //003 	
		}
		else
		{
			Shooter_LOWPower = .50;
			Shooter_HIGHPower = .80;
			Shooter_LOWRPM = 2500;
			Shooter_HIGHRPM = 3200;
			PVALUE = .0025;
			IVALUE = .0025;
			DVALUE = .003; 
		}
	}
	public void dispose()
	{
		Util.consoleLog("Is Counter Null: " + (tlEncoder == null));
		Util.consoleLog();
		if (shooterPidController != null)
		{
			if (shooterPidController.isEnabled()) shooterPidController.disable();
			shooterPidController.free();
		}
		if (shooterMotor1 != null) shooterMotor1.free();
		if (ShooterIndexer != null) ShooterIndexer.free();
		if (shooterFeederMotor !=null) shooterFeederMotor.free();
		if (encoder != null) encoder.free();
		if (intakeMotor != null) intakeMotor.free();
		
		if (tlEncoder != null) tlEncoder.free();

	}
	public void intakeSet(double power)
	{
		Util.consoleLog("%f", power);
		if (power == 0)
		{
			intakeMotor.set(0);
		}
		else
		{
		intakeMotor.set(power);
		}
		if (power != 0)
		{
			Util.consoleLog("Ball Intake Motor Active");
			SmartDashboard.putBoolean("BallPickupMotor", true);
		}
		else
		{
			Util.consoleLog("Ball Intake Motor Stopped");
			SmartDashboard.putBoolean("BallPickupMotor", false);
		}
	}
	public void shooterSet(double power)
	{
		Util.consoleLog("%f", power);
		
		if (SmartDashboard.getBoolean("PIDEnabled", false))
		{ 
			if (power == 0) {
				shooterMotor1.set(0);
			}
			if (power == Shooter_LOWPower)
			{
				holdShooterRPM(SmartDashboard.getNumber("LowSetting", Shooter_LOWRPM));
			}
			else if (power == Shooter_HIGHPower)
			{
				holdShooterRPM(SmartDashboard.getNumber("HighSetting", Shooter_HIGHRPM));
			}
			else
				shooterMotor1.set(power);
		}
		else
		{
			shooterMotor1.set(power);
		}
		if (power != 0)
		{
			Util.consoleLog("Shooter Motors Active");
			SmartDashboard.putBoolean("ShooterMotor", true);
		}
		else
		{
			Util.consoleLog("Shooter Motors Stopped");
			SmartDashboard.putBoolean("ShooterMotor", false);
		}
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
	public void load()
	{
		Util.consoleLog("Loading the cannon");
		ShooterIndexer.set(Indexer_Power);
		//shooterFeederMotor.set(Feeder_Power);
		SmartDashboard.putBoolean("DispenserMotor", true);
	}
	public void Swab()
	{
		Util.consoleLog("Ceasing Loading");
		ShooterIndexer.set(0);
		shooterFeederMotor.set(0);
		SmartDashboard.putBoolean("DispenserMotor", false);
	}
	public void fire()
	{
		Util.consoleLog();
		load();
		shooterSet(Shooter_HIGHPower);
	}
	public void ceaseFire()
	{
		Util.consoleLog();
		shooterSet(0);
		Swab();
	}
	public void feed()
	{
		Util.consoleLog();
		shooterFeederMotor.set(0.45);
	}
	public void choke()
	{
		Util.consoleLog();
		shooterFeederMotor.set(0);
	}
	public void vomit()
	{
		Util.consoleLog();
		shooterFeederMotor.set(-.45);
		SmartDashboard.putBoolean("DispenserMotor", true);
	}
	private void holdShooterRPM(double RPM)
	{
		double pValue = SmartDashboard.getNumber("PValue", PVALUE);
		double iValue = SmartDashboard.getNumber("IValue", IVALUE);
		double dValue = SmartDashboard.getNumber("DValue", DVALUE);	
		Util.consoleLog("RPM =%.0f p=%.4f i=%.4f d-%.4f", RPM, pValue, iValue, dValue);
		shooterPidController.setPID(pValue, iValue, dValue, 0.0);
		shooterPidController.setSetpoint(RPM/60);
		shooterPidController.setPercentTolerance(5);
		shooterPidController.setToleranceBuffer(4096);
		shooterPidController.setContinuous();
		shooterSpeedSource.reset();
		shooterPidController.enable();
		
	}
	public class ShooterSpeedSource implements PIDSource
	{
		//private Encoder encoder;
		private Counter counter;
		private int inversion = -1;
		private double rpmAccumulator, rpmSampleCount;
		
		//public ShooterSpeedSource(Encoder encoder)
		//{
			//this.encoder = encoder;
		//}
		public ShooterSpeedSource(Counter counter)
		{
			this.counter = counter;
		}
		
		@Override
		public void setPIDSourceType(PIDSourceType pidSource)
		{
			//if (encoder!= null) encoder.setPIDSourceType(pidSource);
			if (counter != null) counter.setPIDSourceType(pidSource);
			
		}
		
		@Override
		public PIDSourceType getPIDSourceType()
		{
			//if (encoder != null) return encoder.getPIDSourceType();
			if (counter != null) return counter.getPIDSourceType();
			
			return null;
		}
		public void setInverted(boolean inverted)
		{
			if(inverted)
				inversion = -1;
			else
				inversion = 1;
		}
		public int get()
		{
			//if (encoder != null) return encoder.get() * inversion;
			if (counter != null) return counter.get() * inversion;
			return 0;
		}
		public double getRate()
		{
			//if (encoder != null) return encoder.getRate() * inversion;
			if (counter != null) return counter.getRate() * inversion;
			return 0;
		}
		@Override
		public double pidGet()
		{
			if (encoder != null)
			{
			if (encoder.getPIDSourceType() == PIDSourceType.kRate)
				return getRate();
			else
				return get();
			}
			else if (counter != null)
			{
				if (counter.getPIDSourceType() == PIDSourceType.kRate)
					return getRate();
				else
					return get();
			}
			else
				return 0;
		}
		public void reset()
		{
			rpmAccumulator = rpmSampleCount = 0;
		//	if (encoder != null) encoder.reset();
			if (counter != null) counter.reset();
		}
		
	}
}
