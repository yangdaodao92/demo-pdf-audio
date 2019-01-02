package com.yangdaodao.demopdfaudio.util;

import glodon.gcj.member.center.utils.util.ObsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LocalObsUtil {

	public static String endPoint = "obs.cn-north-1.myhwclouds.com";
	public static String ak = "6GBKY5WYZZLZTQZSOHR2";
	public static String sk = "0BRmyWXDdFv0iO7Bs4KYQjST4vrspmJJ0dm3Shr3";
	public static String bucketName = "dws-behaviour-data-operation";
	public static long partSize = 20;

	public static InputStream download(String objectKey) {
		return ObsUtil.download(endPoint, ak, sk, bucketName, objectKey);
	}

	public static boolean download(String objectKey, File file) throws IOException {
		return ObsUtil.download(endPoint, ak, sk, bucketName, objectKey, file, 0);
	}

	public static void upload(File file, String objectKey) throws IOException {
		ObsUtil.upload(endPoint, ak, sk, bucketName, partSize * 1024 * 1024L, file, objectKey);
	}

	public static List<String> listAllObjectKeys(String prefix) {
		return ObsUtil.listAllObjectKeys(endPoint, ak, sk, bucketName, prefix);
	}

}
