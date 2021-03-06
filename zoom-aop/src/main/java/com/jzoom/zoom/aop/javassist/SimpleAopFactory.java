package com.jzoom.zoom.aop.javassist;

import java.lang.reflect.Method;

import com.jzoom.zoom.aop.AopMaker;
import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.impl.AbsAopFactory;
import com.jzoom.zoom.aop.impl.ReflectMethodCaller;
import com.jzoom.zoom.aop.utils.JavassistUtils;
import com.jzoom.zoom.common.logger.Logger;
import com.jzoom.zoom.common.logger.Loggers;
import com.jzoom.zoom.common.utils.Classes;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;


/**
 * 核心使用javassist作为类字节码工具
 * @author jzoom
 *
 */
public class SimpleAopFactory extends AbsAopFactory {
	

	
	private static final Logger logger = Loggers.getLogger();
	
	private ClassPool classPool;

	
	
	public SimpleAopFactory(AopMaker...makers) {
		super(makers);
		this.classPool = JavassistUtils.getClassPool();
	}

	
	@Override
	public void destroy() {
		super.destroy();
		
		this.classPool = null;
		
		JavassistUtils.destroy();
	}

	@Override
	protected Class<?> enhance(Class<?> src, AopConfig[] configs) throws Exception {
		ClassPool classPool = this.classPool;
		
		// 创建一个子类
		CtClass subClass = classPool.makeClass(src.getName()+TAIL);
		subClass.setSuperclass(classPool.get(src.getName()));
		
		//创建静态变量 AopConfig[] ,名称为 _$configs
		CtField field = new CtField(classPool.get( AopConfig[].class.getName()  ), CONFIG_FIELD_NAME, subClass);
		field.setModifiers(Modifier.PROTECTED | Modifier.STATIC);
		subClass.addField(field);
		
		int index = 0;
		for (AopConfig aopConfig : configs) {
			
			logger.debug("为%s增强功能", aopConfig.getMethod());
			
			subClass.addMethod(toPublic(createRenameMethod(aopConfig.getMethod(), subClass)));
			subClass.addMethod(toPublic(createMethod(aopConfig.getMethod(), aopConfig.getInterceptors(),subClass,index++)));
			
		}
		
		// 生成MethodCaller,简单的用反射
		Class<?> resultClass = subClass.toClass(src.getClassLoader(), null);
		for (AopConfig aopConfig : configs) {
			final Method newMethod = resultClass.getDeclaredMethod("__"+aopConfig.getMethod().getName(), aopConfig.getMethod().getParameterTypes());
			aopConfig.setCaller( new ReflectMethodCaller(newMethod) );
		}
		
		//最后更新一下静态字段
		Classes.set(resultClass,CONFIG_FIELD_NAME,configs);
	
		
		
		return resultClass;
	}
	
	
	/**
	 * 创建一个重命名的函数，用来调用super
	 * 
	 * TestModel.test => TestModel$Enhance.__test
	 * 
	 * @return
	 * @throws CannotCompileException 
	 */
	private CtMethod createRenameMethod(Method method,CtClass declaring) throws CannotCompileException {
		StringBuilder sb = new StringBuilder();
		sb.append(getMethodDeclearation(method,true));
		sb.append("{");
		sb.append(getCallSuper(method) );
		sb.append("}");
		
		logger.debug("body is %s", sb.toString());
		
		
		
		return CtMethod.make(sb.toString(), declaring);
	}
	
	private CtMethod toPublic(CtMethod method) {
		method.setModifiers(Modifier.PUBLIC);
		return method;
	}

	private String getCallSuper(Method method) {
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		
		if(returnType!=Void.class) {
			sb.append("return ");
		}
		
		sb.append("super.").append(method.getName()).append("($$);");
		
		return sb.toString();
	}

	
	
	private CtMethod createMethod(Method method,MethodInterceptor[] methodInterceptors,CtClass subClass ,int index) throws CannotCompileException {
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(getMethodDeclearation(method,false));
		sb.append("{");
		sb.append(getBody(method, index, subClass.getName()));
		sb.append("}");
		
		logger.debug("body is %s", sb.toString());
		
		return CtMethod.make(sb.toString(), subClass);
	}
	
	private String class2Type(Class<?> clazz) {
		if(clazz == Void.class) {
			return "void";
		}
		return clazz.getName();
	}
	
	private void parseParamTypes(Method method,StringBuilder sb) {
		boolean first = true;
		int index = 0;
		for (Class<?> type : method.getParameterTypes()) {
			if(first) {
				first = false;
			}else {
				sb.append(",");
			}
			sb.append(type.getName());
			sb.append(" p");
			sb.append( index++  );
		}
	}
	
	private String getMethodDeclearation(Method method,boolean renameMethod) {
		
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		sb.append(class2Type(returnType));
		sb.append(" ");
		if(renameMethod) {
			sb.append("__");
		}
		sb.append(method.getName());
		sb.append("(");
		parseParamTypes(method,sb);
		sb.append(")");
		
		return sb.toString();
	}
	
	private String getBody(Method method,int index,String className) {
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		
		sb.append("com.jzoom.zoom.aop.MethodInvoker invoker = new com.jzoom.zoom.aop.MethodInvoker(");
		sb.append(className).append(".")
			.append(SimpleAopFactory.CONFIG_FIELD_NAME)
			.append("[").append(index).append("],").append(" $0, $args);");
		//throw
		sb.append("invoker.invoke();");
		
		if(returnType!=Void.class) {
			sb.append("return ($r)invoker.getReturnObject();");
		}
		
		return sb.toString();
	}
	
}
