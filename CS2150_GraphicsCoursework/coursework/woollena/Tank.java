package coursework.woollena;

import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Tank {
	
	private Vector3f tankPosition;
	private float turretRotation;
	
	private LinkedList<Bullet> bullets;

	/**
	 * Constructs a tank object, which can be drawn and fire bullets
	 * @param posX the position of the tank on the x axis
	 * @param posY the position of the tank on the y axis
	 * @param posZ the position of the tank on the z axis
	 */
	public Tank(float posX, float posY, float posZ){
		tankPosition = new Vector3f(posX, posY, posZ);
		turretRotation = 0;
		bullets = new LinkedList<Bullet>();
	}
	
	/**
	 * Draws the tank into the world, based on it's current position
	 * @param tankBodyTexture the texture for the main body of the tank
	 * @param tankTurretTexture the texture for the turret block
	 * @param tankBarrelTexture the texture for the barrel of the tank's turret
	 */
	public void draw(Texture tankBodyTexture, Texture tankTurretTexture, Texture tankBarrelTexture){
		//Draw tank body
		GL11.glPushMatrix();
		{
			GL11.glTranslatef(tankPosition.x, tankPosition.y, tankPosition.z);
			GL11.glPushMatrix();
			{
				// enable texturing and bind an appropriate texture
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tankBodyTexture.getTextureID());

				GL11.glCallList(CS2150Coursework.LIST_TANK_BODY);//Tank body

				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			GL11.glPopMatrix();

			//Draw tank turret
			GL11.glPushMatrix();
			{
				//This texture binding will be applied to turret, barrel, and cap
				// enable texturing and bind an appropriate texture
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tankTurretTexture.getTextureID());
				
				GL11.glRotatef(turretRotation, 0.0f, 1.0f, 0.0f);
				GL11.glTranslatef(0.0f, 0.381f, 0.0f);
				GL11.glPushMatrix();
				{
					
					GL11.glCallList(CS2150Coursework.LIST_TANK_TURRET);//Tank turret
				}
				GL11.glPopMatrix();

				//Draw tank turret barrel
				GL11.glPushMatrix();
				{
					GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
					/*
					 *  y = turret height / 2
					 *  y = 0.19 / 2
					 *  y = 0.095
					 */
					GL11.glTranslatef(0.0f, 0.095f, 0.135f);
					GL11.glPushMatrix();
					{
						/*
						 * Pasted method params here because they're not shown by eclipse for this
						 * draw(float baseRadius, float topRadius, float height, int slices, int stacks)
						 */
						new Cylinder().draw(0.05f, 0.05f, 0.4f, 10, 1);//Tank turret barrel
					}
					GL11.glPopMatrix();


					//Bullet stuff was here


					//Draw tank turret barrel cap
					GL11.glPushMatrix();
					{
						GL11.glTranslatef(0.0f, 0.0f, 0.4f);
						GL11.glPushMatrix();
						{
							//GL11.glRotatef(180, 0.0f, 1.0f, 0.0f);
							CS2150Coursework.drawCircle(0.05f, 10);//Tank turret barrel cap
						}
						GL11.glPopMatrix();

						//Draw next thing

					}
					GL11.glPopMatrix();
					
					
					GL11.glDisable(GL11.GL_TEXTURE_2D);
				}
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
		
		drawBullets();
	}
	
	/**
	 * Adds a bullet fired from this tank, that will be rendered with the tank
	 * @param animationScale the animation scale to be used when the bullet travels
	 */
	public void fireBullet(){
		Bullet newBullet = new Bullet(tankPosition.x, tankPosition.y, tankPosition.z, turretRotation);
		bullets.add(newBullet);
	}
	
	/**
	 * Moves the tank the specified distance in the frame being rendered
	 * @param x the distance to move in the x direction
	 * @param z the distance to move in the z direction
	 */
	public void move(float x, float z){
		tankPosition.translate(x, 0.0f, z);
	}
	
	/**
	 * Sets the turret rotation in degrees
	 * @param angle the angle of the turret
	 */
	public void setTurretRotation(float angle){
		turretRotation = angle;
	}
	
	public Vector3f getPosition(){
		return tankPosition;
	}
	
	private void drawBullets(){
		/*
		 * for every bullet:
		 *     get current position + rotation
		 *     translate by amount to make travel
		 */

		Iterator<Bullet> it = bullets.iterator();
		while (it.hasNext()) {
			Bullet bullet = it.next();
			if(System.currentTimeMillis() - bullet.getTimeCreated() > 4000){
				it.remove();
			}

			//Draw bullet
			GL11.glPushMatrix();
			{
				//Transformations from the other shapes
				GL11.glTranslatef(bullet.getNextPosX(), bullet.getNextPosY(), bullet.getNextPosZ());
				GL11.glRotatef(bullet.getRotation(), 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(90, 0.0f, 1.0f, 0.0f);
				GL11.glTranslatef(0.0f, 0.381f, 0.0f);
				GL11.glTranslatef(0.0f, 0.095f, 0.135f);
				//My transformation to fire it from the end of the barrel 
				GL11.glTranslatef(0.0f, 0.0f, 0.35f);

				//Draw bullet body
				GL11.glPushMatrix();
				{
					/*
					 * Pasted method params here because they're not shown by eclipse for this
					 * draw(float baseRadius, float topRadius, float height, int slices, int stacks)
					 */
					new Cylinder().draw(0.03f, 0.03f, 0.15f, 10, 1);//Bullet body
				}
				GL11.glPopMatrix();

				//Draw bullet tip
				GL11.glPushMatrix();
				{
					GL11.glTranslatef(0.0f, 0.0f, 0.15f);
					GL11.glPushMatrix();
					{
						/*
						 * Pasted method params here because they're not shown by eclipse for this
						 * draw(float baseRadius, float topRadius, float height, int slices, int stacks)
						 */
						new Cylinder().draw(0.03f, 0.0f, 0.05f, 10, 10);//Bullet tip
					}
					GL11.glPopMatrix();

					//Draw next thing

				}
				GL11.glPopMatrix();


				//Draw bullet back
				GL11.glPushMatrix();
				{
					GL11.glPushMatrix();
					{
						GL11.glRotatef(180, 0.0f, 1.0f, 0.0f);
						CS2150Coursework.drawCircle(0.03f, 10);//Bullet tip
					}
					GL11.glPopMatrix();

					//Draw next thing

				}
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
		}
	}
	
}
