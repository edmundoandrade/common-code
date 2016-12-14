package edworld.common.infra.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public abstract class QRCodeUtil {
	public static BufferedImage generateImage(String info, int width, int height, int margin) {
		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hintMap.put(EncodeHintType.MARGIN, margin);
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix;
		try {
			byteMatrix = qrCodeWriter.encode(info, BarcodeFormat.QR_CODE, width, height, hintMap);
		} catch (WriterException e) {
			throw new IllegalArgumentException(e);
		}
		BufferedImage image = new BufferedImage(byteMatrix.getWidth(), byteMatrix.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, byteMatrix.getWidth(), byteMatrix.getHeight());
		graphics.setColor(Color.BLACK);
		for (int x = 0; x < byteMatrix.getWidth(); x++)
			for (int y = 0; y < byteMatrix.getHeight(); y++)
				if (byteMatrix.get(x, y))
					graphics.fillRect(x, y, 1, 1);
		return image;
	}
}
