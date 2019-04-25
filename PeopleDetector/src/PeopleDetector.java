import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

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
	    jframe.setSize(1920, 1080);
	    JLabel vidpanel = new JLabel();
	    jframe.setContentPane(vidpanel);
	    jframe.setVisible(true);

	    
	    // initialize people detector
        HOGDescriptor hog = new HOGDescriptor();
        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        
		// video to be analyzed
        VideoCapture vc = new VideoCapture("resources/TownCentreXVID.avi");
        
        // if you want to make video capture smaller you can (it'll be faster but less accurate)
//		vc.set(Videoio.CAP_PROP_FRAME_WIDTH, 640); // width
//		vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480); // height
		
		// initialize matrix that will be frames of video
		Mat frame = new Mat();
		Mat outputFrame = new Mat(new Size(1920, 1080), CvType.CV_8UC3);

		while (true){
			if (vc.read(frame)){
				// resize the frame
//				Imgproc.resize( frame, frame, new Size(800, 800) );
				
				// create output frame with sky blue background
				// we will draw clouds onto this frame from info in video frame
				// TODO: if you put this outside the while loop, the clouds will stack ontop eachother
				outputFrame.setTo(new Scalar(255,191,0));
				
				MatOfRect peds = new MatOfRect();
				MatOfDouble weights = new MatOfDouble();
				
				// Detect the people
				hog.detectMultiScale(frame, peds, weights);
								
				// turn the rectangles matrix of found people
				// into an array of rectangles
				Rect[] pedsArray = peds.toArray();
	            
				// Get coordinates of rectangle's corners for cloud drawing and then draw the cloud
				Point[] points = new Point[4];
				for(Rect rect: pedsArray){
					Point p = new Point(rect.x, rect.y);
					Point q = new Point(rect.br().x, rect.br().y);
					points[0] = p; // top left
					points[2] = q; // bottom right
					double w = rect.br().x - rect.x;
					double h = rect.br().y - rect.y;
					points[1] = new Point(rect.x+w, rect.y); // top right
					points[3] = new Point(rect.x, rect.y+h); // bottom left
					MatOfPoint mp = new MatOfPoint();
					
					mp.fromArray(points);
					List<MatOfPoint> ppt = new ArrayList<MatOfPoint>();
					ppt.add(mp);
					Imgproc.fillPoly(outputFrame, ppt, new Scalar(255,255,255), 8, 0, new Point(0,0));

				}
				
				
//				// draw rectangle over the frame (trying to make it clouds tho)
//				for (int i = 0; i < pedsArray.length; i++){
//	                
//					Imgproc.rectangle(outputFrame, pedsArray[i].tl(), pedsArray[i].br(), new Scalar(0, 255, 0, 255), 3);	
//					    
//					    
//
//	            }
								
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
