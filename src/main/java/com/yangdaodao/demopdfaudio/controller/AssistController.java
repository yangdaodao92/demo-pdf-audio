package com.yangdaodao.demopdfaudio.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.apache.tomcat.util.compat.JrePlatform.IS_WINDOWS;


@Controller
@RequestMapping("assist")
public class AssistController {

	public String jarParentPath = new ApplicationHome(getClass()).getSource().getParentFile().toString();
	public String outerStaticPath = !IS_WINDOWS ? (jarParentPath + "/outer-static") : "F:/BaiduNetdiskDownload/outer-static";

	@RequestMapping({"init", "test"})
	public String test() {
		return "init";
	}

	@RequestMapping("loadCatalogue")
	@ResponseBody
	public JSONObject loadCatalogue() throws IOException {
//		String cataloguePath = jarParentPath + "/catalogue.json";
//		LocalObsUtil.download(baseObjectKey + "/catalogue.json", new File(cataloguePath));
		File catalogue = new File(outerStaticPath + "/catalogue.json");
		return JSONObject.parseObject(FileUtils.readFileToString(catalogue, "utf-8"));
	}

	@RequestMapping("loadObs")
	@ResponseBody
	public String loadObs(HttpServletResponse response, String obsKey) throws IOException {
//		File pdf = new File(outerStaticPath + "/" + obsKey);
//		if (!pdf.exists()) {
//			LocalObsUtil.download(obsKey, pdf);
//		}
		return "/" + obsKey;
	}

}
