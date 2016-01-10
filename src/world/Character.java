
package world;

import processing.core.PApplet;
import static processing.core.PConstants.*;

public class Character {
	
	private World world;
	
	protected Character( World world ) {
		this.world = world;
		feetXYMinPosition = (float)(Integer.MIN_VALUE+World.VISIBILITY_RADIUS)*Chunk.CHUNK_SIZE;
		feetXYMaxPosition = (float)(Integer.MAX_VALUE-World.VISIBILITY_RADIUS)*Chunk.CHUNK_SIZE;
	}
	
	private float xFeetPosition = 0;
	private float yFeetPosition = 0;
	private float zFeetPosition = 0;
	private final float zEyePosition = 200;
	private final float feetXYMinPosition;
	private final float feetXYMaxPosition;
	private final float xyMovementRate = 25;
	private final float zMovementRate = 75;
	private float zSpeed = 0;
	private boolean hasMoved = false;
	
	protected float getXFeetPosition() {
		return xFeetPosition;
	}
	protected float getYFeetPosition() {
		return yFeetPosition;
	}
	protected float getZFeetPosition() {
		return zFeetPosition;
	}
	protected float getZEyePosition() {
		return zEyePosition;
	}
	
	protected boolean hasMoved() {
		return hasMoved;
	}
	
	protected int getLatitude() {
		return PApplet.round(xFeetPosition/Chunk.CHUNK_SIZE);
	}
	protected int getLongitude() {
		return PApplet.round(yFeetPosition/Chunk.CHUNK_SIZE);
	}
	
	protected Chunk getCurrentChunk() {
		return world.chunks.get( Chunk.id( getLatitude(), getLongitude() ) );
	}
	
	protected void handleXYMotion() {
		if ( world.keyPressed ) {
			float angle = 0;
			switch ( world.key ) {
				case 'w':
					angle = PI;
					break;
				case 'a':
					angle = HALF_PI;
					break;
				case 's':
					angle = 0;
					break;
				case 'd':
					angle = -HALF_PI;
					break;
				default:
					return;
			}
			
			float direction = zBodyRotation+zHeadRotation+angle;
			hasMoved = false;
			
			float adjustedXYMovementRate = xyMovementRate * 60/world.frameRate;
			
			float newXFeetPosition = xFeetPosition+PApplet.cos(direction)*adjustedXYMovementRate;
			if ( newXFeetPosition >= feetXYMinPosition && newXFeetPosition <= feetXYMaxPosition ) {
				xFeetPosition = newXFeetPosition;
				hasMoved = true;
			}
			float newYFeetPosition = yFeetPosition+PApplet.sin(direction)*adjustedXYMovementRate;
			if ( newYFeetPosition >= feetXYMinPosition && newYFeetPosition <= feetXYMaxPosition ) {
				yFeetPosition = newYFeetPosition;
				hasMoved = true;
			}
			zFeetPosition = World.max( zFeetPosition, getCurrentChunk().getTotalHeight() );
		}
	}
	
	protected void handleZMotion() {
		zSpeed -= World.GRAVITY;
		float adjustedZSpeed = zSpeed * 60/world.frameRate;
		zFeetPosition = World.max( zFeetPosition+adjustedZSpeed, getCurrentChunk().getTotalHeight() );
	}
	
	protected void jump() {
		if ( zFeetPosition == getCurrentChunk().getTotalHeight() ) {
			zSpeed = zMovementRate;
		}
	}
	
	private float yHeadRotation = 0;
	private float zHeadRotation = 0;
	private float zBodyRotation = 0;
	private final float zBodyRotationRate = PI/100;
	
	protected float getYHeadRotation() {
		return yHeadRotation;
	}
	protected float getZHeadRotation() {
		return zHeadRotation;
	}
	protected float getZBodyRotation() {
		return zBodyRotation;
	}
	
	protected void handleXRotation() {
		yHeadRotation = -(float)world.mouseY/world.height*PI+HALF_PI;
	}
	
	protected void handleZRotation() {
		zHeadRotation = (float)world.mouseX/world.width*PI+HALF_PI;
		
		if ( world.mouseX == 0 ) {
			zBodyRotation -= zBodyRotationRate;
		} else if ( world.mouseX == world.width-1 ) {
			zBodyRotation += zBodyRotationRate;
		}
		zBodyRotation = zBodyRotation%TAU;
	}
	
}
