import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import processing.core.PImage;

/**
 * An action that can be taken by an entity
 */
public class Action extends ActionAbstraction {
	public ActionKind kind;
	public Entity entity;
	public WorldModel world;
	public ImageStore imageStore;
	public int repeatCount;

	public Action() { }

	public Action(ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
		this.kind = kind;
		this.entity = entity;
		this.world = world;
		this.imageStore = imageStore;
		this.repeatCount = repeatCount;
	}

	protected void executeAction(Action action, EventScheduler scheduler) {
		switch (action.kind) {
		case ACTIVITY:
			executeActivityAction(action, scheduler);
			break;

		case ANIMATION:
			executeAnimationAction(action, scheduler);
			break;
		}
	}

	protected void executeAnimationAction(Action action, EventScheduler scheduler) {
		nextImage(action.entity);

		if (action.repeatCount != 1) {
			EventScheduler.scheduleEvent(scheduler, action.entity,
					createAnimationAction(action.entity, Math.max(action.repeatCount - 1, 0)),
					EventScheduler.getAnimationPeriod(action.entity));
		}
	}

	protected void executeActivityAction(Action action, EventScheduler scheduler) {
		switch (action.entity.kind) {
		case SAPLING:
			SaplingAbstraction saplingInt = new Sapling();
			saplingInt.executeSaplingActivity(action.entity, action.world, action.imageStore, scheduler);
			break;

		case TREE:
			TreeAbstraction treeAbstraction = new Tree();
			treeAbstraction.executeTreeActivity(action.entity, action.world, action.imageStore, scheduler);
			break;

		case FAIRY:
			executeFairyActivity(action.entity, action.world, action.imageStore, scheduler);
			break;

		case DUDE_NOT_FULL:
			executeDudeNotFullActivity(action.entity, action.world, action.imageStore, scheduler);
			break;

		case DUDE_FULL:
			executeDudeFullActivity(action.entity, action.world, action.imageStore, scheduler);
			break;

		default:
			throw new UnsupportedOperationException(
					String.format("executeActivityAction not supported for %s", action.entity.kind));
		}
	}

	protected void executeFairyActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		Optional<Entity> fairyTarget = Functions.findNearest(world, entity.position,
				new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

		if (fairyTarget.isPresent()) {
			Point tgtPos = fairyTarget.get().position;

			if (moveToFairy(entity, world, fairyTarget.get(), scheduler)) {
				SaplingAbstraction saplingInt = new Sapling();

				Entity sapling = saplingInt.createSapling("sapling_" + entity.id, tgtPos,
						ImageStoreAbstract.getImageList(imageStore, Functions.SAPLING_KEY));

				Entity.addEntity(world, sapling);
				EventScheduler.scheduleActions(sapling, scheduler, world, imageStore);
			}
		}

		EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore),
				entity.actionPeriod);
	}

	protected void executeDudeNotFullActivity(Entity entity, WorldModel world, ImageStore imageStore,
			EventScheduler scheduler) {
		Optional<Entity> target = Functions.findNearest(world, entity.position,
				new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

		if (!target.isPresent() || !moveToNotFull(entity, world, target.get(), scheduler)
				|| !transformNotFull(entity, world, scheduler, imageStore)) {
			EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore),
					entity.actionPeriod);
		}
	}

	protected void executeDudeFullActivity(Entity entity, WorldModel world, ImageStore imageStore,
			EventScheduler scheduler) {
		Optional<Entity> fullTarget = Functions.findNearest(world, entity.position,
				new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

		if (fullTarget.isPresent() && moveToFull(entity, world, fullTarget.get(), scheduler)) {
			transformFull(entity, world, scheduler, imageStore);
		} else {
			EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore),
					entity.actionPeriod);
		}
	}

	public void updateOnTime(EventScheduler scheduler, long time) {
		while (!scheduler.eventQueue.isEmpty() && scheduler.eventQueue.peek().time < time) {
			Event next = scheduler.eventQueue.poll();

			EventScheduler eventScheduler = new EventScheduler();
			eventScheduler.removePendingEvent(scheduler, next);

			executeAction(next.action, scheduler);
		}
	}

	protected Point nextPositionFairy(Entity entity, WorldModel world, Point destPos) {
		int horiz = Integer.signum(destPos.x - entity.position.x);
		Point newPos = new Point(entity.position.x + horiz, entity.position.y);

		if (horiz == 0 || Functions.isOccupied(world, newPos)) {
			int vert = Integer.signum(destPos.y - entity.position.y);
			newPos = new Point(entity.position.x, entity.position.y + vert);

			if (vert == 0 || Functions.isOccupied(world, newPos)) {
				newPos = entity.position;
			}
		}

		return newPos;
	}
	
	protected boolean moveToFairy(Entity fairy, WorldModel world, Entity target, EventScheduler scheduler) {
		if (Functions.adjacent(fairy.position, target.position)) {
			Entity.removeEntity(world, target);
			EventScheduler.unscheduleAllEvents(scheduler, target);
			return true;
		} else {
			Point nextPos = nextPositionFairy(fairy, world, target.position);

			if (!fairy.position.equals(nextPos)) {
				Optional<Entity> occupant = Entity.getOccupant(world, nextPos);
				if (occupant.isPresent()) {
					EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
				}

				Entity.moveEntity(world, fairy, nextPos);
			}
			return false;
		}
	}

	protected boolean moveToNotFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler) {
		if (Functions.adjacent(dude.position, target.position)) {
			dude.resourceCount += 1;
			target.health--;
			return true;
		} else {
			Point nextPos = Point.nextPositionDude(dude, world, target.position);

			if (!dude.position.equals(nextPos)) {
				Optional<Entity> occupant = Entity.getOccupant(world, nextPos);
				if (occupant.isPresent()) {
					EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
				}

				Entity.moveEntity(world, dude, nextPos);
			}
			return false;
		}
	}

	protected boolean moveToFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler) {
		if (Functions.adjacent(dude.position, target.position)) {
			return true;
		} else {
			Point nextPos = Point.nextPositionDude(dude, world, target.position);

			if (!dude.position.equals(nextPos)) {
				Optional<Entity> occupant = Entity.getOccupant(world, nextPos);
				if (occupant.isPresent()) {
					EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
				}

				Entity.moveEntity(world, dude, nextPos);
			}
			return false;
		}
	}

	protected void nextImage(Entity entity) {
		entity.imageIndex = (entity.imageIndex + 1) % entity.images.size();
	}
	
	protected void performActions(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore,
			Entity miner) {

		Entity.removeEntity(world, entity);
		EventScheduler.unscheduleAllEvents(scheduler, entity);

		Entity.addEntity(world, miner);
		EventScheduler.scheduleActions(miner, scheduler, world, imageStore);
	}
	
	protected boolean transformNotFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		if (entity.resourceCount >= entity.resourceLimit) {
			Entity miner = createDudeFull(entity.id, entity.position, entity.actionPeriod, entity.animationPeriod,
					entity.resourceLimit, entity.images);

			performActions(entity, world, scheduler, imageStore, miner);

			return true;
		}

		return false;
	}

	protected void transformFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		Entity miner = createDudeNotFull(entity.id, entity.position, entity.actionPeriod, entity.animationPeriod,
				entity.resourceLimit, entity.images);

		performActions(entity, world, scheduler, imageStore, miner);
	}
	
	protected static Entity createDudeFull(String id, Point position, int actionPeriod, int animationPeriod,
			int resourceLimit, List<PImage> images) {
		return new Entity(EntityKind.DUDE_FULL, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod,
				0, 0);
	}
	
	

}
