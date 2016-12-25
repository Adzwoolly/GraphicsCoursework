package coursework.woollena;

public class Bullet {
	
	private static final float speed = 0.175f;
	
	private float tankPosX;
	private float tankPosY;
	private float tankPosZ;
	private float turretRotation;
	
	private float xComponent;
	private float yComponent;
	
	private long timeCreated;
	
	public Bullet(float tankPosX, float tankPosY, float tankPosZ, float turretRotation){
		this.tankPosX = tankPosX;
		this.tankPosY = tankPosY;
		this.tankPosZ = tankPosZ;
		this.turretRotation = turretRotation;
		
		xComponent = (float) (speed * Math.cos(Math.toRadians(0 - turretRotation)));
		yComponent = (float) (speed * Math.sin(Math.toRadians(0 - turretRotation)));
		
		timeCreated = System.currentTimeMillis();
	}
	
	public float getNextPosX(){
		tankPosX += (xComponent * CS2150Coursework.getDT());
		return tankPosX;
	}
	
	public float getNextPosY(){
		return tankPosY;
	}
	
	public float getNextPosZ(){
		tankPosZ += (yComponent * CS2150Coursework.getDT());
		return tankPosZ;
	}
	
	public float getRotation(){
		return turretRotation;
	}
	
	public long getTimeCreated(){
		return timeCreated;
	}
}
