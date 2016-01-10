
package world;

import processing.core.*;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class World extends PApplet {
	
	public static void main( String args[] ) {
		PApplet.main( new String[] { "--present", "world.World" } );
	}
	
	public void settings() {
		fullScreen( P3D );
	}
	
	private PFont fontSmall;
	private PFont fontNormal;
	
	protected Character player;
	
	protected HashMap<String,Chunk> chunks = new HashMap<String,Chunk>();
	protected ArrayList<Chunk> currentChunks = new ArrayList<Chunk>();
	protected Chunk selectedChunk;
	
	private int minLatitude = 0;
	private int maxLatitude = 0;
	private int minLongitude = 0;
	private int maxLongitude = 0;
	
	protected static final float NOISE_OFFSET = 10000000;
	
	protected static final int VISIBILITY_RADIUS = 15;
	protected static final int FADE_AWAY = 3;
	protected static final float GRAVITY = 5;
	
	private static final float BG_RED = 31;
	private static final float BG_GREEN = 95;
	private static final float BG_BLUE = 255;
	
	public void setup() {
		frameRate( 60 );
		noCursor();
		
		fontSmall = createFont( "Consolas", 20, true );
		fontNormal = createFont( "Consolas", 40, true );
		
		player = new Character( this );
		currentChunks = getCurrentChunks();
	}
	
	protected static final float CLOUD_MOVEMENT_RATE = .004f*Chunk.CHUNK_SIZE;
	
	protected float cloudPosition = 0;
	protected int cloudOffset = 0;
	
	public void draw() {
		if ( paused ) {
			drawPauseScreen();
			pausedFrames++;
		} else {
			drawGame();
		}
	}
	
	public void drawGame() {
		background( BG_RED, BG_GREEN, BG_BLUE );
		lights();
		
		camera( width/2, height/2, height/2, width/2, height/2, 0, 0, 1, 0 );
		
		player.handleXYMotion();
		player.handleZMotion();
		player.handleXRotation();
		player.handleZRotation();
		
		pushMatrix();
			translate( width/2, height, 0 );
			rotateX( HALF_PI );
			rotateZ( HALF_PI );
			rotateY( -player.getYHeadRotation() );
			
			rotateZ( -player.getZHeadRotation()-player.getZBodyRotation() );
			translate( -player.getXFeetPosition(), -player.getYFeetPosition(), -(player.getZFeetPosition()+player.getZEyePosition()) );
			
			cloudPosition = (cloudPosition+CLOUD_MOVEMENT_RATE)%Chunk.CHUNK_SIZE;
			if ( cloudPosition < CLOUD_MOVEMENT_RATE ) {
				cloudOffset++;
			}
			
			if ( player.hasMoved() ) {
				currentChunks = getCurrentChunks();
			}
			
			selectedChunk = selectChunk();
			
			for ( Chunk chunk: currentChunks ) {
				chunk.draw();
			}
		popMatrix();
		
		drawCursor();
		
		writeGameInfo();
	}
	
	protected boolean drawGrid = true;
	
	private void drawCursor() {
		pushMatrix();
			translate( width/2, height/2, 0 );
			stroke( 255 );
			strokeWeight( 2 );
			line( 10, 0, 0, 3, 0, 0 );
			line( -10, 0, 0, -3, 0, 0 );
			line( 0, 10, 0, 0, 3, 0 );
			line( 0, -10, 0, 0, -3, 0 );
		popMatrix();
	}
	
	private void writeGameInfo() {
		fill( 255 );
		
		textFont( fontNormal );
		textAlign( LEFT );
		text( round( frameRate ) + " FPS", 30, 60, -400 );
		
		textFont( fontSmall );
		textAlign( LEFT );
		text( "press 'P' to pause", 30, 100, -400 );
		
		textFont( fontSmall );
		textAlign( RIGHT );
		text( "X: " + player.getLatitude() + "\nY: " + player.getLongitude() + "\nZ: " +player.getCurrentChunk().getHeight() + "\n" + player.getCurrentChunk().getBiome().getName(), width-30, 40, -400 );
	}
	
	public void drawPauseScreen() {
		background( 0 );
		
		camera();
		
		PImage image = createMapImage();
		image( image, minLongitude-player.getLongitude()+width/2, -(maxLatitude-player.getLatitude())+height/2 );
		drawArrow();
		
		writeControls();
		
		drawCompass();
	}
	
	private void drawArrow() {
		pushMatrix();
			translate( width/2, height/2, 0 );
			rotateZ( player.getZBodyRotation()+player.getZHeadRotation() );
			stroke( 255 );
			strokeWeight( 2 );
			line( 0, 7, -7, -7 );
			line( 0, 7, 7, -7 );
		popMatrix();
	}
	
	private void drawCompass() {
		pushMatrix();
			translate( width-120, 120, 0 );
			
			stroke( 255 );
			strokeWeight( 2 );
			noFill();
			ellipse( 0, 0, 100, 100 );
			line( 0, 65, 0, -65 );
			line( 65, 0, -65, 0 );
			line( -5, -55, 0, -65 );
			line( 5, -55, 0, -65 );
			
			textFont( fontSmall );
			textAlign( CENTER );
			fill( 255 );
			text( "N", 0, -70, 0);
			text( "S", 0, 80, 0);
			text( "W", -75, 5, 0);
			text( "E", 75, 5, 0);
		popMatrix();
	}
	
	private void writeControls() {
		textFont( fontSmall );
		textAlign( LEFT );
		fill( 255 );
		text( "    [MOUSE-Y] - rotate x-axis\n"
				+ "    [MOUSE-X] - rotate y-axis\n"
				+ "     [A], [D] - move left/right\n"
				+ "     [W], [S] - move forward/backward\n"
				+ "      [SPACE] - jump\n\n"
				+ " [MOUSE LEFT] - increase block height\n"
				+ "[MOUSE RIGHT] - decrease block height\n"
				+ "        [ALT] - toggle grid\n\n"
				+ "          [I] - take map picture\n"
				+ "          [O] - take screenshot\n\n"
				+ "          [P] - pause/unpause game\n"
				+ "     [ESCAPE] - quit",
				30, 50, 0
			);
	}
	
	protected int pausedFrames = 0;
	
	public void keyPressed( KeyEvent event ) {
		if ( event.getKey() == 'p' ) {
			paused = !paused;
		} else if ( !paused ) {
			if ( event.getKey() == CODED ) {
				switch ( event.getKeyCode() ) {
					case ALT:
						drawGrid = !drawGrid;
						break;
				}
			} else {
				switch ( event.getKey() ) {
					case ' ':
						player.jump();
						break;
					case 'i':
						saveMapImage();
						break;
					case 'o':
						saveScreenshot();
						break;
				}
			}
		}
	}
	
	public void mouseClicked() {
		if ( selectedChunk == null ) {
			return;
		}
		switch ( mouseButton ) {
			case LEFT:
				selectedChunk.increaseHeight();
				break;
			case RIGHT:
				selectedChunk.decreaseHeight();
				break;
		}
	}
	
	protected boolean paused = false;
	
	private ArrayList<Chunk> getCurrentChunks() {
		ArrayList<Chunk> newCurrentChunks = new ArrayList<Chunk>();
		
		int playerLatitude = player.getLatitude();
		int playerLongitude = player.getLongitude();
		
		for ( int longitude = playerLongitude-VISIBILITY_RADIUS; longitude <= playerLongitude+VISIBILITY_RADIUS; longitude++ ) {
			for ( int latitude = playerLatitude-VISIBILITY_RADIUS; latitude <= playerLatitude+VISIBILITY_RADIUS; latitude++ ) {
				if ( dist(playerLatitude,playerLongitude,latitude,longitude) > VISIBILITY_RADIUS ) {
					continue;
				}
				Chunk chunk = chunks.get( Chunk.id( latitude, longitude ) );
				if ( chunk == null ) {
					chunk = new Chunk( this, latitude, longitude );
					chunks.put( chunk.getId(), chunk );
				}
				newCurrentChunks.add( chunk );
				minLatitude = min( minLatitude, latitude );
				maxLatitude = max( maxLatitude, latitude );
				minLongitude = min( minLongitude, longitude );
				maxLongitude = max( maxLongitude, longitude );
			}
		}
		
		return newCurrentChunks;
	}
	
	protected static final int SELECTION_RADIUS = 4;
	
	private Chunk selectChunk() {
		int playerLatitude = player.getLatitude();
		int playerLongitude = player.getLongitude();
		
		ArrayList<Chunk> candidates = new ArrayList<Chunk>();
		candidates.addAll( currentChunks );
		ArrayList<Chunk> removeList = new ArrayList<Chunk>();
		
		for ( Chunk chunk: candidates ) {
			if ( dist(playerLatitude,playerLongitude,chunk.getLatitude(),chunk.getLongitude()) > SELECTION_RADIUS ) {
				removeList.add( chunk );
			}
		}
		candidates.removeAll( removeList );
		removeList.clear();
		
		float precision = Chunk.CHUNK_SIZE/4;
		
		while ( precision >= 1 ) {
			removeList.addAll( candidates );
			for ( Chunk chunk: candidates ) {
				pushMatrix();
					translate( chunk.getLatitude()*Chunk.CHUNK_SIZE, chunk.getLongitude()*Chunk.CHUNK_SIZE, chunk.getTotalHeight() );
					for ( int x = (int)(-Chunk.CHUNK_SIZE/2); x <= (Chunk.CHUNK_SIZE/2); x+=precision ) {
						for ( int y = (int)(-Chunk.CHUNK_SIZE/2); y <= (Chunk.CHUNK_SIZE/2); y+=precision ) {
							if ( abs(screenX( x, y, 0 )-width/2) < precision && abs(screenY( x, y, 0 )-height/2) < precision ) {
								removeList.remove( chunk );
							}
						}
					}
				popMatrix();
			}
			candidates.removeAll( removeList );
			removeList.clear();
			
			if ( candidates.size() <= 1 ) {
				break;
			} else {
				precision/=4;
			}
		}
		
		if ( candidates.size() >= 1 ) {
			int playerX = player.getLatitude();
			int playerY = player.getLongitude();
			float playerZ = player.getZFeetPosition()+player.getZEyePosition();
			
			HashMap<Chunk,Float> distances = new HashMap<Chunk,Float>();
			for ( Chunk chunk: candidates ) {
				distances.put( chunk, dist(chunk.getLatitude(),chunk.getLongitude(),chunk.getHeight(),playerX,playerY,playerZ) );
			}
			
			float minDistance = Float.MAX_VALUE;
			Chunk closest = null;
			for ( Chunk chunk: distances.keySet() ) {
				if ( distances.get( chunk ) < minDistance ) {
					minDistance = distances.get( chunk );
					closest = chunk;
				}
			}
			
			return closest;
		} else if ( candidates.size() == 1 ) {
			for ( Chunk chunk: candidates ) {
				return chunk;
			}
		}
		
		return null;
	}
	
	private PImage createMapImage() {
		int imageWidth = maxLongitude-minLongitude+1;
		int imageHeight = maxLatitude-minLatitude+1;
		PImage image = createImage( imageWidth, imageHeight, RGB );
		image.loadPixels();
		
		for ( int y = 0; y < imageWidth; y++ ) {
			for ( int x = 0; x < imageHeight; x++ ) {
				int latitude = maxLatitude-x;
				int longitude = minLongitude+y;
				Chunk chunk = chunks.get( Chunk.id( latitude, longitude ) );
				if ( chunk == null ) {
					continue;
				}
				image.pixels[x*imageWidth+y] = color( chunk.getRed(), chunk.getGreen(), chunk.getBlue() );
			}
		}
		
		image.updatePixels();
		return image;
	}
	
	private void saveMapImage() {
		long time = java.lang.System.currentTimeMillis();
		PImage image = createMapImage();
		image.save( "screenshots/Unbounded-" + time + ".png" );
	}
	
	private void saveScreenshot() {
		long time = java.lang.System.currentTimeMillis();
		saveFrame( "screenshots/Unbounded-" + time + ".png" );
	}
	
}
