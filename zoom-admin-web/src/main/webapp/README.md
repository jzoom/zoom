java -classpath /Users/jzoom/.m2/repository/com/h2database/h2/1.3.176/h2-1.3.176.jar org.h2.tools.Console -web

/Users/jzoom/working/zoom/zoom-admin/admin

cd /Users/jzoom/eclipse-workspace/zoom-admin/src/main/webapp/
browser-sync start --proxy "localhost:8090" --files "**/*.css, **/*.html, **/*.js"


设计方案：
1、Page
增删查改叫做一个Page

一个Page包含如下信息：
（1）元数据
页面类型：（模板类型）
url
表的元数据：增加、修改、列表、搜索的字段
模板


2、特殊Page：
（1）地图
可以认为是查询的一种特殊方式，即为模板不一样，后端接口一样

（2）非数据库增删查改，如高德地图api
前端一样，后端定制接口

（3）条件增加、修改：
如在增加、修改的时候，选择了某个字段，则其余的字段会产生变化，
如选择个人还是企业之后，后续表单不同：（不同的路由解决）

（4）动态表单
需要动态增加字段或者内容
如增加的商户，需要新增网点

3、结论：
（1）通过模块id，获取元数据
（2）获取模板（Vue）
（3）创建模板（Vue）



问题：

1、创建PathVariable的controller必须重启才能加载方法参数的名称 
2、表单提交过之后，第二次提交会出现第一次提交的参数（已经解决)
3、


模板设计：
1、页面可以从模板来
2、模板数据源：
meta 接口
（1）database
（2）api


界面设计：
1、选择模板
2、选择api
3、填写api的数据
4、生成页面（保存到页面库或者更新）
5、


组件设计：
1、组件名称
2、组件模板
3、组件数据（基础数据除外，如comment,label等)


表装饰器
1、原表
2、源字段
3、类型

类型的使用场景
1、搜索
2、列表展示
3、编辑
4、详情


字典：
1、id找到组
2、name+value组成键值对





插件设计:
1、jar插件自带资源


系统暴露接口给插件调用

插件暴露接口给系统调用

系统设置hook，插件监听hook，然后做响应的处理

插件增加hook，系统调用hook

后台管理的插件：
1、需要能增加菜单项（有可能是一个项目，带子菜单）
2、需要单独工作
3、可以上传插件


解除绑定
主框架暴露接口：

MainFrame
void update(String event, Object sender, Object data);

插件暴露接口:
void startup( MainFrame );
void shutdown( MainFrame );


使用场景：
1、search
2、table
3、detail
4、edit

类型：
1、string
2、int
4、money
5、date
6、dateRange
7、time
8、timeRange
9、text
10、image
11、image flow
12、edit image
13、code
14、html
15、file
16、各类enum，需要定义   StringEnum （全部定义，无定义）
组件：
1、form
2、table
3、search
4、display




