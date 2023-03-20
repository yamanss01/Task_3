import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler {
	public PriorityQueue<Event> eventQueue;
	public Map<Entity, List<Event>> pendingEvents;
	public double timeScale;

	public EventScheduler() { }

	public EventScheduler(double timeScale) {
		this.eventQueue = new PriorityQueue<>(new EventComparator());
		this.pendingEvents = new HashMap<>();
		this.timeScale = timeScale;
	}

	protected static void scheduleEvent(EventScheduler scheduler, Entity entity, Action action, long afterPeriod) {
		long time = System.currentTimeMillis() + (long) (afterPeriod * scheduler.timeScale);
		Event event = new Event(action, time, entity);

		scheduler.eventQueue.add(event);

		// update list of pending events for the given entity
		List<Event> pending = scheduler.pendingEvents.getOrDefault(entity, new LinkedList<>());
		pending.add(event);
		scheduler.pendingEvents.put(entity, pending);
	}

	protected static void unscheduleAllEvents(EventScheduler scheduler, Entity entity) {
		List<Event> pending = scheduler.pendingEvents.remove(entity);

		if (pending != null) {
			for (Event event : pending) {
				scheduler.eventQueue.remove(event);
			}
		}
	}

	protected static void scheduleActions(Entity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
		switch (entity.kind) {
		case DUDE_FULL:
			scheduleEvents(entity, scheduler, world, imageStore);
			break;

		case DUDE_NOT_FULL:
			scheduleEvents(entity, scheduler, world, imageStore);
			break;

		case OBSTACLE:
			scheduleEvent(scheduler, entity, ActionAbstraction.createAnimationAction(entity, 0), getAnimationPeriod(entity));
			break;

		case FAIRY:
			scheduleEvents(entity, scheduler, world, imageStore);
			break;

		case SAPLING:
			scheduleEvents(entity, scheduler, world, imageStore);
			break;

		case TREE:
			scheduleEvents(entity, scheduler, world, imageStore);
			break;

		default:
		}
	}

	private static void scheduleEvents(Entity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
		scheduleEvent(scheduler, entity, ActionAbstraction.createActivityAction(entity, world, imageStore), entity.actionPeriod);
		scheduleEvent(scheduler, entity, ActionAbstraction.createAnimationAction(entity, 0), getAnimationPeriod(entity));
	}
	
	protected static int getAnimationPeriod(Entity entity) {
		switch (entity.kind) {
		case DUDE_FULL:
		case DUDE_NOT_FULL:
		case OBSTACLE:
		case FAIRY:
		case SAPLING:
		case TREE:
			return entity.animationPeriod;
		default:
			throw new UnsupportedOperationException(
					String.format("getAnimationPeriod not supported for %s", entity.kind));
		}
	}

	protected void removePendingEvent(EventScheduler scheduler, Event event) {
		List<Event> pending = scheduler.pendingEvents.get(event.entity);

		if (pending != null) {
			pending.remove(event);
		}
	}

}
