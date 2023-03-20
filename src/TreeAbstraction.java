
public abstract class TreeAbstraction {

	abstract void executeTreeActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler);
	
	abstract boolean transformTree(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore);
	
	abstract boolean transformPlant(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
