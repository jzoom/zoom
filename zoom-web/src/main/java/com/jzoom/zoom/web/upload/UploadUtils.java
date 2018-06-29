package com.jzoom.zoom.web.upload;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class UploadUtils {
	private static DiskFileItemFactory factory = new DiskFileItemFactory();
	public static void doUpload(HttpServletRequest request) {

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		if (!ServletFileUpload.isMultipartContent(request)) {
			
			return;
		}
		try {
			List<FileItem> list = upload.parseRequest(request);
			for (FileItem item : list) {
				String name = item.getFieldName();
				String value = item.getString("UTF-8");
				
				if (item.isFormField()) {
					
				} else {
					
				}
			}
		} catch (Exception e) {
			
		}

	}
}
