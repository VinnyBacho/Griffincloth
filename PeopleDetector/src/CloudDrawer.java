import org.opencv.core.Point;

public class CloudDrawer {
	
	/**
	 * Global intersection angles of two circles of the same radius
	 * @param p
	 * @param q
	 * @param radius
	 * @return
	 */
	public double[] intersection(Point p, Point q, int radius){
		
		double dx = q.x - p.x;
		double dy = q.y - p.y;
	
		double len = Math.sqrt(dx*dx + dy*dy);
		
		double a = 0.5 * len / radius;
		
		if (a < -1) a = -1;
		if (a > 1) a = 1;
		
		double phi = Math.atan2(dy, dx);
		double gamma = Math.acos(a);
				
		return new double[]{phi - gamma, Math.PI + phi + gamma};
		
	}
}
