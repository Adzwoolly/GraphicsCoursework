package coursework.woollena;

public class BulletData {
	
	private static final float speed = 0.175f;
	
	private float tankPosX;
	private float tankPosY;
	private float tankPosZ;
	private float turretRotation;
	
	private float xComponent;
	private float yComponent;
	
	private long timeCreated;
	
	public BulletData(float animationScale, float tankPosX, float tankPosY, float tankPosZ, float turretRotation){
		this.tankPosX = tankPosX;
		this.tankPosY = tankPosY;
		this.tankPosZ = tankPosZ;
		this.turretRotation = turretRotation;
		
		xComponent = (float) (speed * animationScale * Math.cos(Math.toRadians(0 - turretRotation)));
		yComponent = (float) (speed * animationScale * Math.sin(Math.toRadians(0 - turretRotation)));
		
		timeCreated = System.currentTimeMillis();
	}
	
	public float getNextPosX(){
		tankPosX += xComponent;
		return tankPosX;
	}
	
	public float getNextPosY(){
		return tankPosY;
	}
	
	public float getNextPosZ(){
		tankPosZ += yComponent;
		return tankPosZ;
	}
	
	public float getRotation(){
		return turretRotation;
	}
	
	public long getTimeCreated(){
		return timeCreated;
	}
}
