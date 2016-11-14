package edworld.common.infra.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import edworld.pdfreader4humans.PDFReader;
import edworld.pdfreader4humans.impl.MainBoxDetector;
import edworld.pdfreader4humans.impl.MainMarginDetector;
import edworld.pdfreader4humans.impl.MainPDFComponentLocator;

public abstract class PDFUtil {
	public static String toXML(URL url) {
		return toXML(url, 0);
	}

	public static String toXML(URL url, float containmentTolerance) {
		return getPDFReader(url, containmentTolerance).toXML();
	}

	public static List<String> toTextLines(URL url) {
		return toTextLines(url, 0);
	}

	public static List<String> toTextLines(URL url, float containmentTolerance) {
		return getPDFReader(url, containmentTolerance).toTextLines();
	}

	public static boolean isValid(File file) {
		try {
			InputStream input = new FileInputStream(file);
			try {
				return new String(Arrays.copyOf(IOUtils.toByteArray(input), 5)).equals("%PDF-");
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static PDFReader getPDFReader(URL url, float containmentTolerance) {
		try {
			return new PDFReader(url, new MainPDFComponentLocator(), new MainBoxDetector(), new MainMarginDetector(),
					containmentTolerance);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
