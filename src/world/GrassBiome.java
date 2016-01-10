
package world;

public class GrassBiome extends Biome {
	
	private static GrassBiome instance;
	
	protected static GrassBiome getInstance() {
		if ( instance == null ) {
			instance = new GrassBiome();
		}
		return instance;
	}
	
	protected String getName() {
		return "Grass";
	};
	
	protected static final float THRESHOLD = .46f;
	
	protected int getHeight( float noise ) {
		return 1+(int)(Math.random()*2);
	};
	
	protected int getRed() {
		return (int)(Math.random()*64);
	};
	protected int getGreen() {
		return 127+(int)(Math.random()*64);
	};
	protected int getBlue() {
		return (int)(Math.random()*128);
	};
	
}
