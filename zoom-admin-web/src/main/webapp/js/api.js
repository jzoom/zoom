axios.defaults.headers.post['Content-Type'] = 'application/json';




var instance = axios.create({
  baseURL: '/',
  timeout: 30000,
});


var token = localStorage.getItem("token");

function isLogin(){
	return token;
}

function setToken(_token){
	token = _token;
	localStorage.setItem('token',token);
}

// 拦截request,设置全局请求为ajax请求
instance.interceptors.request.use(function(config){
  config.headers['token'] = token;
  config.headers['Content-Type'] = 'application/json';
  console.log(config);
  return config
});

instance.interceptors.response.use(function(response){
	return new Promise(function(resolve){
		console.log(response);
		resolve(response);
	});
},function(error){
	return Promise.reject(error);
});

var loadingTasks = {};
/**
 * 请求api
 */
async function api( api,data ={}){
	var param = JSON.stringify(data);
	var key = api + param;
	var created = false;
	try{
		NProgress.start();
		var response;
		if(key in loadingTasks){

		}else{
			loadingTasks[key] = instance.post(api,param);
			created = true;
		}
		response = await loadingTasks[key];
		return response.data;
	}catch(e){
		return parseError(e);
	}finally{
		if(created){
			delete loadingTasks[key];
		}
		NProgress.done();
	}

}

function parseError(e){
	var response = e.response;
		if(response){
			var status = response.status;
			var data = response.data;
			if(status == 500){
				return Promise.reject({code:'server',error:data});
			}
			if(status == 401){
				return Promise.reject({code:'auth',error:"登录过期"});
			}
			if(status == 402){
				return Promise.reject({code:'auth',error:"登录过期"});
			}
			if(status == 403){
				return Promise.reject({code:'access',error:"没有权限",data:data});
				
			}
			if(status == 404){
				return Promise.reject({code:'server',error:"本接口未找到"});
			}
			//其他错误
			if(status == 418){
				return Promise.reject({code:'message',error:data.error});
			}
			return Promise.reject({code:'unresolved',error:data});
		}else{
			return Promise.reject({code:'http'});
		}
}

async function load( ids ,data){
	for(var i=0; i < ids.length ; ++i){
		await define( ids[i].id,ids[i].data );
	}
	new Vue(Object.assign({el: "#app"},data));
}

/**
 * 获取动态template
 * @param {string} id   路由
 */
async function getTemplate( id ,props = {}){
	try{
		var response = await instance.get(id);
		var data = response.data;
		data = data.trim();
		if(data.startsWith('var')){
			eval(data);
			return $result;
		}
		if(data.startsWith('{') && data.endsWith("}")){
			eval("var $result="+data);
			return Vue.component(id, $result  );
		}
		return Vue.component(id, Object.assign({template: data,},props));
	}catch(e){
		return parseError(e);
	}
}

/**
 * 创建一个curd的列表应该有的数据和方法
 * @param {*} url 
 */
function getCurdData(url){
	return {
		data:function(){
			return {
				url:url,			//url
				current:null,		//当前选中的数据
				list:[],			//列表数据
				loading:false,		//是否在加载数据
				total:0,
				search:{			//搜索数据，可以从元数据拿
					_pageSize:30,
					_page:1,
					//然后这里是数据
				}
			}
		},
		mounted(){
			this.refresh();
		},
		methods:{
			handleSelect(data){		//选中数据
				this.current = data;
			},
			async refresh(){				//获取数据
				try{
					this.loading = true;
					let page = await api(this.url,this.search);
					this.list = page.list;
					this.search._pageSize = page.pageSize;
					this.search._page = page.page;
					this.total = page.total;
				}catch(e){
					this.$root.$handleError(e);
				}finally{
					this.loading = false;
				}
			
			},
		}
	};

}

function define(id,data){
	return axios.get('templates/' + id + '.txt')
		.then(function (response) {
			Vue.component(id, Object.assign(data, {
				template: response.data,
			}));
		})
		.catch(function (err) {
			console.log(err);
		});
}