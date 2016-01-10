
package world;

public class MountainBiome extends Biome {
	
	private static MountainBiome instance;
	
	protected static MountainBiome getInstance() {
		if ( instance == null ) {
			instance = new MountainBiome();
		}
		return instance;
	}
	
	protected String getName() {
		return "Mountain";
	};
	
	protected static final float THRESHOLD = .69f;
	
	protected int getHeight( float noise ) {
		return 5+World.round(45*World.sq((noise-THRESHOLD)/(1-THRESHOLD))-(float)Math.random());
	};
	
	protected int getRed() {
		return 91+(int)(Math.random()*32);
	};
	protected int getGreen() {
		return 127+(int)(Math.random()*32);
	};
	protected int getBlue() {
		return 159+(int)(Math.random()*64);
	};
	
}
