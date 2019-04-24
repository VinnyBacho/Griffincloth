import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.objdetect.HOGDescriptor;

public class PeopleDetector {

	/**
	 * Takes in a video frame and detects the people in it
	 * then it draws the detected people onto a blue background output frame
	 * TODO change it from rectangles to actual clouds
	 * @param args
	 */
	public static void main (String args[]){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
		// initialize the JFrame for video display
		JFrame jframe = new JFrame("Title");
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setSize(640, 480);
	    JLabel vidpanel = new JLabel();
	    jframe.setContentPane(vidpanel);
	    jframe.setVisible(true);

	    
	    // initialize people detector
        HOGDescriptor hog = new HOGDescriptor();
        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        
		// video to be analyzed
        VideoCapture vc = new VideoCapture("resources/TownCentreXVID.avi");
		vc.set(Videoio.CAP_PROP_FRAME_WIDTH, 640); // width
		vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480); // height
		
		// initialize matrix that will be frames of video
		Mat frame = new Mat();
		
		while (true){
			if (vc.read(frame)){
				// resize the frame
//				Imgproc.resize( frame, frame, new Size(800, 800) );
				
				// create output frame with sky blue background
				// we will draw clouds onto this frame from info in video frame
				Mat outputFrame = new Mat(new Size(1920, 1080), CvType.CV_8UC3);
				outputFrame.setTo(new Scalar(255,191,0));
				
				MatOfRect peds = new MatOfRect();
				MatOfDouble weights = new MatOfDouble();
				
				// Detect the people
				hog.detectMultiScale(frame, peds, weights);
								
				// turn the rectangles matrix of found people
				// into an array of rectangles
				Rect[] pedsArray = peds.toArray();
	            
				// get the x,y coordinates of people the matrices
				// because drawing clouds over a rectangle doesn't make as much sense I think
				ArrayList<Point> points = new ArrayList<Point>();
				for(Rect rect: pedsArray){
					for(int i = 0; i< 100; i++){
						frame.put(rect.x + i, rect.y + i, new double[]{255.0, 255.0, 255.0});
						System.out.println(rect.x + ", " + rect.y);
					}
					Point p = new Point(rect.x, rect.y);
					points.add(p);
				}
				
				// turn our list of points into a matrix
				MatOfPoint mp = new MatOfPoint();
				mp.fromList(points);
				
				// draw rectangle over the frame
				for (int i = 0; i < pedsArray.length; i++){
	                
					Imgproc.rectangle(outputFrame, pedsArray[i].tl(), pedsArray[i].br(), new Scalar(0, 255, 0, 255), 3);
//					Imgproc.(frame, mp, new Scalar(0, 0, 0));

	            }
								
				// turn the frame into an image
				ImageIcon image = new ImageIcon(matToBufferedImage(outputFrame));

	            // repaint image on jframe
		        vidpanel.setIcon(image);
		        vidpanel.repaint();
			}
			
		}
		
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
