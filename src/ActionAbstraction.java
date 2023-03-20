import java.util.List;

import processing.core.PImage;

public abstract class ActionAbstraction {

	abstract void executeAction(Action action, EventScheduler scheduler);

	abstract void executeAnimationAction(Action action, EventScheduler scheduler);

	abstract void executeActivityAction(Action action, EventScheduler scheduler);

	abstract void executeFairyActivity(Entity entity, WorldModel world, ImageStore imageStore,
			EventScheduler scheduler);

	abstract void executeDudeNotFullActivity(Entity entity, WorldModel world, ImageStore imageStore,
			EventScheduler scheduler);

	abstract void executeDudeFullActivity(Entity entity, WorldModel world, ImageStore imageStore,
			EventScheduler scheduler);

	abstract void updateOnTime(EventScheduler scheduler, long time);

	abstract boolean moveToFairy(Entity fairy, WorldModel world, Entity target, EventScheduler scheduler);

	abstract Point nextPositionFairy(Entity entity, WorldModel world, Point destPos);

	abstract boolean moveToNotFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler);

	abstract boolean moveToFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler);

	abstract boolean transformNotFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore);

	abstract void transformFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore);

	abstract  void performActions(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore,
			Entity miner);
	
	protected abstract void nextImage(Entity entity);
	
	static Action createAnimationAction(Entity entity, int repeatCount) {
		return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
	}

	static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
		return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
	}

	static Entity createDudeNotFull(String id, Point position, int actionPeriod, int animationPeriod, int resourceLimit,
			List<PImage> images) {
		return new Entity(EntityKind.DUDE_NOT_FULL, id, position, images, resourceLimit, 0, actionPeriod,
				animationPeriod, 0, 0);
	}
}
