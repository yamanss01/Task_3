import java.util.List;

import processing.core.PImage;

public class Sapling extends SaplingAbstraction {
	
	@Override
	public Entity createSapling(String id, Point position, List<PImage> images) {
		return new Entity(EntityKind.SAPLING, id, position, images, 0, 0, Functions.SAPLING_ACTION_ANIMATION_PERIOD,
				Functions.SAPLING_ACTION_ANIMATION_PERIOD, 0, Functions.SAPLING_HEALTH_LIMIT);
	}
	
	@Override
	public boolean transformSapling(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		ActionAbstraction action = new Action();
		if (entity.health <= 0) {
			Entity stump = Functions.createStump(entity.id, entity.position,
					ImageStoreAbstract.getImageList(imageStore, Functions.STUMP_KEY));

			
			action.performActions(entity, world, scheduler, imageStore, stump);

			return true;
		} else if (entity.health >= entity.healthLimit) {
			Entity tree = Functions.createTree("tree_" + entity.id, entity.position,
					Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN),
					Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN),
					Functions.getNumFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN),
					ImageStoreAbstract.getImageList(imageStore, Functions.TREE_KEY));

			action.performActions(entity, world, scheduler, imageStore, tree);
			return true;
		}

		return false;
	}
	
	@Override
	public void executeSaplingActivity(Entity entity, WorldModel world, ImageStore imageStore,
			EventScheduler scheduler) {
		TreeAbstraction tree = new Tree();
		entity.health++;
		if (!tree.transformPlant(entity, world, scheduler, imageStore)) {
			EventScheduler.scheduleEvent(scheduler, entity, Action.createActivityAction(entity, world, imageStore),
					entity.actionPeriod);
		}
	}

}
