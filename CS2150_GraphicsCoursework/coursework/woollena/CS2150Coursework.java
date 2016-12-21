/* CS2150Coursework.java
 * Username: woollena
 * Name: Adam Woollen
 * Student number: 159138164
 *
 * Scene Graph:
 * 	Scene origin
 *  |
 *  +-- [T(0, 0, 1)] Ground (Cube)
 *  |   |
 *  |   +-- [T(0, 0, 0.2)] Tank body (custom shape)
 *  |   |   |
 *  |   |   +-- [T()] Tank turret (custom shape)
 *  |   |       |
 *  |   |       +-- [] Tank barrel (cylinder)
 *  |   |           |
 *  |   |           +-- [] Tank cap (circle)
 *  |   |
 *  |   +-- [] Any bullet bodies (cylinder)
 *  |   |   |
 *  |   |   +-- [] Any bullet caps (cone from cylinder)
 *  |   |   |
 *  |   |   +-- [] Any bullet backs (circle)
 *  
 *  
 *  
 *  |   |       |
 *  |   |       +-- [] Left upper arm (Cube)
 *  |   |       |   |
 *  |   |       |   +-- [] Left lower arm (Cube)
 *  |   |       |
 *  |   |       +-- [] Right upper arm (Cube)
 *  |   |       |   |
 *  |   |       |   +-- [] Right lower arm (Cube)
 *  |   |       |
 *  |   |       +-- [] Head (Cube)
 *  |   |       |
 *  |   |       +-- [Ry(90)] Mini-gun mount (Cylinder)
 *  |   |           |
 *  |   |           +-- [] Mini-gun (Cylinder)
 *  |   |
 *  |   +-- [] Left leg upper (Cube)
 *  |   |   |
 *  |   |   +-- [] Left leg mid (Cube)
 *  |   |       |
 *  |   |       +-- [] Left leg lower (Cube)
 *  |   |           |
 *  |   |           +-- [] Left foot (Cube)
 *  |   |
 *  |   +-- [] Right leg upper (Cube)
 *  |   |   |
 *  |   |   +-- [] Right leg mid (Cube)
 *  |   |       |
 *  |   |       +-- [] Right leg lower (Cube)
 *  |   |           |
 *  |   |           +-- [] Right foot (Cube)
 *  |
 *  
 *
 */
package coursework.woollena;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Vector3f;

import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;
import GraphicsLab.*;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;

/**
 * <p>This project is a tank game, where the player must use their skills to destroy all enemy tanks.</p>
 * <p>The player may move the tank, aim the turret, and fire.</p>
 * 
 * <p>Game controls:
 * <ul>
 * <li>WASD to move the tank</li>
 * <li>Left and right arrow keys to rotate the turret</li>
 * <li>Space to fire!</li>
 * </ul>
 * </p>
 * <p>Development controls:
 * <ul>
 * <li>Press the escape key to exit the application
 * <li>Hold the x, y and z keys to view the scene along the x, y and z axis, respectively
 * <li>While viewing the scene along the x, y or z axis, use the up and down cursor keys
 *      to increase or decrease the viewpoint's distance from the scene origin
 * </ul>
 * </p>
 * TODO: Add any additional controls for your sample to the list above
 *
 */
public class CS2150Coursework extends GraphicsLab
{
	//Variables for FPS printing
	double lastTime = System.currentTimeMillis();
	int frames = 0;
	int updateMillis = 500;

	/** display list id for the tank body */
	private final int LIST_TANK_BODY = 1;
	/** display list id for the tank turret */
	private final int LIST_TANK_TURRET = 2;

	/** ids for nearest, linear and mipmapped textures for testing */
	private Texture testTexture;
	/** 
	 * ids for nearest, linear and mipmapped textures for the tank body
	 * <p>Sources:
	 * <ul>
	 * <li>Tracks - https://freetextures.3dtotal.com/preview.php?imi=7216&s=tank&p=0&cid=</li>
	 * <li>Wheels - http://www.flamesofwar.com/Default.aspx?tabid=110&art_id=998</li>
	 * </ul>
	 * </p>
	 */
	private Texture tankMainTexture;
	/** 
	 * ids for nearest, linear and mipmapped textures for the tank body
	 * <p>Sources:
	 * <ul>
	 * <li>Wood - http://gnrbishop.deviantart.com/art/Wood-floor-86913934</li>
	 * </ul>
	 * </p>
	 */
	private Texture woodFloorTexture;
	/** 
	 * ids for nearest, linear and mipmapped textures for the transparent plane
	 */
	private Texture plainWhiteTexture;

