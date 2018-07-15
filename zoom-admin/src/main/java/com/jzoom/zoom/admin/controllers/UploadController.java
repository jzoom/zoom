package com.jzoom.zoom.admin.controllers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

import com.jzoom.zoom.common.codec.HashStr;
import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.web.annotation.Controller;
import com.jzoom.zoom.web.annotation.JsonResponse;

@Controller(key="upload")
public class UploadController {

	
	@JsonResponse
	public String image( FileItem file ) throws IOException {
		String name = HashStr.md5(UUID.randomUUID().toString()) + ".jpg";
		FileUtils.copyInputStreamToFile(file.getInputStream(), 
				new File("/Users/jzoom/SourceCode/mirror/centos6.9/www/uploads",
						name
						));
		
		return "http://localhost:8070/uploads/"+name;
		
	}
	
	@JsonResponse
	public String jar( FileItem file ) throws IOException {
		String name = HashStr.md5(UUID.randomUUID().toString()) + ".jar";
		FileUtils.copyInputStreamToFile(file.getInputStream(), 
				new File("/Users/jzoom/SourceCode/mirror/centos6.9/www/uploads",
						name
						));
		
		return "http://localhost:8070/uploads/"+name;
		
	}
}
