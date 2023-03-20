
public abstract class WorldViewAbstraction {

	abstract void shiftView(WorldView view, int colDelta, int rowDelta);
	 
	abstract void drawViewport(WorldView view);
	 
	abstract void drawBackground(WorldView view);
	 
	abstract void drawEntities(WorldView view);
	 
	abstract Point worldToViewport(Viewport viewport, int col, int row);
}
