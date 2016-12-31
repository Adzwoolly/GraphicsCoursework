/* CS2150Coursework.java
 * Username: woollena
 * Name: Adam Woollen
 * Student number: 159138164
 *
 * Scene Graph:
 * 	Scene origin
 *  |
 *  +-- [T(0, -7.8, -7) T(0, -0.5, 0) S(10, 1, 10)] Ground (Cube)
 *      |
 *      +-- [T(tankPosX, tankPosY, tankPosZ)] Tank body (custom shape)
 *      |   |
 *      |   +-- [Ry(turretRotation) T(0, 0.381, 0)] Tank turret (custom shape)
 *      |       |
 *      |       +-- [Ry(90) T(0.0, 0.095, 0.135)] Tank turret barrel (cylinder)
 *      |           |
 *      |           +-- [T(0.0, 0.0, 0.4)] Tank turret barrel cap (circle)
 *      |
 *      +-- [T(bulletPosX, bulletPosY, bulletPosZ) Ry(90) T(0.0, 0.381, 0.0) T(0.0, 0.095, 0.135)] Any bullet bodies (cylinder)
 *          |
 *          +-- [T(0.0, 0.0, 0.15)] Any bullet caps (cone from cylinder)
 *          |
 *          +-- [Ry(180)] Any bullet backs (circle)
 *
 */
package coursework.woollena;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;
import GraphicsLab.*;
import sun.security.krb5.internal.LastReq;

/**
 * <p>This project is a tank game, where the player must use their skills to destroy all enemy tanks.</p>
 * <p>Unfortunately, only the player's tank has been implemented, and there is no bullet collision detection.</p>
 * <p>The player may move the tank, aim the turret, and fire.</p>
 * 
 * <p>Game controls:
 * <ul>
 * <li>WASD to move the tank</li>
 * <li>Aim the turret with the mouse</li>
 * <li>Space or mouse left click to fire!</li>
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
 * 
 * @author Adam Woollen
 * @version 1.0
 */
public class CS2150Coursework extends GraphicsLab
{
	//Variables for FPS printing
	double lastTime = System.currentTimeMillis();
	int frames = 0;
	int updateMillis = 500;

	/** display list id for the tank body */
	public final static int LIST_TANK_BODY = 1;
	/** display list id for the tank turret */
	public final static int LIST_TANK_TURRET = 2;

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
	private Texture transparencyBlackTexture;
	/** 
	 * ids for nearest, linear and mipmapped textures for the tank turret
	 * <p>Sources:
	 * <ul>
	 * <li>Damage effect (applied to blue colour) - https://www.youtube.com/watch?v=LK5mXWKj5Ks</li>
	 * </ul>
	 * </p>
	 */
	private Texture tankTurretTexture;

	//Animation variables
	private Tank playerTank;
	private ArrayList<Tank> enemyTanks;
	private boolean makeBullet;
	private boolean lastBulletInputState;

	java.nio.FloatBuffer mouseWorldPos;
	float moveLevelX;
	float moveLevelY;
	float moveLevelZ;
	float levelWidth;
	float levelHeight;
	
	long lastFrameRenderTime;
	static float dt;
	
	ArrayList<Vector3f> levelBlocks;


	//TODO: Feel free to change the window title and default animation scale here
	public static void main(String args[])
	{   
		new CS2150Coursework().run(WINDOWED, "CS2150 Coursework Submission - Tanks", 0.01f);
	}

