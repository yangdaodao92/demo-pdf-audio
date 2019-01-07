package com.yangdaodao.demopdfaudio.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yangdaodao.demopdfaudio.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.apache.tomcat.util.compat.JrePlatform.IS_WINDOWS;


@Controller
@RequestMapping("assist")
@Slf4j
public class AssistController {

	public String jarParentPath = new ApplicationHome(getClass()).getSource().getParentFile().toString();
	public String outerStaticPath = !IS_WINDOWS ? (jarParentPath + "/outer-static") : "F:/BaiduNetdiskDownload/outer-static";

	@RequestMapping("init")
	public String init(String confName, ModelMap modelMap) {
		modelMap.addAttribute("confName", confName);
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

	@RequestMapping("loadUserConf/{confName}")
	@ResponseBody
	public JSONObject loadUserConf(@PathVariable String confName) throws IOException {
		File confFile = new File(outerStaticPath + "/conf-" + confName + ".json");
		JSONObject conf = confFile.exists() ? JSONObject.parseObject(FileUtils.readFileToString(confFile, "utf-8")) : new JSONObject();
		if (!confFile.exists()) {
			FileUtils.write(confFile, JSON.toJSONString(conf, true), "utf-8");
		}
		return conf;
	}

	@RequestMapping("updateUserConf/{confName}")
	@ResponseBody
	public void updateUserConf(@RequestBody JSONObject userConf, @PathVariable String confName) throws IOException {
		File confFile = new File(outerStaticPath + "/conf-" + confName + ".json");
		FileUtils.write(confFile, JSON.toJSONString(userConf, true), "utf-8");
	}

}
