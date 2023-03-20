import java.util.List;
import java.util.Map;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class ImageStoreAbstract {

	abstract void loadImages(Scanner in, ImageStore imageStore, PApplet screen);

	abstract void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen);

	abstract List<PImage> getImages(Map<String, List<PImage>> images, String key);

	abstract void setAlpha(PImage img, int maskColor, int alpha);
	
	static List<PImage> getImageList(ImageStore imageStore, String key) {
		return imageStore.images.getOrDefault(key, imageStore.defaultImages);
	}

	static PImage getCurrentImage(Object entity) {
		if (entity instanceof Background) {
			return ((Background) entity).images.get(((Background) entity).imageIndex);
		} else if (entity instanceof Entity) {
			return ((Entity) entity).images.get(((Entity) entity).imageIndex);
		} else {
			throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", entity));
		}
	}
}