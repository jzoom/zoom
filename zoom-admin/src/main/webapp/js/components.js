/**
 * 
 * 全局字典定义：
 * templateUrl   :  静态模板地址,使用get方式取回模板
 * api           :  接口地址
 * 
 * 
 */


(function(){

    /**
     * 增删查改 -> 新增按钮
     */
    Vue.component('add-button',{
        props:['templateUrl','api','title','label','fullscreen','refresh'],      
        template:`<dialog-button type="primary" :label="label?label:'增加'" :form="{}" icon="el-icon-plus" :refresh="refresh" :title="title" :templateUrl="templateUrl" :api="api" :fullscreen="fullscreen"></dialog-button>`
    });

    Vue.component('curd-pane',{
        template:`<div class="curd-pan">
        <add-button 
            :fullscreen="fullscreen"
            :templateUrl="module+'/add'" 
            :api="module+'/add'" 
            :title="'增加'+comment" />
        <edit-button 
            :fullscreen="fullscreen"
            v-if="current != null" 
            :api="module+'/put/'+current.id"
            :templateUrl="module+'/edit'" 
            :dataUrl="module+'/get/'+current.id" 
            :title="'修改'+comment" />
        <del-button  
            v-if="current != null" 
            :api="module+'/del/'+current.id" 
            confirm="真的要删除吗,本操作不能撤销?" 
            :title="'删除'+comment" />
    </div>`,
        props:[
            'module',    //模块  /mod
            'comment',      //模块名称(中文注释)
            'current',
            'fullscreen'
        ],
    });

     /**
     * 增删查改 -> 删除按钮
     */
    Vue.component('del-button',{
        props:['api','title','label','fullscreen','refresh','confirm','data'],   
        template:`<api-button type="danger" :confirm="confirm" :label="label?label:'删除'" icon="el-icon-delete" :refresh="refresh" :title="title" :api="api" :data="data" :fullscreen="fullscreen"></dialog-button>`
    });
    /**
     * 增删查改 -> 编辑按钮
     */
    Vue.component('edit-button',{
        props:[ 'dataUrl',              //获取数据的url
        'templateUrl',                  //模板url
        'api',
        'title','label','fullscreen','refresh'],     
        template:`<dialog-button 
            ref="button" 
            type="info" 
            :label="label?label:'修改'" 
            :form="form" 
            icon="el-icon-edit" 
            :refresh="refresh" 
            :title="title" 
            :templateUrl="templateUrl" 
            :api="api" 
            :loading="loading" 
            :click="open" 
            :fullscreen="fullscreen"></dialog-button>`,
        data(){
            return {
                loading:false,
                form:{},
            }
        },
        methods:{
            async open(){
                this.loading = true;
                try{
                    this.form = await api(this.dataUrl,{});
                    //重新渲染
                    setTimeout(()=>{this.$refs.button.open();},10);
                }catch(e){
                    this.$root.$handleError(e);
                }finally{
                    this.loading = false;
                }
            },  
        }
    });

    /**
     * api按钮，点击之后，调用某个api,可以指定参数
     */
    Vue.component('api-button',{
        props:['api','title','confirm', 'data', 'label','fullscreen','refresh','icon','type','click','form'],
        template:`<el-button :type="type" size="medium" :icon="icon" @click="callapi">{{this.label}}</el-button>`,
        methods:{
            callapi(){
                if(this.confirm){
                    this.$confirm(this.confirm, '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                      }).then(() => {
                        //api
                        this.doCallapi();
                      }).catch(() => {
                        
                      });
                }else{
                    this.doCallapi();
                }
            },

            async doCallapi(){
                try{
                    await api(this.api,this.data||{});
                    this.$root.refresh();
                }catch(e){
                    this.$root.$handleError(e);
                }finally{
                    
                }
            }
        }
    });

    /**
     * 点击这个按钮弹出对话框，并加载按钮上指定的templateUrl模板,
     * 点击对话框的确定，调用指定api地址
     */
    Vue.component('dialog-button',{
        props:['templateUrl', //templateUrl
        'api',
        'title',
        'label',
        'fullscreen',
        'refresh',
        'icon',
        'type',
        'click',
        'form'      //表单数据
    ],      
       
        template:`<el-button :type="type" size="medium" :icon="icon" @click="checkOpen">
        {{this.label}}
        </el-button>`,
        data(){
            return {
                click:null
            }
        },
        methods:{
            checkOpen(){
                if(this.click){
                    this.click();
                    return;
                }
                this.open();
            },
            open(){
                this.$root.openDialog({
                    templateUrl:this.templateUrl,
                    api:this.api,
                    title:this.title,
                    fullscreen:this.fullscreen,
                    form:this.form,
                    refresh:this.refresh
                });
            }
        }
    });

    const Loading = {
        template: '<p>Loading...</p>'
    };

    Vue.component('main-router', {
        props:[
            'innerref'          //内部ref
        ],
        data() {
            return {
                loading:false,
                component:'',
            }
        },
        computed: {
            ViewComponent() {
                return this.$root.routes[this.component] || Loading
            }
        },
        methods: {
            load(templateUrl,props){
                this.component = null;
                setTimeout(async ()=>{
                    if (!this.$root.routes[templateUrl]) {
                        //开始加载url
                        this.loading = true;
                        try {
                            this.$root.routes[templateUrl] = await getTemplate(templateUrl, props);
                        } finally {
                            this.loading = false;
                        }
                    }
                    this.component = templateUrl;
                },0);
            }
        },
        render(h) {
            return h(this.ViewComponent,{ref:this.innerref ? this.innerref : 'submit'})
        }
    });

    Vue.component('submit-dialog',{
        props:[],
        template:`<el-dialog :fullscreen="fullscreen" :title="title" :visible.sync="show">
        <main-router ref="dialogcontent" innerref="form"></main-router>
        <div slot="footer" class="dialog-footer">
            <el-button @click="show = false">取 消</el-button>
            <el-button type="primary" @click="submit" :loading="submiting">确 定</el-button>
        </div>
    </el-dialog>`,
        data(){
            return {
                show:false,
                fullscreen:false,
                title:'',
                templateUrl:null,
                api:null,
                submiting:false,
                form:{},
                rules:{},
                onSuccess:null,     //表示表单提交成功之后的后续动作，一般来说是一个函数
            } 
        },
        methods:{
            getData(){
                return {
                    form:this.form,
                    rules:this.rules,                   
                }
            },

            loadDialog({title,templateUrl,fullscreen,api,form,rules={},onSuccess}){
                this.show = true;
                this.title = title;
                this.form = form;
                this.templateUrl = templateUrl;
                this.api = api,
                this.rules = rules;
                this.onSuccess = onSuccess || this.refreshMain;
                this.fullscreen = fullscreen;

                const loadFunc = ()=>{
                    if(!this.$refs.dialogcontent){
                        setTimeout(loadFunc, 20);
                        return;
                    }
                    this.$refs.dialogcontent.load(templateUrl,{
                        data:this.getData
                    });
                };
                if(this.$refs.dialogcontent){
                    loadFunc();
                }else{
                    setTimeout(loadFunc, 20);
                }
                
            },

            async doSubmit(promise){
                try{
                    this.submiting = true;
                    var result = await promise;
                    //表示成功
                    if(this.onSuccess){
                        this.onSuccess({result:result});
                    }
                    this.show = false;
                    this.$message.success(this.title+`成功`);
                 }catch(e){
                     this.$root.$handleError(e);
                 }finally{
                     this.submiting = false;
                 }
            },

            refreshMain(){
                this.$root.refresh();
            },

            async submit(){
                var dialogcontent =  this.$refs['dialogcontent'];
                //表单(不一定是el-form)
                var form = dialogcontent.$refs['form'];
                if(typeof form.submit == 'function'){
                    //采用自己的提交
                    this.doSubmit(form.submit());
                    return;
                }
                //表单内部的实际提交对象
                var submit = form.$refs['submit'];
                if(submit){
                    /*
                    submit.validate(async (ret)=>{
                        if(!ret){
                            return;
                        }
                        this.doSubmit();
                    });*/
                }else{

                }
                this.doSubmit(api(this.api,this.form));
            }
        },
    });

    /**
     * 表单input
     */
    Vue.component('form-input',{
        template:`<el-form-item :label="label" label-width="120px">
        <el-input :value="value" @input="$emit('input', event.target.value)" auto-complete="off"></el-input>
    </el-form-item>`,
        props:['label','value'],
    });

    function isNumber(val){

        var regPos = /^\d+(\.\d+)?$/; //非负浮点数
        var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
        if(regPos.test(val) || regNeg.test(val)){
            return true;
        }else{
            return false;
        }
    
    }


    Vue.component('form-switch',{
        template:`<el-form-item :label="label" label-width="120px">
            <el-switch :value="isSelected" @change="change"></el-switch>
        </el-form-item>`,
        props:['label','value'],
        computed:{
            isSelected(){
                if(typeof this.value == 'string'){
                    return this.value == '1';
                }
                if(isNumber(this.value)){
                    return this.value != 0;
                }
                return this.value;
            }
        },
        methods:{
            change(value){
                this.$emit('input', value);
            }
        }
    });

    /**
     * 树形选择器
     */
    Vue.component('tree-select',{
        props:['search','label','placeholder','api','value'],
        template:`<el-form-item :label="label" label-width="120px">
            <el-select :value="value" @change="change" :placeholder="placeholder">
                <el-option
                    v-for="item in tree"
                    :key="item.id"
                    :label="item.label"
                    :value="item.id">
                    <span>{{getSpace(item.level) + item.label }}</span>
                </el-option>
            </el-select>
        </el-form-item>`,
        computed:{
            tree:function(){
                var data = this.data;
                return this.getChildren(data,0);
            }
        },
        mounted(){
            this.refresh();
        },
        
        data(){
            return {
                data:[],
                loading:false,
                search:{},
            };
        },
        methods:{
            change(event){
                this.$emit('input', event);
            },
            refresh: async function () {
                this.loading = true;
                var self = this;
                try{
                    this.data = await api(this.api,this.search);
                }finally{
                    self.loading = false;
                }
            },
            getSpace(c){
                var str = "";
                for(var i=0; i < c; ++i ){
                    str += 'L';
                }
                return str;
            },
            getChildren(src,level){
                var arr = [];
                for(var i=0; i < src.length ;++i){
                    var data = src[i];
                    arr.push(data);
                    data.level = level;
                    if( data.children ){
                        arr = arr.concat( this.getChildren(data.children,level+1) );
                    }
                }
                return arr;
            }
        }
    });


    Vue.component('side-bar',{
        template:` <el-scrollbar wrapClass="scrollbar-wrapper">
        <el-menu default-active="0" @select="handleSelect" class="el-menu-vertical-demo">
            <template v-for="item in data">
                <el-submenu :index="item.id">
                    <template slot="title">
                        <i class="el-icon-location"></i>
                        <span>{{item.label}}</span>
                    </template>
                    <el-menu-item-group>
                        <template v-for="subitem in item.children">
                            <el-menu-item  :index="subitem.url">{{subitem.label}}</el-menu-item>
                        </template>
                    </el-menu-item-group>
                </el-submenu>
                
            </template>
            
        </el-menu>
    </el-scrollbar>`,
        data() {
            return {
                data: [],
            };
        },
        mounted() {
            this.refresh();
        },
        methods: {
            handleSelect(key, keyPath) {
                console.log(key, keyPath);
                var data = keyPath[1];
                if (data) {
                    this.$root.go("/" + data);
                }
            },
            async refresh() {
                try {
                    var list = await api('menu/list');
                    this.data = list;
                } catch (e) {

                }
            }
        }
    });

    Vue.component('main-frame',{
        template:`<div class="flex container">
        <div class="flex container column">
            <div class="navigation">
                <img src="img/logo.jpg" class="logo" />
            </div>
            <div class="flex container row" style="margin-top:10px">
                <div class="left">
                    <side-bar class="sidebar-container"></side-bar>
                </div>
                <div class="flex container" style="margin-left:10px; margin-right: 10px;">
                    <main-router ref="main" class="flex container"></main-router>
                </div>
            </div>
        </div>
        <submit-dialog ref="dialog"></submit-dialog>
    </div>`,
        mounted() {
            this.go("/mod/index", getCurdData("/mod/index"));
        },
        data() {
            return {
                routes: {}, //所有模板(路由)
                url:null,       //当前加载的url
                props:null,     //当前加载url的数据
            }
        },
        methods: {
            async openDialog(props) {
                this.$refs.dialog.loadDialog( props);
            },
            async go(url, props) {
                this.url = url;
                this.props = props || getCurdData(url);
                this.$refs.main.load(url,this.props);
            },
            async refresh(){
                this.$refs.main.load(this.url,this.props);
            },

            getCurrentData(){
                var content = this.$refs.main.$refs.submit;
                if(!content)return null;
                var data = content.current;
                return data;
            }
        }
    });

    
    const ErrorView = {
        template: '<p>Error...</p>'
    };

    Vue.component('custom-router',{
        props:['route'],
        render(h){
            return h(this.route || Loading);
        }
    });

    Vue.component('code-editor',{
        props:['value','style','language','theme'],
        template:`<monaco-editor 
            @mounted="loaded" 
            :code="value" 
            :theme="theme" 
            @change="change" 
            :language="language" 
            :style="style"></monaco-editor>`,
        data(){
            return {
                editor:null,
                text:null,
            }
        },
        beforeUpdate(){
            this.setValue(this.value);
        },
        methods:{
            loaded(editor){
                this.editor = editor;
                if(this.text){
                    editor.getModel().setValue(this.text);
                }
                
            },
            change(editor){
                this.text = editor.getModel().getValue();
                this.$emit('input',this.text);
            },
            setValue(value){
                if(value!=this.text){
                    this.text = value;
                    if(this.editor){
                        this.editor.getModel().setValue(value);
                    }
                    
                }
                
                
            }
        }
    });

    Vue.component('simple-table',{
        template:`<div class="pannel" >
        <el-table 
            :loading="loading" 
            style="width:100%" 
            :data="list" 
            highlight-current-row                
            @current-change="handleSelect">
            <template v-for="val in columns">
                <el-table-column :prop="val[0]" :label="val[1]" />
            </template>
        </el-table>
    </div>`,
        props:['loading','list','columns'],
        methods:{
            handleSelect(row){
                this.$emit('change',row);
            }
        }

    });

    Vue.component('simple-pagination',{
        template:`<div class="pannel" >
        <el-pagination 
            style="float:right" 
            @size-change="handle" 
            @current-change="handle" 
            :current-page="search._page" 
            :page-sizes="[20, 50, 100,200]"
            :page-size="search._pageSize" 
            layout="total, sizes, prev, pager, next, jumper" 
            :total="total">
        </el-pagination>
    </div>`,
        props:["search","total"],
        methods:{
            handle(){
                this.$emit('refresh');
            }
        }
    });

    Vue.component('simple-list',{
        template:`<div class="flex column" style="overflow:auto">
        <simple-table 
        :list="list" 
        :loading="loading" 
        @change="$emit('change',$event)"   
        :columns="columns" 
        />
    <simple-pagination 
        @refresh="$emit('refresh',$event)"  
       
        :search="search" :total="total" />
        </div>`,
        props:['loading','list','columns',"search","total"],
        
    });


})();