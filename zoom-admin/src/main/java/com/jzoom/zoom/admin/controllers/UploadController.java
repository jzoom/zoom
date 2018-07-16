package com.jzoom.zoom.admin.controllers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.jzoom.zoom.common.ConfigurationConstants;

import com.jzoom.zoom.common.codec.HashStr;
import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.ioc.annonation.Inject;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;

@Controller(key="upload")
public class UploadController{
	
	@Inject(ConfigurationConstants.UPLOAD_DIR)
	private String uploadDir;
	@Inject(ConfigurationConstants.UPLOAD_FORMAT)
	private String uploadFormat;

	
	@JsonResponse
	public String image( FileItem file ) throws IOException {
		ConfigReader.getDefault();
		String name = HashStr.md5(UUID.randomUUID().toString()) + ".jpg";
		FileUtils.copyInputStreamToFile(file.getInputStream(), 
				new File(uploadDir,
						name
						));
		
		return String.format(uploadFormat, name);
		
	}
	
	@JsonResponse
	public String jar( FileItem file ) throws IOException {
		String name = HashStr.md5(UUID.randomUUID().toString()) + ".jar";
		FileUtils.copyInputStreamToFile(file.getInputStream(), 
				new File(uploadDir,
						name
						));
		return String.format(uploadFormat, name);
		
	}
}
