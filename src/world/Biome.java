
package world;

public abstract class Biome {
	
	protected abstract String getName();
	
	protected abstract int getHeight( float noise );
	
	protected abstract int getRed();
	protected abstract int getGreen();
	protected abstract int getBlue();
	
}
