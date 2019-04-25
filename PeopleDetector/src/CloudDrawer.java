import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class CloudDrawer {
	
	public static void main (String args[]){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
		// initialize the JFrame for video display
		JFrame jframe = new JFrame("Title");
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setSize(1920, 1080);
	    JLabel vidpanel = new JLabel();
	    jframe.setContentPane(vidpanel);
	    jframe.setVisible(true);

		Mat outputFrame = new Mat(new Size(1920, 1080), 0);

	    MatOfPoint a = new MatOfPoint(new Point(250,50));
	    MatOfPoint b = new MatOfPoint(new Point(450, 150));
	    MatOfPoint c = new MatOfPoint(new Point(350,450));
	    MatOfPoint d = new MatOfPoint(new Point(50,300));
	    List<MatOfPoint> mp = new ArrayList<MatOfPoint>();
	    mp.add(a);
	    mp.add(b);
	    mp.add(c);
	    mp.add(d);
	    
	    Imgproc.polylines(outputFrame, mp, true, new Scalar(0,0,0), 5);
	    
	 // turn the frame into an image
		ImageIcon image = new ImageIcon(matToBufferedImage(outputFrame));

        // repaint image on jframe
        vidpanel.setIcon(image);
        vidpanel.repaint();
	}
	
	/**
	 * Global intersection angles of two circles of the same radius
	 * @param previous
	 * @param curr
	 * @param radius
	 * @return
	 */
	public double[] intersection(double[] previous, double[] curr, int radius){
		Point p = new Point(previous[0], previous[1]);
		Point c = new Point(curr[0], curr[1]);
		double dx = c.x - p.x;
		double dy = c.y - p.y;
	
		double len = Math.sqrt(dx*dx + dy*dy);
		
		double a = 0.5 * len / radius;
		
		if (a < -1) a = -1;
		if (a > 1) a = 1;
		
		double phi = Math.atan2(dy, dx);
		double gamma = Math.acos(a);
				
		return new double[]{phi - gamma, Math.PI + phi + gamma};
		
	}
	
	
	public void cloud(JFrame jf, Point[] points) {
		
		// initial parameters for cloud
		int radius = 20;
		double overlap = 5/6;
		boolean stretch = true;
		
		// Create a list of circles
		ArrayList<double[]> circle = new ArrayList<double[]>();
		double delta = 2 * radius * overlap;
		
		Point prev = points[points.length-1];
		for (int i = 0; i< points.length; i++){
			Point current = points[i];
			
			// get distance between two points
			double dx = current.x - prev.x;
			double dy = current.y - prev.y;
			double len = Math.sqrt(dx*dx + dy*dy);
			dx = dx / len;
			dy = dy / len;
			
			// intermediary for delta
			double d = delta;
			
			if (stretch) {
				double n = (len / delta + 0.5);
				
				if (n < 1) n = 1;
				d = len / n;
			}
			
			for (int j = 0; j + 0.1 * d < len; j+=d){
				double[] adder = {prev.x + j *dx, prev.y + j *dy};
				circle.add(adder);
			}
			
			prev = current;
		}
		
//		// Determine intersection angles of circles
//		double[] previous = circle.get(circle.size() - 1);
//		for (int i = 0; i< circle.size(); i++){
//			double[] curr = circle.get(i);
//			double[] angle = intersection(previous, curr, radius);
//			
//			previous.length - 1 = angle[0];
//			
//			
//		}
		
		// Draw cloud
		
		
	}
	private static BufferedImage matToBufferedImage(Mat original) {
		// init stuff
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width*height*channels];
		original.get(0, 0 , sourcePixels);
		
		if ( original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
}