	protected void initScene() throws Exception
	{
		//TODO: Initialise your resources here - might well call other methods you write.
		//Set animation field values
		playerTank = new Tank(0.0f, 0.0f, -2.0f);
		enemyTanks = new ArrayList<Tank>();
		makeBullet = false;
		lastBulletInputState = false;

		mouseWorldPos = BufferUtils.createFloatBuffer(3);
		mouseWorldPos.put(0, 0.0f);
		mouseWorldPos.put(1, 0.0f);
		mouseWorldPos.put(2, 0.0f);

		moveLevelX = 0.0f;
		moveLevelY = -7.8f;
		moveLevelZ = -7.0f;
		
		lastFrameRenderTime = System.nanoTime();
		dt = 0.0f;
		
		enemyTanks.add(new Tank(-2.0f, 0.0f, -2.0f));

		//Load the textures
		//Paths change depending on whether you run in eclipse or command line
		try{
			testTexture = loadTexture("coursework/woollena/textures/test.bmp");
			tankMainTexture = loadTexture("coursework/woollena/textures/tankTextureMain.bmp");
			woodFloorTexture = loadTexture("coursework/woollena/textures/woodFloor.bmp");
			transparencyBlackTexture = loadTexture("coursework/woollena/textures/transparencyBlack.bmp");
			tankTurretTexture = loadTexture("coursework/woollena/textures/tankTurret.bmp");
		} catch(Exception e){
			testTexture = loadTexture("textures/test.bmp");
			tankMainTexture = loadTexture("textures/tankTextureMain.bmp");
			woodFloorTexture = loadTexture("textures/woodFloor.bmp");
			transparencyBlackTexture = loadTexture("textures/transparencyBlack.bmp");
			tankTurretTexture = loadTexture("textures/tankTurret.bmp");
		}


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




		/*
		 * To disable the windows cursor, I used the following answer
		 * http://forum.lwjgl.org/index.php?topic=594.msg23476#msg23476
		 * I did read official documentation however, it did not work
		 * I tried this 'hack' and it did, so I used it.
		 */

		try {
			org.lwjgl.input.Cursor emptyCursor = new org.lwjgl.input.Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
			Mouse.setNativeCursor(emptyCursor);
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*
		 * Build the level blocks
		 */
		LevelReader lvlReader = new LevelReader(1);
		levelBlocks = new ArrayList<Vector3f>();
		
		String lvlLine = lvlReader.getNextLine();
		
		String[] lvlDimensions = lvlLine.split("x");
		levelWidth = Integer.parseInt(lvlDimensions[0]);
		levelHeight = Integer.parseInt(lvlDimensions[1]);
		Vector3f startCornerPos = new Vector3f(0.5f - (levelWidth / 2), 0.5f, 0.5f - (levelHeight / 2));
		
		int lineCount = 0;
		lvlLine = lvlReader.getNextLine();
		while(lvlLine != null){
			//For each symbol check if it's a block or tank
			//If so, handle it appropriately
			char[] items = lvlLine.toCharArray();
			for(int i = 0; i < items.length; i++){
				switch(items[i]){
				case '@'://Build a wall
					levelBlocks.add(new Vector3f(startCornerPos.getX() + i, startCornerPos.getY(), startCornerPos.getZ() + lineCount));
					break;
				default:
					//Do nothing, we will assume empty space
					break;
				}
			}
			
			lvlLine = lvlReader.getNextLine();
			lineCount++;
		}
	}

	protected void checkSceneInput()
	{
		//TODO: Check for keyboard and mouse input here

		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			playerTank.move(-0.5f * dt, 0);
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			playerTank.move(0.5f * dt, 0);
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			playerTank.move(0, -0.5f * dt);
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			playerTank.move(0, 0.5f * dt);
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
		//        (obtained via a call to getAnimationScale() in your modifications so that your animations
		//        can be made faster or slower depending on the machine you are working on

		//Set the animation scale to the time to render the last frame in seconds
		//This essentially makes it 'x per second' instead of 'x per frame'
		dt = (System.nanoTime() - lastFrameRenderTime) / 100000000.0f;
		lastFrameRenderTime = System.nanoTime();
	}

	protected void renderScene()
	{
		//TODO: Render your scene here - remember that a scene graph will help you write this method! 
		//      It will probably call a number of other methods you will write.

		
		//Print frames per second to console
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
			GL11.glPushMatrix();
			{
				GL11.glTranslatef(0.0f, -0.5f, -0.0f);
				GL11.glScalef(levelWidth, 1f, levelHeight);

				// enable texturing and bind an appropriate texture
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, woodFloorTexture.getTextureID());

				drawUnitCube();//Ground

				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			GL11.glPopMatrix();

			
			
			/*
			 * Draw all blocks in world
			 */
			// enable texturing and bind an appropriate texture
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, woodFloorTexture.getTextureID());
			
			for(Vector3f block : levelBlocks){
				GL11.glPushMatrix();
				{
					GL11.glTranslatef(block.getX(), block.getY(), block.getZ());
					drawUnitCube();
				}
				GL11.glPopMatrix();
			}
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			

			/**
			 * Render tank here!
			 * Moved to own class to tidy up code, and then I can reuse it to also make enemy tanks
			 */
			playerTank.draw(tankMainTexture, tankTurretTexture, tankTurretTexture);
			
			for(Tank tank : enemyTanks){
				tank.draw(tankMainTexture, tankTurretTexture, tankTurretTexture);
				if(String.valueOf(System.currentTimeMillis()).contains("001")){
					tank.fireBullet();
				}
			}
			
			if(makeBullet){
				playerTank.fireBullet();
				makeBullet = false;
			}


			//Bullet code was here



		}
		GL11.glPopMatrix();

		/*
		 * Create shape above floor so bullets are correctly aligned with mouse
		 * 
		 * Here I create an invisible plane at the same height as the bullets.
		 * This makes it so that the bullets and mouse are properly aligned and
		 * aiming is accurate
		 * Without this, the mouse is below the bullet and at most angles the bullet
		 * will not accurately pass through the mouse
		 */
		GL11.glPushMatrix();
		{
			GL11.glTranslatef(0.0f, 0.5f, 0.0f);
			GL11.glScalef(15, 1, 15);
			// enable texturing and bind an appropriate texture
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, transparencyBlackTexture.getTextureID());
			drawInvisibleUnitPlane();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();


		/*******************
		 * Mouse tracking! *
		 *******************/

		/*
		 * I used the following answer in order to do the mouse tracking in 3D space
		 * The answer has a great explanation of what all of the components are
		 * http://gamedev.stackexchange.com/a/71489
		 */

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


