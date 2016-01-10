
package world;

public class SandBiome extends Biome {
	
	private static SandBiome instance;
	
	protected static SandBiome getInstance() {
		if ( instance == null ) {
			instance = new SandBiome();
		}
		return instance;
	}
	
	protected String getName() {
		return "Sand";
	};
	
	protected int getHeight( float noise ) {
		return 1;
	};
	
	protected static final float THRESHOLD = .44f;
	
	protected int getRed() {
		return 127+(int)(Math.random()*32);
	};
	protected int getGreen() {
		return 127+(int)(Math.random()*32);
	};
	protected int getBlue() {
		return 31+(int)(Math.random()*32);
	};
	
}
