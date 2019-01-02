package com.yangdaodao.demopdfaudio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yangdaodao.demopdfaudio.common.Constants.baseObjectKey;

@Slf4j
public class FormatAudioPdf {
	public static String[] allSuffixArray = new String[]{"mp3", "m4a", "pdf", "png", "jpg"};
	public static List<String> audioSuffixList = Arrays.asList("mp3", "m4a");

	public static String basePath = "F:\\BaiduNetdiskDownload";
//	public static String courseGroupName = "03.产品思维30讲（完结）";
//	public static String courseGroupName = "16 五分钟商学院(完)";
	public static String courseGroupName = "13 贾行家说《聊斋》（完结）";
	public static String parentPath = basePath + "/" + courseGroupName;
	public static String parentPathFormat = basePath + "\\" + courseGroupName + "-Format";
	public static String parentPathGroup = basePath + "\\" + courseGroupName + "-Group";

	public static String outerStaticPath = "F:/BaiduNetdiskDownload/outer-static";

	@Test // 更新目录
	public void updateCatalogue() throws IOException {
		JSONObject catalogue = readCatalogue();
		catalogue.remove(courseGroupName);
		JSONObject courseGroupJSON = new JSONObject();
		List<File> allFiles = listFiles(new File(parentPathGroup));
		for (int i = 0; i < allFiles.size(); i++) {
			File file = allFiles.get(i);
			String obsKey = StringUtils.replace(StringUtils.replace(file.getAbsolutePath(), parentPathGroup, baseObjectKey + "/" + courseGroupName), "\\", "/");
			FileUtils.copyFile(file, new File(outerStaticPath + "/" + obsKey));

			JSONObject tempJSONObject = courseGroupJSON;
			for (String path : file.getParent().replace(parentPathGroup + "\\", "").split("\\\\")) {
				if (tempJSONObject.getJSONObject(path) == null) {
					tempJSONObject.put(path, new JSONObject());
				}
				tempJSONObject = tempJSONObject.getJSONObject(path);
			}
			// 写入目录
			try {
				writeCourseDetailToCatalogue(file, tempJSONObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info("{} / {}", i + 1, allFiles.size());
		}
		catalogue.put(courseGroupName, courseGroupJSON);
		writeCatalogue(catalogue);
	}

	/**
	 * 处理音频及PDF
	 * 本身为扁平结构：删除多余的名字
	 */
	@Test // 03.产品思维30讲（完结）
	public void formatAudioPdf() throws IOException {
		// 1 去除多余的名称内容
		File baseDirectoryFile = new File(parentPath);
		for (File file : listFiles(baseDirectoryFile)) {
			String fileName = file.getName();
			String newFileName = StringUtils.replace(fileName, "【更多课程微信：sex20101220】", "");
			newFileName = RegExUtils.removePattern(newFileName, "^lz");
			FileUtils.copyFile(file, new File(parentPathFormat + File.separator + newFileName));
		}
	}

	@Test
	public void generateCatalogue() throws IOException {
		// 0 大致检查下文件名是否相互匹配（音频、图片、pdf）
		// 1 按名称分组到文件夹
		Multimap<String, File> multimap = MultimapBuilder.linkedHashKeys().arrayListValues().build();
		List<File> fileList = listFiles(new File(parentPathFormat));
		for (File file : fileList) {
			String plainFileName = StringUtils.split(file.getName(), ".", 2)[0];
			multimap.put(plainFileName, file);
		}

		// 将文件拷贝到对应组下
		int index = 0;
		for (String plainFileName : multimap.keySet()) {
			for (File file : multimap.get(plainFileName)) {
				File copiedFile = new File(parentPathGroup + File.separator + plainFileName + File.separator + file.getName());
				FileUtils.copyFile(file, copiedFile);
			}
			log.info("{} / {}", ++index, multimap.keySet().size());
		}
	}

	/**
	 * 整理文件层级
	 * 最终结果
	 * 16 五分钟商学院(完)-Format -> 2016年9月 -> (lr0926.png、lr0926第一天从最有钱那个心理账户花钱.mp3、lr0926.mp3)
	 */
	@Test // 16 五分钟商学院(完)
	public void formatAudioPdf2() throws IOException {
		File baseDirectoryFile = new File(parentPath);

		Multimap<String, File> multimap = MultimapBuilder.linkedHashKeys().arrayListValues().build();
		for (File file : listFiles(baseDirectoryFile)) {
			multimap.put(file.getParentFile().getName(), file);
		}
		for (File file : listFiles(baseDirectoryFile)) {
			multimap.put(file.getParentFile().getParentFile().getName(), file);
		}

		for (String key : multimap.keys()) {
			for (File file : multimap.get(key)) {
				FileUtils.copyFile(file, new File(parentPathFormat + File.separator + key + File.separator + file.getName()));
			}
		}
	}

	@Test // 格式化并上传
	public void generateCatalogue2() throws IOException {
		Multimap<String, File> tempMultimap = MultimapBuilder.linkedHashKeys().arrayListValues().build();
		List<File> fileList = new ArrayList<>(FileUtils.listFiles(new File(parentPathFormat), new String[]{"mp3", "m4a", "png", "jpg"}, true));
		for (File file : fileList) {
			tempMultimap.put(findFileKey(file, "[lL][rR]\\d{4}"), file);
		}
		// 1 找到课程真实名称
		Multimap<String, File> multimap = MultimapBuilder.linkedHashKeys().arrayListValues().build();
		for (String key : tempMultimap.keys()) {
			multimap.putAll(findCourseName(tempMultimap.get(key)), tempMultimap.get(key));
		}
		// 2 按课程名分组
		for (String courseName : multimap.keys()) {
			for (File file : multimap.get(courseName)) {
				String newParentPath = StringUtils.replace(file.getParent(), parentPathFormat, parentPathGroup) + "/" + courseName + "/" + file.getName();
				FileUtils.copyFile(file, new File(newParentPath));
			}
		}
	}

	private void writeCourseDetailToCatalogue(File file, JSONObject tempJSONObject) throws IOException {
		JSONArray audioList = tempJSONObject.getJSONArray("audioList") == null ? new JSONArray() : tempJSONObject.getJSONArray("audioList");
		JSONArray pdfList = tempJSONObject.getJSONArray("pdfList") == null ? new JSONArray() : tempJSONObject.getJSONArray("pdfList");
		JSONArray pictureList = tempJSONObject.getJSONArray("pictureList") == null ? new JSONArray() : tempJSONObject.getJSONArray("pictureList");

		String fileName = file.getName();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		String obsKey = StringUtils.replace(StringUtils.replace(file.getAbsolutePath(), parentPathGroup, baseObjectKey + "/" + courseGroupName), "\\", "/");
		if (audioSuffixList.contains(suffix)) { // 如果是音频
			audioList.add(obsKey);
		} else if ("pdf".equals(suffix)) {
			pdfList.add(obsKey);
		} else {
			pictureList.add(obsKey);
		}

		if (audioList.size() > 0) {
			tempJSONObject.put("audioList", audioList);
		}
		if (pdfList.size() > 0) {
			tempJSONObject.put("pdfList", pdfList);
		}
		if (pictureList.size() > 0) {
			tempJSONObject.put("pictureList", pictureList);
		}
	}

	/**
	 * 查看所有的后缀
	 */
	@Test
	public void findAllSuffixes() {
		File baseDirectoryFile = new File(parentPath);
		Collection<File> files = FileUtils.listFiles(baseDirectoryFile, new IOFileFilter() {
			@Override
			public boolean accept(File file) {
				return true;
			}

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		}, new IOFileFilter() {
			@Override
			public boolean accept(File file) {
				return true;
			}

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		});
		Set<String> set = new HashSet<>();
		for (File file : files) {
			set.add(StringUtils.substring(file.getName(), file.getName().lastIndexOf(".") + 1));
		}
		System.out.println(StringUtils.join(set));
	}

	public String findCourseName(Collection<File> list) {
		String courseName = "";
		for (File file : list) {
			String filePlainName = StringUtils.substring(file.getName(), 0, file.getName().lastIndexOf("."));
			if (filePlainName.length() > courseName.length()) {
				courseName = filePlainName;
			}
		}
		return courseName;
	}

	/**
	 * 发现文件分组的关键 key
	 * @param regex lr0101 lr\d{4}
	 */
	public String findFileKey(File file, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(file.getName());
		if (matcher.find()) {
			return file.getParent() + matcher.group();
		} else {
			throw new RuntimeException(file.getName());
		}
	}

	//读取目录
	public JSONObject readCatalogue() {
		File catalogueFile = new File(outerStaticPath + File.separator + "catalogue.json");
		JSONObject catalogue = new JSONObject();
		if (catalogueFile.exists()) {
			try {
				catalogue = JSONObject.parseObject(FileUtils.readFileToString(catalogueFile, "utf-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return catalogue;
	}

	//保存目录
	public void writeCatalogue(JSONObject catalogue) {
		File catalogueFile = new File(outerStaticPath + File.separator + "catalogue.json");
		try {
			FileUtils.write(catalogueFile, JSON.toJSONString(catalogue, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<File> listFiles(File file) {
		return new ArrayList<>(FileUtils.listFiles(file, allSuffixArray, true));
	}

}