		/*
		 * I used the following answer to work out the mathematics for turret rotation
		 * http://stackoverflow.com/a/23408996
		 * I modified it to make the tank the origin
		 * atan2(a.y - o.y, a.x - o.x) - atan2(b.y - o.y, b.x - o.x)
		 * The first atan2 has 1, 0 because position a is just to the right of the tank (+1) and
		 * the origin is the tank, making the result just 1 (tank + 1 - tank)
		 * The same works for 0, too
		 */
		Vector3f tankPos = playerTank.getPosition();
		playerTank.setTurretRotation((float) Math.toDegrees(Math.atan2(1, 0) - Math.atan2(mouseWorldPos.get(2) - tankPos.z, mouseWorldPos.get(0) - tankPos.x)) - 90);


		/*
		 * I used the following answer for making the 2D cursor but, modified it to
		 * remove unneeded bits and draw my own cursor
		 * http://stackoverflow.com/a/5468894
		 * It works by changing the projection to parallel, drawing a 2D object, and
		 * then switching back to 3D so we can render the game as usual
		 */
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		{
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0, 800, 600, 0.0, -1.0, 10.0);//Changes to parallel projection
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			{
				GL11.glLoadIdentity();
				GL11.glDisable(GL11.GL_CULL_FACE);

				float fixedMouseY = 600 - Mouse.getY();


				GL11.glBegin(GL11.GL_QUADS);
				{
					//Neither of these appear to work
					//Colour.RED.submit();
					//GL11.glColor3f(1.0f, 0.0f, 0.0f);


					//Coordinates from top-left
					GL11.glVertex2f(Mouse.getX() - 2, fixedMouseY - 30);
					GL11.glVertex2f(Mouse.getX() + 2, fixedMouseY - 30);
					GL11.glVertex2f(Mouse.getX() + 2, fixedMouseY - 10);
					GL11.glVertex2f(Mouse.getX() - 2, fixedMouseY - 10);

					GL11.glVertex2f(Mouse.getX() + 10, fixedMouseY - 2);
					GL11.glVertex2f(Mouse.getX() + 30, fixedMouseY - 2);
					GL11.glVertex2f(Mouse.getX() + 30, fixedMouseY + 2);
					GL11.glVertex2f(Mouse.getX() + 10, fixedMouseY + 2);
					
					GL11.glVertex2f(Mouse.getX() - 2, fixedMouseY + 10);
					GL11.glVertex2f(Mouse.getX() + 2, fixedMouseY + 10);
					GL11.glVertex2f(Mouse.getX() + 2, fixedMouseY + 30);
					GL11.glVertex2f(Mouse.getX() - 2, fixedMouseY + 30);
					
					GL11.glVertex2f(Mouse.getX() - 30, fixedMouseY - 2);
					GL11.glVertex2f(Mouse.getX() - 10, fixedMouseY - 2);
					GL11.glVertex2f(Mouse.getX() - 10, fixedMouseY + 2);
					GL11.glVertex2f(Mouse.getX() - 30, fixedMouseY + 2);
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
	
	public static float getDT(){
		return dt;
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

			GL11.glTexCoord2f(0.35f, 0.65f);
			v1.submit();
			
			GL11.glTexCoord2f(0.0f, 1.0f);
			v4.submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v3.submit();
			
			GL11.glTexCoord2f(0.35f, 0.35f);
			v2.submit();
		}
		GL11.glEnd();

		//Draw face far
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v6.toVector(), v7.toVector()).submit();

			GL11.glTexCoord2f(0.65f, 0.65f);
			v5.submit();
			
			GL11.glTexCoord2f(0.65f, 0.35f);
			v6.submit();
			
			GL11.glTexCoord2f(1.0f, 0.0f);
			v7.submit();
			
			GL11.glTexCoord2f(1.0f, 1.0f);
			v8.submit();
		}
		GL11.glEnd();

		//Draw face left
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v5.toVector(), v8.toVector()).submit();

			GL11.glTexCoord2f(0.35f, 0.65f);
			v1.submit();
			
			GL11.glTexCoord2f(0.65f, 0.65f);
			v5.submit();
			
			GL11.glTexCoord2f(1.0f, 1.0f);
			v8.submit();
			
			GL11.glTexCoord2f(0.0f, 1.0f);
			v4.submit();
		}
		GL11.glEnd();

		//Draw face right
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v2.toVector(), v3.toVector(), v7.toVector()).submit();

			GL11.glTexCoord2f(0.35f, 0.35f);
			v2.submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v3.submit();
			
			GL11.glTexCoord2f(1.0f, 0.0f);
			v7.submit();
			
			GL11.glTexCoord2f(0.65f, 0.35f);
			v6.submit();
		}
		GL11.glEnd();

		//Draw face top
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v2.toVector(), v6.toVector()).submit();

			GL11.glTexCoord2f(0.35f, 0.65f);
			v1.submit();
			
			GL11.glTexCoord2f(0.35f, 0.35f);
			v2.submit();
			
			GL11.glTexCoord2f(0.65f, 0.35f);
			v6.submit();
			
			GL11.glTexCoord2f(0.65f, 0.65f);
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
	protected static void drawCircle(float radius, int slices){
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