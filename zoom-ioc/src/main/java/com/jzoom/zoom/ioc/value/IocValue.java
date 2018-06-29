package com.jzoom.zoom.ioc.value;

import com.jzoom.zoom.ioc.IocContainer;

/**
 * 一个可以被ioc容器特殊对待的值，可以是原始值，也可以是非原始值，非原始值必须以${}括起来
 * 1、原始值		  
 * 2、ref:bean名称  如 ${ref:mybean}
 * 3、cfg:配置名称  如 ${cnf:myconfig}
 * 4、env:环境变量名称 如 ${env:PATH}
 * @author jzoom
 *
 */
public interface IocValue {
	/**
	 * 获取值，
	 * 考虑到 依赖注入中的注入其他model，所以做了一层封装
	 * @param ioc
	 * @return
	 */
	Object get(IocContainer ioc);


}