	//Animation variables
	private Vector3f tankPosition;
	private float turretRotation;
	private LinkedList<BulletData> bullets;
	private boolean makeBullet;
	private boolean lastBulletInputState;

	java.nio.FloatBuffer mouseWorldPos;
	float moveLevelX;
	float moveLevelY;
	float moveLevelZ;


	//TODO: Feel free to change the window title and default animation scale here
	public static void main(String args[])
	{   
		new CS2150Coursework().run(WINDOWED, "CS2150 Coursework Submission - Tanks", 0.01f);
	}

	protected void initScene() throws Exception
	{
		//TODO: Initialise your resources here - might well call other methods you write.
		//Set animation field values
		tankPosition = new Vector3f(0.0f, 0.0f, -2.0f);
		turretRotation = 0;
		bullets = new LinkedList<BulletData>();
		makeBullet = false;
		lastBulletInputState = false;

		mouseWorldPos = BufferUtils.createFloatBuffer(3);
		mouseWorldPos.put(0, 0.0f);
		mouseWorldPos.put(1, 0.0f);
		mouseWorldPos.put(2, 0.0f);

		moveLevelX = 0.0f;
		moveLevelY = -7.0f;
		moveLevelZ = -7.0f;

		//Load the textures
		testTexture = loadTexture("coursework/woollena/textures/test.bmp");
		tankMainTexture = loadTexture("coursework/woollena/textures/tankTextureMain.bmp");
		woodFloorTexture = loadTexture("coursework/woollena/textures/woodFloor.bmp");
		plainWhiteTexture = loadTexture("coursework/woollena/textures/plainWhite.bmp");

		// Global ambient light level
		float globalAmbient[] = {1f,  1f,  1f, 1.0f};
		// Set the global ambient lighting
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(globalAmbient));



		// Define light properties in RGBA
		float diffuse0[] = {0.9f,  0.9f, 0.9f, 1.0f};
		float ambient0[] = {0.9f,  0.9f, 0.9f, 1.0f};
		//Define light location in xyz?
		float position0[] = {0.0f, 1.0f, 0.0f, 1.0f};

		// Supply OpenGL with the properties for the first light
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, FloatBuffer.wrap(ambient0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, FloatBuffer.wrap(diffuse0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(position0));
		// Enable the first light
		GL11.glEnable(GL11.GL_LIGHT0);

		// enable lighting calculations
		GL11.glEnable(GL11.GL_LIGHTING);
		// ensure that all normals are re-normalised after transformations automatically
		GL11.glEnable(GL11.GL_NORMALIZE);

		//Prepare lists
		GL11.glNewList(LIST_TANK_BODY, GL11.GL_COMPILE);
		{
			drawUnitTankBody();
		}
		GL11.glEndList();

		GL11.glNewList(LIST_TANK_TURRET, GL11.GL_COMPILE);
		{
			drawUnitTankTurret();
		}
		GL11.glEndList();




		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		try {
			org.lwjgl.input.Cursor emptyCursor = new org.lwjgl.input.Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
			Mouse.setNativeCursor(emptyCursor);
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void checkSceneInput()
	{
		//TODO: Check for keyboard and mouse input here

		//No longer needed because turret now follows mouse
		/*if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			turretRotation = (turretRotation - (5.0f * getAnimationScale())) % 360;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			turretRotation = (turretRotation + (5.0f * getAnimationScale())) % 360;
		}*/

		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			tankPosition.translate(-0.13f * getAnimationScale(), 0, 0);
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			tankPosition.translate(0.13f * getAnimationScale(), 0, 0);
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			tankPosition.translate(0, 0, -0.13f * getAnimationScale());
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			tankPosition.translate(0, 0, 0.13f * getAnimationScale());
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)){
			if(lastBulletInputState == false){
				makeBullet = true;
			}
			lastBulletInputState = true;
		} else{
			lastBulletInputState = false;
		}

	}

	protected void updateScene()
	{
		//TODO: Update your scene variables here - remember to use the current animation scale value
		//        (obtained via a call to getAnimationScale()) in your modifications so that your animations
		//        can be made faster or slower depending on the machine you are working on



	}

