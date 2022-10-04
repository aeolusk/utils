package com.bono.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageUtils {
	public static BufferedImage loadImage(Class<?> resourceLoader, String imagePath) {
		return loadImage(resourceLoader.getResource(imagePath));
	}

	public static BufferedImage loadImage(URL url) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(url);
		} catch (MalformedURLException mue) {
			log.warn("url: " + mue.getMessage());
		} catch (IllegalArgumentException iae) {
			log.warn("arg: " + iae.getMessage());
		} catch (IOException ioe) {
			log.warn("read: " + ioe.getMessage());
		}
		if (image == null) {
			image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		}
		return image;
	}
}
