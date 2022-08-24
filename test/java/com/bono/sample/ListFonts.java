package com.bono.sample;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Locale;

import org.junit.Test;

public class ListFonts {
	@Test
	public void listFonts() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Font[] allFonts = ge.getAllFonts();

		for (Font font : allFonts) {
			System.out.println(font.getFontName(Locale.US) + ":" + font);
		}
	}
}
