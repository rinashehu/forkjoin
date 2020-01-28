import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class TestImageFilter {

	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		String choice = "s";
		BufferedImage image = null;
		String srcFileName = null;
		try {

			srcFileName = "src/IMAGE1.jpg";
			//srcFileName = args[0]+"/IMAGE1.jpg";
			File srcFile = new File(srcFileName);
			image = ImageIO.read(srcFile);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Usage: java TestAll <image-file>");
			System.exit(1);
		}
		catch (IIOException e) {
			System.out.println("Error reading image file " + srcFileName + " !");
			System.exit(1);
		}

		System.out.println("Source image: " + srcFileName);

		int w = image.getWidth();
		int h = image.getHeight();
		System.out.println("Image size is " + w + "x" + h);
		System.out.println();
	
		int[] src = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst = new int[src.length];
		long startTime = 0;
		long endTime = 0;
		System.out.println("Choose the processing mode: \n \"p\"- parallel \n \"s\"- sequential ");
		choice = input.nextLine();
		String mode = "";
		switch(choice){
			case "p":
				mode ="parallel";
				System.out.println("Starting "+mode+" image filter.");
				startTime = System.currentTimeMillis();
				ParallelFJImageFilter pF = new ParallelFJImageFilter(src, dst, 1,h,w);
				ForkJoinPool pool = new ForkJoinPool(16);
				pool.invoke(pF);
				endTime = System.currentTimeMillis();
			break;
			case "s":
				mode = "sequential";
				System.out.println("Starting "+mode+" image filter.");
				startTime = System.currentTimeMillis();

				ImageFilter filter0 = new ImageFilter(src, dst, w, h);
				filter0.apply();

				endTime = System.currentTimeMillis();
			break;
			default: System.out.println("Your mode is invalid"); return;
		}




		long tSequential = endTime - startTime; 
		System.out.println( mode+" image filter took " + tSequential + " milliseconds.");

		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst, 0, w);

		String dstName = srcFileName.replace(".jpg","") + "Filtered.jpg";
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);

		System.out.println("Output image: " + dstName);
	}
}
