import java.util.List;

import processing.core.PImage;

public abstract class SaplingAbstraction {

	abstract Entity createSapling(String id, Point position, List<PImage> images);

	abstract boolean transformSapling(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore);

	abstract void executeSaplingActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}