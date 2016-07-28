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
		try {
			return new PDFReader(url, new MainPDFComponentLocator(), new MainBoxDetector(), new MainMarginDetector())
					.toXML();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static List<String> toTextLines(URL url) {
		try {
			return new PDFReader(url, new MainPDFComponentLocator(), new MainBoxDetector(), new MainMarginDetector())
					.toTextLines();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
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
}
