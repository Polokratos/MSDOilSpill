package msdoilspill;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
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
	private int size = 3;
	public int editType=0;

	private Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
		initialize(length, height);
	}

	public void iteration(int iter_num) {
		Spiller.spill();
		advection();
		spreading(iter_num);
		emulsification();
		dispersion();
		viscosityChange();
		seashoreInteraction();

		confirmCellMovenent();
		this.repaint();
		System.out.println(cells[116][224].type);
	}

	//#region Iteration component functions
	public void advection(){
		for (int x = 1; x < cells.length -1; ++x)
			for (int y = 1; y < cells[x].length -1; ++y)
			{
				if (cells[x][y].type==3) {
					int deltaX_m = (int) (Globals.AdvectionAlpha * cells[x][y].cev.currentX_ms + Globals.AdvectionBeta * cells[x][y].cev.windX_ms) * Globals.simulationStep_s;
					int deltaY_m = (int) (Globals.AdvectionAlpha * cells[x][y].cev.currentY_ms + Globals.AdvectionBeta * cells[x][y].cev.windY_ms) * Globals.simulationStep_s;
					for (OilParticle ops : cells[x][y].civ.getParticles()) {
						ops.advectionMovement(deltaX_m, deltaY_m);
					}
				}
			}
	}
	public void spreading(int iter_num){
		for (int x = 1; x < cells.length-1; ++x)
			for (int y = 1; y < cells[x].length-1; ++y)
			{
				if (cells[x][y].type!=0) {
					cells[x][y].calculateSpreading(cells[x][y + 1], 0, 1, iter_num); //Von Neumann.
					cells[x][y].calculateSpreading(cells[x + 1][y], 1, 0, iter_num);
				}
			}
	}
	public void evaporation() {
//		for (int x = 1; x< cells.length-1; ++x){
//			for (int y = 1; y< cells[x].length-1; ++y){
//				double POI = 1000 * Math.exp(-(4.4 + Math.log(Globals.EvaporationTb)) *
//						(1.803 * (Globals.EvaporationTbI/cells[x][y].cev.temperature_K-1)-
//								0.803*Math.log(Globals.EvaporationTbI/cells[x][y].cev.temperature_K)));
//				double MI = 2.41 * Math.pow(10, -6) * (Math.pow(Globals.EvaporationTbI, 2.847) * ()
//				double Ei = 1.25 * Math.pow(10, -3) * MI * POI * xi / (8.314 * cells[x][y].cev.temperature_K);
//				double dMi = Ei * cells[x][y].cev.
//			}
//		}
	}
	public void emulsification() {
		for (int x=1; x<cells.length; ++x){
			for (int y=1; y<cells[x].length; ++y){
				if (cells[x][y].type==3) {
					double wind_state = Math.pow(Math.pow(Math.pow(cells[x][y].cev.windX_ms, 2) + Math.pow(cells[x][y].cev.windY_ms, 2), 0.5) + 1, 2);
					for (OilParticle op : cells[x][y].civ.getParticles()) {
						double Yk = op.OPS.water_mass_kg / (op.OPS.water_mass_kg + op.OPS.mass_kg);
						double dYk = 2.0 * 10e-6 * wind_state * (1 - Yk / 0.7) * Globals.simulationStep_s;
						op.Y = Yk;
						op.dY = dYk;
						op.OPS.water_mass_kg = (Yk + dYk) * op.OPS.mass_kg / (1 - Yk + dYk);
					}
				}
			}
		}
	}
	public void dispersion() {
		for (int x=1; x<cells.length; ++x){
			for (int y=1; y<cells[x].length; ++y){
				double Da = 0.11 * Math.pow((Math.pow(Math.pow(cells[x][y].cev.windX_ms, 2) + Math.pow(cells[x][y].cev.windY_ms, 2), 0.5)+1), 2);
				for (OilParticle op: cells[x][y].civ.getParticles()){
					double slick_thickness = 100 * op.OPS.getVolume()/(RNG.getInstance().nextDouble()*1000+0.003); // tutaj mamy problem z danymi
					double Db = 1/(1+50*Math.pow(op.OPS.getDynamicViscosity(), 0.5)* slick_thickness*Globals.SurfaceTension_dyne_over_s);
					double dm = op.OPS.mass_kg * Da * Db/3600 * Globals.simulationStep_s;
					op.OPS.mass_kg-=dm;
				}
			}
		}
	}
	public void viscosityChange() {
		for (OilParticle op: OilParticle.allParticles){
			double du = 2.5*op.OPS.getDynamicViscosity()*op.getdY()/Math.pow(1-0.7*op.getY(),2);
			op.OPS.dynamic_viscosity-=du;
		}
	}
	public void seashoreInteraction() {
		for (int x = 1; x<cells.length-1; ++x)
			for (int y = 1; y<cells[x].length-1; ++y){
				if (cells[x][y].type==2) {
					ArrayList<Integer> ocean_neighbours = new ArrayList<>();
					for (int x1 = -1; x1 <= 1; ++x1) {
						for (int y1 = -1; y1 <= 1; ++y1) {
							if (x1 != 0 && y1 != 0 && cells[x + x1][y + y1].type == 0) {
								ocean_neighbours.add((x1 + 1) * 3 + (y1 + 1));
							}
						}
					}
					if (ocean_neighbours.size() > 0) {
						double total_mass = cells[x][y].civ.getTotalMass_kg();
						double d_mass = -Math.log(2) / cells[x][y].cev.shoreline_half_life
								* total_mass * Globals.simulationStep_s;
						double probability = Math.abs(d_mass) / total_mass;
						for (OilParticle oil_p : cells[x][y].civ.getParticles()) {
							if (RNG.getInstance().nextDouble() < probability) {
								int r = RNG.getInstance().nextInt(ocean_neighbours.size());
								int cell_x = ocean_neighbours.get(r) / 3 - 1;
								int cell_y = ocean_neighbours.get(r) % 3 - 1;
								oil_p.locationDelta_x_m += (cell_x - x) * CEV.cellSize_m;
								oil_p.locationDelta_y_m += (cell_y - y) * CEV.cellSize_m;
							}
						}
					}
				}
			}
	}

	public void confirmCellMovenent(){
		for (OilParticle op : OilParticle.allParticles) {
			op.confirmMovement();
		}
		OilParticle.allParticles.removeIf(op -> op.OPS.mass_kg <= 0);
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

        try {
            MapLoader.initCells(length, height, "./app/src/main/resources/map.txt", cells);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load cell types from file: " + e.getMessage());
            System.exit(1);
        }
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

	public void componentResized(ComponentEvent e) {}

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
