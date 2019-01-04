package com.yangdaodao.demopdfaudio.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author yangnx
 * @date 2019/1/4
 */
@Slf4j
public class Pdf2ImageUtil {

	public static void main(String[] args) {
		pdf2Image("G:/11.pdf", 300);
	}

	/***
	 * PDF文件转PNG图片，全部页数
	 *
	 * @param pdfFilePath pdf完整路径
	 * @param dpi dpi越大转换后越清晰，相对转换速度越慢
	 */
	public static boolean pdf2Image(String pdfFilePath, int dpi) {
		File file = new File(pdfFilePath);
		PDDocument pdDocument;
		try {
			String imgParentPath = file.getParent();
			String imageNamePrefix = file.getName().substring(0, file.getName().lastIndexOf('.')); // 获取图片文件名

			pdDocument = PDDocument.load(file);
			PDFRenderer renderer = new PDFRenderer(pdDocument);

			/* dpi越大转换后越清晰，相对转换速度越慢 */
			for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
				String imgFilePathPrefix = imgParentPath + File.separator + imageNamePrefix;
				File dstFile = new File(imgFilePathPrefix + "_" + String.valueOf(i + 1) + ".png");
				BufferedImage image = renderer.renderImageWithDPI(i, dpi);
				ImageIO.write(image, "png", dstFile);
			}

			pdDocument.close();
			log.info("PDF文档转PNG图片成功！");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
