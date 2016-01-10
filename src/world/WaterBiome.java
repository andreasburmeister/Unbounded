
package world;

public class WaterBiome extends Biome {
	
	private static WaterBiome instance;
	
	protected static WaterBiome getInstance() {
		if ( instance == null ) {
			instance = new WaterBiome();
		}
		return instance;
	}
	
	protected String getName() {
		return "Water";
	};
	
	protected int getHeight( float noise ) {
		return 0;
	};
	
	protected int getRed() {
		return 0;
	};
	protected int getGreen() {
		return 0;
	};
	protected int getBlue() {
		return 0;
	};
	
	protected static final float WATER_SCALE = 2.5f;
	protected static final float WATER_CHANGE_RATE = .007f;
	
	protected int getRed( World world, Chunk chunk ) {
		return (int)(world.noise( (chunk.getLatitude()+World.NOISE_OFFSET)/WATER_SCALE, (chunk.getLongitude()+World.NOISE_OFFSET)/WATER_SCALE, world.frameCount*WATER_CHANGE_RATE )*32);
	};
	protected int getGreen( World world, Chunk chunk ) {
		return (int)(world.noise( (chunk.getLatitude()+World.NOISE_OFFSET)/WATER_SCALE, (chunk.getLongitude()+World.NOISE_OFFSET)/WATER_SCALE, world.frameCount*WATER_CHANGE_RATE )*64);
	};
	protected int getBlue( World world, Chunk chunk ) {
		return 95+(int)(world.noise( (chunk.getLatitude()+World.NOISE_OFFSET)/WATER_SCALE, (chunk.getLongitude()+World.NOISE_OFFSET)/WATER_SCALE, world.frameCount*WATER_CHANGE_RATE )*160);
	};
	
}
