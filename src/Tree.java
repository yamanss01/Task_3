
public class Tree extends TreeAbstraction{
	
	public void executeTreeActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

		if (!transformPlant(entity, world, scheduler, imageStore)) {

			EventScheduler.scheduleEvent(scheduler, entity, Action.createActivityAction(entity, world, imageStore),
					entity.actionPeriod);
		}
	}
	
	public boolean transformTree(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		if (entity.health <= 0) {
			Entity stump = Functions.createStump(entity.id, entity.position,
					ImageStoreAbstract.getImageList(imageStore, Functions.STUMP_KEY));

			ActionAbstraction action = new Action();
			action.performActions(entity, world, scheduler, imageStore, stump);

			return true;
		}

		return false;
	}
	public boolean transformPlant(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		if (entity.kind == EntityKind.TREE) {
			return transformTree(entity, world, scheduler, imageStore);
		} else if (entity.kind == EntityKind.SAPLING) {
			SaplingAbstraction sapling = new Sapling();
			return sapling.transformSapling(entity, world, scheduler, imageStore);
		} else {
			throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", entity));
		}
	}



}
