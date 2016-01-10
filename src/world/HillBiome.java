
package world;

public class HillBiome extends Biome {
	
	private static HillBiome instance;
	
	protected static HillBiome getInstance() {
		if ( instance == null ) {
			instance = new HillBiome();
		}
		return instance;
	}
	
	protected String getName() {
		return "Hill";
	};
	
	protected static final float THRESHOLD = .60f;
	
	protected int getHeight( float noise ) {
		return 3+(int)(Math.random()*2);
	};
	
	protected int getRed() {
		return 127+(int)(Math.random()*64);
	};
	protected int getGreen() {
		return 63+(int)(Math.random()*64);
	};
	protected int getBlue() {
		return (int)(Math.random()*64);
	};
	
}
