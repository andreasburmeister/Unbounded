
package world;

public class Chunk {
	
	private World world;

	protected Chunk( World world, int latitude, int longitude ) {
		this.world = world;
		this.latitude = latitude;
		this.longitude = longitude;
		
		float noise = world.noise( (float)(latitude+World.NOISE_OFFSET+world.random( -BIOME_FUZZYNESS/2, BIOME_FUZZYNESS/2 ))/BIOME_SCALE, (float)(longitude+World.NOISE_OFFSET+world.random( -BIOME_FUZZYNESS/2, BIOME_FUZZYNESS/2 ))/BIOME_SCALE );
		biome = chooseBiome( noise );
		height = biome.getHeight( noise );
		red = biome.getRed();
		green = biome.getGreen();
		blue = biome.getBlue();
	}
	
	protected static final float BIOME_SCALE = 25;
	protected static final float BIOME_FUZZYNESS = BIOME_SCALE/5;
	
	private Biome chooseBiome( float noise ) {
		if ( noise > MountainBiome.THRESHOLD ) {
			return MountainBiome.getInstance();
		} else if ( noise > HillBiome.THRESHOLD ) {
			return HillBiome.getInstance();
		} else if ( noise > GrassBiome.THRESHOLD ) {
			return GrassBiome.getInstance();
		} else if ( noise > SandBiome.THRESHOLD ) {
			return SandBiome.getInstance();
		} else {
			return WaterBiome.getInstance();
		}
	}
	
	private int latitude;
	private int longitude;
	
	protected int getLatitude() {
		return latitude;
	}
	protected int getLongitude() {
		return longitude;
	}
	
	private final Biome biome;
	
	protected Biome getBiome() {
		return biome;
	};
	
	protected static final int MAX_HEIGHT = 50;
	
	private int height;
	
	protected int getHeight() {
		return height;
	}
	
	protected void increaseHeight() {
		height = World.min(MAX_HEIGHT,++height);
	}
	
	protected void decreaseHeight() {
		height = World.max(0,--height);
	}
	
	protected float getTotalHeight() {
		return height*Z_FACTOR;
	}
	
	private int red;
	private int green;
	private int blue;
	
	protected int getRed() {
		return red;
	}
	protected int getGreen() {
		return green;
	}
	protected int getBlue() {
		return blue;
	}
	
	protected static String id( int latitude, int longitude ) {
		return latitude + "@" + longitude;
	}
	
	protected String getId() {
		return id( latitude, longitude );
	}
	
	protected static final float CHUNK_SIZE = 500;
	protected static final float Z_FACTOR = CHUNK_SIZE/4;
	
	protected void draw() {
		float distance = World.sqrt(World.sq(world.player.getLatitude()-latitude)+World.sq(world.player.getLongitude()-longitude));
		int opacity = World.min( 255, (int)((255+(World.VISIBILITY_RADIUS-distance-World.FADE_AWAY)*(255f/(World.FADE_AWAY+1)))) ) ;
		
		world.pushMatrix();
			world.translate( latitude*CHUNK_SIZE, longitude*CHUNK_SIZE, 0 );
			world.stroke( 0 );
			if ( world.drawGrid ) {
				world.strokeWeight( 2 );
			} else {
				world.strokeWeight( 0 );
			}
			drawCloud( opacity );
			if ( world.selectedChunk == this ) {
				world.stroke( 255 );
				world.strokeWeight( 5 );
			}
			drawGround( opacity );
		world.popMatrix();
	}
	
	protected static final float CLOUD_SCALE = 5;
	protected static final float CLOUD_CHANGE_RATE = .0001f;
	protected static final float CLOUD_THRESHOLD = .6f;
	protected static final int CLOUD_HEIGHT = 35;
	protected static final int CLOUD_THICKNESS = 2;
	
	private void drawCloud( int opacity ) {
		if ( world.noise( (latitude+World.NOISE_OFFSET)/CLOUD_SCALE, (longitude-world.cloudOffset+World.NOISE_OFFSET)/CLOUD_SCALE, (world.frameCount-world.pausedFrames)*CLOUD_CHANGE_RATE ) > CLOUD_THRESHOLD ) {
			world.pushMatrix();
				world.translate( 0,  world.cloudPosition, (CLOUD_HEIGHT+CLOUD_THICKNESS/2f)*Z_FACTOR);
				world.fill( 191, 191, 255, opacity );
				world.box( CHUNK_SIZE, CHUNK_SIZE, CLOUD_THICKNESS*Z_FACTOR );
			world.popMatrix();
		}
	}
	
	private void drawGround( int opacity ) {
		world.pushMatrix();
			world.translate( 0,  0, height*Z_FACTOR/2-.5f );
			WaterBiome water = WaterBiome.getInstance();
			if ( biome == water ) {
				red = water.getRed( world, this );
				green = water.getGreen( world, this );
				blue = water.getBlue( world, this );
			}
			world.fill( red, green, blue, opacity );
			world.box( CHUNK_SIZE, CHUNK_SIZE, height*Z_FACTOR+1 );
		world.popMatrix();
	}
	
}
