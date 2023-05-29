package msdoilspill;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static Integer BoardHeight = null;
	public static void setBoardHeight(int value)
	{
		if(BoardHeight == null)
			BoardHeight = value;
		else throw new RuntimeException("BoardHeight may be set only once!");
	}

	private static Integer BoardWidth = null;
	public static void setBoardWidth(int value)
	{
		if(BoardWidth == null)
			BoardWidth = value;
		else throw new RuntimeException("BoardHeight may be set only once!");
	}
	private static Board instance = null;
	public static Board getInstance()
	{
		if(instance == null)
			instance = new Board(BoardWidth,BoardHeight);
		return instance;
	}

	private static final long serialVersionUID = 1L;
	public Cell[][] cells;
	private int size = 10;
	public int editType=0;

	private Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
		initialize(length, height);
	}

	public void iteration() {
		Spiller.spill();
		advection();
		spreading();
		seashoreInteraction();

		confirmCellMovenent();
		this.repaint();
	}

	//#region Iteration component functions
	public void advection(){
		for (int x = 1; x < cells.length -1; ++x)
			for (int y = 1; y < cells[x].length -1; ++y)
			{
				int deltaX_m = (int) (Globals.AdvectionAlpha*cells[x][y].cev.currentX_ms+Globals.AdvectionBeta*cells[x][y].cev.windX_ms)*Globals.simulationStep_s;
				int deltaY_m = (int) (Globals.AdvectionAlpha*cells[x][y].cev.currentY_ms+Globals.AdvectionBeta*cells[x][y].cev.windY_ms)*Globals.simulationStep_s;
				for (OilParticle ops : cells[x][y].civ.getParticles()) {
					ops.advectionMovement(deltaX_m, deltaY_m);
				}
			}
	}
	public void spreading(){
		for (int x = 0; x < cells.length; ++x)
			for (int y = 0; y < cells[x].length; ++y)
			{
				cells[x][y].calculateSpreading(cells[x][y+1],0,1); //Von Neumann.
				cells[x][y].calculateSpreading(cells[x+1][y],1,0);
			}
	}
	public void evaporation() {

	}
	public void emulsification() {

	}
	public void dispersion() {

	}
	public void viscosityChange() {

	}
	public void seashoreInteraction() {
		for (int x = 1; x<cells.length-1; ++x)
			for (int y = 1; y<cells[x].length-1; ++y){
				ArrayList<Integer> ocean_neighbours = new ArrayList<>();
				for (int x1=-1; x1<=1; ++x1){
					for (int y1=-1; y1<=1; ++y1){
						if (x1!=0 && y1!=0 && cells[x+x1][y+y1].type==0){
							ocean_neighbours.add((x1+1)*3 + (y1+1));
						}
					}
				}
				if (cells[x][y].type==1){
					double total_mass = cells[x][y].civ.getTotalMass_kg();
					double d_mass = -Math.log(2)/cells[x][y].cev.shoreline_half_life
							*total_mass*Globals.simulationStep_s;
					double probability = Math.abs(d_mass)/total_mass;
					for (OilParticle oil_p: cells[x][y].civ.getParticles()){
						if (RNG.getInstance().nextDouble()<probability){
							int r = RNG.getInstance().nextInt(ocean_neighbours.size());
							int cell_x = ocean_neighbours.get(r)/3-1;
							int cell_y = ocean_neighbours.get(r)%3 - 1;
							oil_p.locationDelta_x_m+=(cell_x-x)*CEV.cellSize_m;
							oil_p.locationDelta_y_m+=(cell_y-y)*CEV.cellSize_m;
						}
					}
				}
			}
	}

	public void confirmCellMovenent(){
		for (OilParticle op : OilParticle.allParticles) {
			op.confirmMovement();
		}
	}
	//#endregion

	public void clear() {
		for (int x = 0; x < cells.length; ++x)
			for (int y = 0; y < cells[x].length; ++y) {
				cells[x][y].clear();
			}
		this.repaint();
	}

	private void initialize(int length, int height) {
		cells = new Cell[length][height];

		for (int x = 0; x < cells.length; ++x)
			for (int y = 0; y < cells[x].length; ++y)
				cells[x][y] = new Cell();
	}

	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}


	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

		for (x = 1; x < cells.length-1; ++x) {
			for (y = 1; y < cells[x].length-1; ++y) {
				g.setColor(cells[x][y].getColor());
				g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < cells.length) && (x > 0) && (y < cells[x].length) && (y > 0)) {
			cells[x][y].type = editType;
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < cells.length) && (x > 0) && (y < cells[x].length) && (y > 0)) {
			cells[x][y].type= editType;
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}
	
}