	protected void renderScene()
	{
		//TODO: Render your scene here - remember that a scene graph will help you write this method! 
		//      It will probably call a number of other methods you will write.

		//Print frames per second
		double currentTime = System.currentTimeMillis();
		frames++;
		if (currentTime - lastTime >= updateMillis){
			System.out.println(frames * (1000 / updateMillis));
			frames = 0;
			lastTime = System.currentTimeMillis();
		}


		GL11.glTranslatef(moveLevelX, moveLevelY, moveLevelZ);


		//Draw ground
		GL11.glPushMatrix();
		{

			GL11.glTranslatef(0.0f, -1.5f, 0.0f);
			GL11.glPushMatrix();
			{
				GL11.glTranslatef(0.0f, -0.5f, -0.0f);
				GL11.glScalef(10f, 1f, 10f);

				// enable texturing and bind an appropriate texture
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, woodFloorTexture.getTextureID());

				drawUnitCube();//Ground

				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			GL11.glPopMatrix();

			//Draw tank body
			GL11.glPushMatrix();
			{
				GL11.glTranslatef(tankPosition.x, tankPosition.y, tankPosition.z);
				GL11.glPushMatrix();
				{
					// enable texturing and bind an appropriate texture
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, tankMainTexture.getTextureID());

					GL11.glCallList(LIST_TANK_BODY);//Tank body

					GL11.glDisable(GL11.GL_TEXTURE_2D);
				}
				GL11.glPopMatrix();

				//Draw tank turret
				GL11.glPushMatrix();
				{
					GL11.glRotatef(turretRotation, 0.0f, 1.0f, 0.0f);
					GL11.glTranslatef(0.0f, 0.381f, 0.0f);
					GL11.glPushMatrix();
					{
						GL11.glCallList(LIST_TANK_TURRET);//Tank turret
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



						if(makeBullet){
							BulletData newBullet = new BulletData(getAnimationScale(), tankPosition.x, tankPosition.y, tankPosition.z, turretRotation);
							bullets.add(newBullet);
							makeBullet = false;
						}




						//Draw tank turret barrel cap
						GL11.glPushMatrix();
						{
							GL11.glTranslatef(0.0f, 0.0f, 0.4f);
							GL11.glPushMatrix();
							{
								//GL11.glRotatef(180, 0.0f, 1.0f, 0.0f);
								drawCircle(0.05f, 10);//Tank turret barrel cap
							}
							GL11.glPopMatrix();

							//Draw next thing

						}
						GL11.glPopMatrix();
					}
					GL11.glPopMatrix();
				}
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();


			/*
			 * for every bullet:
			 *     get current position + rotation
			 *     translate by amount to make travel
			 */

			Iterator<BulletData> it = bullets.iterator();
			while (it.hasNext()) {
				BulletData bullet = it.next();
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
						GL11.glTranslatef(0.0f, 0.0f, 0.0f);
						GL11.glPushMatrix();
						{
							GL11.glRotatef(180, 0.0f, 1.0f, 0.0f);
							/*
							 * Pasted method params here because they're not shown by eclipse for this
							 * draw(float baseRadius, float topRadius, float height, int slices, int stacks)
							 */
							drawCircle(0.03f, 10);//Bullet tip
						}
						GL11.glPopMatrix();

						//Draw next thing

					}
					GL11.glPopMatrix();
				}
				GL11.glPopMatrix();
			}



		}
		GL11.glPopMatrix();


		/*
		 * Create shape above floor so bullets are correctly aligned with mouse
		 */
		GL11.glPushMatrix();
		{
			GL11.glTranslatef(0.0f, -1.0f, 0.0f);
			GL11.glScalef(10, 1, 10);
			// enable texturing and bind an appropriate texture
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, plainWhiteTexture.getTextureID());
			drawInvisibleUnitPlane();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();


		/*******************
		 * Mouse tracking! *
		 *******************/


		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();

		java.nio.FloatBuffer z = BufferUtils.createFloatBuffer(1);
		GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, z);



		// Create FloatBuffer that can hold 16 values.
		java.nio.FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
		// Get current model view matrix:
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);

		// Create FloatBuffer that can hold 16 values.
		java.nio.FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
		// Get current model view matrix:
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);

		// Create FloatBuffer that can hold 16 values.
		java.nio.IntBuffer viewport = BufferUtils.createIntBuffer(16);
		// Get current model view matrix:
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);


		GLU.gluUnProject((float) mouseX, (float) mouseY, z.get(0), modelviewMatrix, projectionMatrix, viewport, mouseWorldPos);

		/*GL11.glPushMatrix();
		{
			GL11.glTranslatef(mouseWorldPos.get(0), mouseWorldPos.get(1), mouseWorldPos.get(2));
			GL11.glScalef(0.5f, 0.5f, 0.5f);
			GL11.glTranslatef(0.0f, 0.5f, 0.0f);

			drawUnitCube();
		}
		GL11.glPopMatrix();*/

		//                               atan2(a.y-o.y, a.x-o.x) - atan2(b.y-o.y, b.x-o.x)
		turretRotation = (float) Math.toDegrees(Math.atan2(1, 0) - Math.atan2(mouseWorldPos.get(2) - tankPosition.z, mouseWorldPos.get(0) - tankPosition.x)) - 90;








		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		{
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0, 800, 600, 0.0, -1.0, 10.0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			{
				GL11.glLoadIdentity();
				GL11.glDisable(GL11.GL_CULL_FACE);

				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

				GL11.glBegin(GL11.GL_QUADS);
				{
					float fixedMouseY = 600 - Mouse.getY();

					GL11.glVertex2f(Mouse.getX() - 5, fixedMouseY - 5);
					GL11.glVertex2f(Mouse.getX() + 5, fixedMouseY - 5);
					GL11.glVertex2f(Mouse.getX() + 5, fixedMouseY + 5);
					GL11.glVertex2f(Mouse.getX() - 5, fixedMouseY + 5);
				}
				GL11.glEnd();

				// Making sure we can render 3d again
				GL11.glMatrixMode(GL11.GL_PROJECTION);
			}
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}
		GL11.glPopMatrix();

	}

	protected void setSceneCamera()
	{
		// Call the default behaviour defined in GraphicsLab. This will set a default perspective projection
		// and default camera settings ready for some custom camera positioning below...  
		super.setSceneCamera();

		//TODO: If it is appropriate for your scene, modify the camera's position and orientation here
		//        using a call to GL11.gluLookAt(...)

		GLU.gluLookAt(0, 0, 0, 0, -7, -5, 0, 1, 0);

	}

	protected void cleanupScene()
	{
		//TODO: Clean up your resources here
	}

	private void setStandardMaterial()
	{
		// How shiny are the faces (specular exponent)
		float shininess = 2.0f;
		// Specular reflection of the faces
		float specular[] = {0.5f, 0.5f, 0.5f, 1.0f};
		// Diffuse reflection of the faces
		float diffuse[] = {0.5f, 0.5f, 0.5f, 1.0f};

		// Set the material properties for the house using OpenGL
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(specular));
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
	}

	/**
	 * Draws a cube of unit length, width and height using the current OpenGL material settings
	 */
	private void drawUnitCube()
	{
		// The vertices for the cube (note that all sides have a length of 1)
		Vertex v1 = new Vertex(-0.5f, -0.5f,  0.5f);
		Vertex v2 = new Vertex(-0.5f,  0.5f,  0.5f);
		Vertex v3 = new Vertex( 0.5f,  0.5f,  0.5f);
		Vertex v4 = new Vertex( 0.5f, -0.5f,  0.5f);
		Vertex v5 = new Vertex(-0.5f, -0.5f, -0.5f);
		Vertex v6 = new Vertex(-0.5f,  0.5f, -0.5f);
		Vertex v7 = new Vertex( 0.5f,  0.5f, -0.5f);
		Vertex v8 = new Vertex( 0.5f, -0.5f, -0.5f);

		// draw the near face:
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v3.toVector(),v2.toVector(),v1.toVector(),v4.toVector()).submit();

			GL11.glTexCoord2f(1f, 1f);
			v3.submit();

			GL11.glTexCoord2f(0, 1f);
			v2.submit();

			GL11.glTexCoord2f(0.0f, 0.0f);
			v1.submit();

			GL11.glTexCoord2f(1f, 0.0f);
			v4.submit();
		}
		GL11.glEnd();

		// draw the left face:
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v2.toVector(),v6.toVector(),v5.toVector(),v1.toVector()).submit();

			v2.submit();
			v6.submit();
			v5.submit();
			v1.submit();
		}
		GL11.glEnd();

		// draw the right face:
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v7.toVector(),v3.toVector(),v4.toVector(),v8.toVector()).submit();

			v7.submit();
			v3.submit();
			v4.submit();
			v8.submit();
		}
		GL11.glEnd();

		// draw the top face:
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v7.toVector(),v6.toVector(),v2.toVector(),v3.toVector()).submit();

			GL11.glTexCoord2f(1.0f, 1.0f);
			v7.submit();

			GL11.glTexCoord2f(0.0f, 1.0f);
			v6.submit();

			GL11.glTexCoord2f(0.0f, 0.0f);
			v2.submit();

			GL11.glTexCoord2f(1.0f, 0.0f);
			v3.submit();
		}
		GL11.glEnd();

		// draw the bottom face:
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(),v1.toVector(),v5.toVector(),v8.toVector()).submit();

			v4.submit();
			v1.submit();
			v5.submit();
			v8.submit();
		}
		GL11.glEnd();

		// draw the far face:
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(),v7.toVector(),v8.toVector(),v5.toVector()).submit();

			v6.submit();
			v7.submit();
			v8.submit();
			v5.submit();
		}
		GL11.glEnd();
	}

	/**
	 * Draws a unit tank body 1*y*1
	 */
	private void drawUnitTankBody()
	{
		//The vertices for the tank body
		Vertex v1 = new Vertex(-0.375f, 0.381f, 0.5f);
		Vertex v2 = new Vertex(0.375f, 0.381f, 0.5f);
		Vertex v3 = new Vertex(0.5f, 0.19f, 0.5f);
		Vertex v4 = new Vertex(0.375f, 0.0f, 0.5f);
		Vertex v5 = new Vertex(-0.375f, 0.0f, 0.5f);
		Vertex v6 = new Vertex(-0.5f, 0.19f, 0.5f);
		Vertex v7 = new Vertex(-0.375f, 0.381f, -0.5f);
		Vertex v8 = new Vertex(0.375f, 0.381f, -0.5f);
		Vertex v9 = new Vertex(0.5f, 0.19f, -0.5f);
		Vertex v10 = new Vertex(0.375f, 0.0f, -0.5f);
		Vertex v11 = new Vertex(-0.375f, 0.0f, -0.5f);
		Vertex v12 = new Vertex(-0.5f, 0.19f, -0.5f);


		//Draw face near
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(), v5.toVector(), v4.toVector()).submit();

			GL11.glTexCoord2f(0.895f, 0.0f);
			v6.submit();

			GL11.glTexCoord2f(1.0f, 0.1f);
			v5.submit();

			GL11.glTexCoord2f(1.0f, 0.9f);
			v4.submit();

			GL11.glTexCoord2f(0.895f, 1.0f);
			v3.submit();

			GL11.glTexCoord2f(0.79f, 0.9f);
			v2.submit();

			GL11.glTexCoord2f(0.79f, 0.1f);
			v1.submit();
		}
		GL11.glEnd();

		//Draw face far
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v7.toVector(), v8.toVector(), v9.toVector()).submit();

			v7.submit();
			v8.submit();
			v9.submit();
			v10.submit();
			v11.submit();
			v12.submit();
		}
		GL11.glEnd();

		//Draw face left-top
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v7.toVector(), v12.toVector()).submit();

			GL11.glTexCoord2f(0.0f, 0.79f);
			v1.submit();

			GL11.glTexCoord2f(0.79f, 0.79f);
			v7.submit();

			GL11.glTexCoord2f(0.79f, 1.0f);
			v12.submit();

			GL11.glTexCoord2f(0.0f, 1.0f);
			v6.submit();
		}
		GL11.glEnd();

		//Draw face left-bottom
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v6.toVector(), v12.toVector()).submit();

			v5.submit();
			v6.submit();
			v12.submit();
			v11.submit();
		}
		GL11.glEnd();

		//Draw face right-top
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v2.toVector(), v3.toVector(), v9.toVector()).submit();

			GL11.glTexCoord2f(0.0f, 0.21f);
			v2.submit();

			GL11.glTexCoord2f(0.0f, 0.0f);
			v3.submit();

			GL11.glTexCoord2f(0.79f, 0.0f);
			v9.submit();

			GL11.glTexCoord2f(0.79f, 0.21f);
			v8.submit();
		}
		GL11.glEnd();

		//Draw face right-bottom
		GL11.glBegin(GL11.GL_POLYGON);
		{

			new Normal(v3.toVector(), v4.toVector(), v10.toVector()).submit();

			v3.submit();
			v4.submit();
			v10.submit();
			v9.submit();
		}
		GL11.glEnd();

		//Draw face top
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v2.toVector(), v8.toVector()).submit();

			GL11.glTexCoord2f(0.0f, 1.0f);
			v1.submit();

			GL11.glTexCoord2f(0.0f, 0.21f);
			v2.submit();

			GL11.glTexCoord2f(0.79f, 0.21f);
			v8.submit();

			GL11.glTexCoord2f(0.79f, 1.0f);
			v7.submit();
		}
		GL11.glEnd();

		//Draw face bottom
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), v5.toVector(), v11.toVector()).submit();

			v4.submit();
			v5.submit();
			v11.submit();
			v10.submit();
		}
		GL11.glEnd();
	}

	/**
	 * Draws a tank turret
	 */
	protected void drawUnitTankTurret()
	{
		//The vertices for the tank body
		Vertex v1 = new Vertex(-0.167f, 0.19f, 0.143f);
		Vertex v2 = new Vertex(0.119f, 0.19f, 0.143f);
		Vertex v3 = new Vertex(0.19f, 0.0f, 0.19f);
		Vertex v4 = new Vertex(-0.19f, 0.0f, 0.19f);
		Vertex v5 = new Vertex(-0.167f, 0.19f, -0.143f);
		Vertex v6 = new Vertex(0.119f, 0.19f, -0.143f);
		Vertex v7 = new Vertex(0.19f, 0.0f, -0.19f);
		Vertex v8 = new Vertex(-0.19f, 0.0f, -0.19f);


		//Draw face near
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v4.toVector(), v3.toVector()).submit();

			v1.submit();
			v4.submit();
			v3.submit();
			v2.submit();
		}
		GL11.glEnd();

		//Draw face far
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v6.toVector(), v7.toVector()).submit();

			v5.submit();
			v6.submit();
			v7.submit();
			v8.submit();
		}
		GL11.glEnd();

		//Draw face left
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v5.toVector(), v8.toVector()).submit();

			v1.submit();
			v5.submit();
			v8.submit();
			v4.submit();
		}
		GL11.glEnd();

		//Draw face right
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v2.toVector(), v3.toVector(), v7.toVector()).submit();

			v2.submit();
			v3.submit();
			v7.submit();
			v6.submit();
		}
		GL11.glEnd();

		//Draw face top
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v2.toVector(), v6.toVector()).submit();

			v1.submit();
			v2.submit();
			v6.submit();
			v5.submit();
		}
		GL11.glEnd();

		//Draw face bottom
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v3.toVector(), v4.toVector(), v8.toVector()).submit();

			v3.submit();
			v4.submit();
			v8.submit();
			v7.submit();
		}
		GL11.glEnd();
	}

	/**
	 * Draws a circle centred about the z axis
	 * @param radius the radius of the circle
	 * @param slices the number of points the circle will be drawn from. Larger = smoother
	 */
	private void drawCircle(float radius, int slices){
		float doublePi = (float) (Math.PI * 2);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		{
			for(int i = 0; i < slices; i++){
				new Vertex((float)(radius * Math.cos(i *  doublePi / slices)),
						(float)(radius * Math.sin(i * doublePi / slices)),
						0.0f).submit();
			}
		}
		GL11.glEnd();
	}

	private void drawInvisibleUnitPlane(){
		//The vertices for the invisible plane
		Vertex v1 = new Vertex(-0.5f, 0.0f,  -0.5f);
		Vertex v2 = new Vertex(0.5f,  0.0f,  -0.5f);
		Vertex v3 = new Vertex(0.5f,  0.0f,  0.5f);
		Vertex v4 = new Vertex(-0.5f, 0.0f,  0.5f);

		GL11.glEnable(GL11.GL_BLEND); //Enable blending.
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA); //Set blending function.
		GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.0f);
		
		//Draw face bottom
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), v3.toVector(), v2.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v4.submit();
			
			GL11.glTexCoord2f(1.0f, 0.0f);
			v3.submit();
			
			GL11.glTexCoord2f(1.0f, 1.0f);
			v2.submit();
			
			GL11.glTexCoord2f(0.0f, 1.0f);
			v1.submit();
		}
		GL11.glEnd();
		
		GL11.glDisable(GL11.GL_BLEND);
	}

}