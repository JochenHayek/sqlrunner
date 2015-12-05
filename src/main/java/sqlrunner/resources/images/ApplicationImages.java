package sqlrunner.resources.images;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * Class contains images as static variables.
 * This class is generated by the IconCodeGenerator
 *
 * author: jan
 * created at: Tue Nov 10 19:05:49 CET 2015
 */
public class ApplicationImages {

	public static final Image SQLRUNNER_PNG = createImage("/sqlrunner/resources/images/sqlrunner.png");

	private static Image createImage(String imageName) {
		Image image = null;
		try {
			image = Toolkit.getDefaultToolkit().getImage(ApplicationImages.class.getResource(imageName));
		} catch (Exception e) {
			System.err.println("unable to load image: " + imageName);
		}
		return image;
	}

}