/**
 * 
 * 全局字典定义：
 * templateUrl   :  静态模板地址,使用get方式取回模板
 * api           :  接口地址
 * 
 * 
 */


(function () {

    /**
     * 增删查改 -> 新增按钮
     */
    Vue.component('add-button', {
        props: ['templateUrl', 'api', 'title', 'label', 'fullscreen', 'refresh'],
        template: `<dialog-button ref="button" type="primary" :label="label?label:'增加'" :form="form" icon="el-icon-plus" :refresh="refresh" :title="title" :templateUrl="templateUrl" :api="api" 
        :click="open" 
        :fullscreen="fullscreen"></dialog-button>`,
        data() {
            return {
                form: {}
            }
        },
        methods: {
            open() {
                this.form = {};
                this.$refs.button.open();
            }
        }
    });

    Vue.component('child-item', {
        props: ['children'],
        render(h) {
            return h(this.children);
        }
    })

    
    Vue.component('curd-pane', {
        template: `<div class="table-title">
            <div class="action-button-pannel">
                <add-button 
                    :fullscreen="fullscreen"
                    :templateUrl="module+'/add'" 
                    :api="module+'/add'" 
                    :title="'增加'+comment" />
                <edit-button 
                    :fullscreen="fullscreen"
                    v-if="current != null" 
                    :api="module+'/put/'+getId()"
                    :templateUrl="module+'/edit'" 
                    :dataUrl="module+'/get/'+getId()" 
                    :title="'修改'+comment" />
                <del-button  
                    v-if="current != null" 
                    :api="module+'/del/'+getId()" 
                    confirm="真的要删除吗,本操作不能撤销?" 
                    :title="'删除'+comment" />
                <slot></slot>
            </div>
            <template v-if="columns&&search" v-for="(column,index) in columns">
                <el-input
                    :placeholder="column[1]" 
                    @input="()=>{search[column[0]]=event.target.value}"
                    :value="search[column[0]]"
                    style="width:100px;margin-right:3px" 
                    size="mini" />
            </template>
            
            <el-button v-if="refresh" @click="refresh" type="primary" size="mini" icon="el-icon-edit">筛选</el-button>
        </div>`,
        props: [
            'module', //模块  /mod
            'comment', //模块名称(中文注释)
            'current',
            'refresh',
            'fullscreen',
            'search',
            'columns',
            'pk'
        ],
        methods:{
            getId(){
                if(!this.current)return null;
                var id;
                var pk = this.pk;
                if( Array.isArray(pk) ){
                    id =  (data)=>{
                        var str = "";
                        var first = true;
                        pk.map((p)=>{
                            if(first)first = false;
                            else str += "__";
                            str += data[p];
                        });
                        return str;
                    };
                }else{
                    id = (data)=>{
                        return data[pk || "id"];
                    };
                }
                
                return id( this.current );
            }
        },
        data(){
            return {
                id:null
            }
        }
        
    });

    /**
     * 增删查改 -> 删除按钮
     */
    Vue.component('del-button', {
        props: ['api', 'title', 'label', 'fullscreen', 'refresh', 'confirm', 'data'],
        template: `<api-button type="danger" :confirm="confirm" :label="label?label:'删除'" icon="el-icon-delete" :refresh="refresh" :title="title" :api="api" :data="data" :fullscreen="fullscreen"></dialog-button>`
    });
    /**
     * 增删查改 -> 编辑按钮
     */
    Vue.component('edit-button', {
        props: ['dataUrl', //获取数据的url
            'templateUrl', //模板url
            'api',
            'title', 'label', 'fullscreen', 'refresh'
        ],
        template: `<dialog-button 
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
        data() {
            return {
                loading: false,
                form: {},
            }
        },
        methods: {
            async open() {
                this.loading = true;
                try {
                    this.form = await api(this.dataUrl, {});
                    //重新渲染
                    setTimeout(() => {
                        this.$refs.button.open();
                    }, 10);
                } catch (e) {
                    this.$root.$handleError(e);
                } finally {
                    this.loading = false;
                }
            },
        }
    });

    /**
     * api按钮，点击之后，调用某个api,可以指定参数
     */
    Vue.component('api-button', {
        props: ['api', 'title', 'confirm', 'data', 'label', 'fullscreen', 'refresh', 'icon', 'type', 'click', 'form'],
        template: `<el-button :type="type" size="mini" :icon="icon" @click="callapi">{{this.label}}</el-button>`,
        methods: {
            callapi() {
                if (this.confirm) {
                    this.$confirm(this.confirm, '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        //api
                        this.doCallapi();
                    }).catch(() => {

                    });
                } else {
                    this.doCallapi();
                }
            },

            async doCallapi() {
                try {
                    await api(this.api, this.data || {});
                    this.$root.refresh();
                } catch (e) {
                    this.$root.$handleError(e);
                } finally {

                }
            }
        }
    });

    /**
     * 点击这个按钮弹出对话框，并加载按钮上指定的templateUrl模板,
     * 点击对话框的确定，调用指定api地址
     */
    Vue.component('dialog-button', {
        props: ['templateUrl', //templateUrl
            'api',
            'title',
            'label',
            'fullscreen',
            'refresh',
            'icon',
            'type',
            'click',
            'form' //表单数据
        ],

        template: `<el-button :type="type" size="mini" :icon="icon" @click="checkOpen">
        {{this.label}}
        </el-button>`,
        data() {
            return {
                click: null
            }
        },
        methods: {
            checkOpen() {
                if (this.click) {
                    this.click();
                    return;
                }
                this.open();
            },
            open() {
                this.$root.openDialog({
                    templateUrl: this.templateUrl,
                    api: this.api,
                    title: this.title,
                    fullscreen: this.fullscreen,
                    form: this.form,
                    refresh: this.refresh
                });
            }
        }
    });

    const Loading = {
        template: '<p>Loading...</p>'
    };

    Vue.component('main-router', {
        props: [
            'innerref' //内部ref
        ],
        data() {
            return {
                loading: false,
                component: '',
            }
        },
        computed: {
            ViewComponent() {
                return this.$root.routes[this.component] || Loading
            }
        },
        methods: {
            async loadTemplate(templateUrl, props) {
                //开始加载url
                this.loading = true;
                try {
                    this.$root.routes[templateUrl] = await getTemplate(templateUrl, props);
                } finally {
                    this.loading = false;
                }
            },

            load(templateUrl, props) {
                this.component = null;
                setTimeout(async () => {
                    //缓存了,如果不缓存,每次都读取
                    /*if (!this.$root.routes[templateUrl]) {
                       await this.loadTemplate(templateUrl,props);
                    }*/
                    try{
                        await this.loadTemplate(templateUrl, props);
                        this.component = templateUrl;
                    }catch(e){
                        this.$root.$handleError(e);
                    }
                    
                    
                }, 0);
            }
        },
        render(h) {
            return h(this.ViewComponent, {
                ref: this.innerref ? this.innerref : 'submit'
            })
        }
    });


    /**
     * 表单编辑表格
     */
    Vue.component('form-table', {

    });

    Vue.component('submit-dialog', {
        props: [],
        template: `<el-dialog :fullscreen="fullscreen" :title="title" :visible.sync="show">
        <main-router ref="dialogcontent" innerref="form"></main-router>
        <div slot="footer" class="dialog-footer">
            <el-button @click="show = false">取 消</el-button>
            <el-button type="primary" @click="submit" :loading="submiting">确 定</el-button>
        </div>
    </el-dialog>`,
        data() {
            return {
                show: false,
                fullscreen: false,
                title: '',
                templateUrl: null,
                api: null,
                submiting: false,
                form: {},
                rules: {},
                onSuccess: null, //表示表单提交成功之后的后续动作，一般来说是一个函数
            }
        },
        methods: {
            getData() {
                return {
                    form: this.form,
                    rules: this.rules,
                }
            },

            loadDialog({
                title,
                templateUrl,
                fullscreen,
                api,
                form,
                rules = {},
                onSuccess
            }) {
                this.show = true;
                this.title = title;
                this.form = form;
                this.templateUrl = templateUrl;
                this.api = api,
                    this.rules = rules;
                this.onSuccess = onSuccess || this.refreshMain;
                this.fullscreen = fullscreen;

                const loadFunc = () => {
                    if (!this.$refs.dialogcontent) {
                        setTimeout(loadFunc, 20);
                        return;
                    }
                    this.$refs.dialogcontent.load(templateUrl, {
                        data: this.getData
                    });
                };
                if (this.$refs.dialogcontent) {
                    loadFunc();
                } else {
                    setTimeout(loadFunc, 20);
                }

            },

            async doSubmit(promise) {
                try {
                    this.submiting = true;
                    var result = await promise;
                    //表示成功
                    if (this.onSuccess) {
                        this.onSuccess({
                            result: result
                        });
                    }
                    this.show = false;
                    this.$message.success(this.title + `成功`);
                } catch (e) {
                    this.$root.$handleError(e);
                } finally {
                    this.submiting = false;
                }
            },

            refreshMain() {
                this.$root.refresh();
            },

            async submit() {
                var dialogcontent = this.$refs['dialogcontent'];
                //表单(不一定是el-form)
                var form = dialogcontent.$refs['form'];
                if (typeof form.submit == 'function') {
                    //采用自己的提交
                    this.doSubmit(form.submit());
                    return;
                }
                //表单内部的实际提交对象
                var submit = form.$refs['submit'];
                if (submit) {
                    /*
                    submit.validate(async (ret)=>{
                        if(!ret){
                            return;
                        }
                        this.doSubmit();
                    });*/
                } else {

                }
                this.doSubmit(api(this.api, this.form));
            }
        },
    });

    /**
     * 表单input
     */
    Vue.component('form-input', {
        template: `<el-form-item :label="label" label-width="120px">
        <el-input :value="value" @input="$emit('input', event.target.value)" auto-complete="off"></el-input>
    </el-form-item>`,
        props: ['label', 'value'],
    });


    Vue.component('dynamic-table',{
        template:`
        <div>
            <el-button @click="handleAdd" type="text">增加</el-button>
            <el-table
                size="small"
                :data="columns"
                style="width:100%">
                <el-table-column
                    label="表"
                    width="180">
                    <template slot-scope="scope">
                        <auto-complete
                            v-model="scope.row.table"
                            labelField="table"
                            idField="table"
                            keyField="target_table"
                            api="deco_table/list" />
                    </template>
                </el-table-column>
                <el-table-column
                    label="条件">
                    <template slot-scope="scope">
                        <el-input placeholder="othertable.id=thistable.id" v-model="scope.row.on"  />
                    </template>
                </el-table-column>
            </el-table>
        </div>`,
        props:['value'],
        data(){
            return {
                columns:[]
            };
        },
        mounted(){
            if(Array.isArray(this.value)){
                this.columns = this.value;
            }
        },
        methods:{
            handleAdd(){
                this.columns.push({
                    table:'',
                    on:''
                });
                this.$emit('input',this.columns);
            }
        }

    })

    Vue.component('quick-filter-pane',{
        
    });

    Vue.component('form-jar',{
        template: `<el-form-item :label="label" label-width="120px">
        <div>
        <el-upload
            action="/upload/jar"
            :show-file-list="false"
            :on-success="handleSuccess"
            :on-preview="handlePreview">
            <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
            <a v-if="url" :href="url">下载</a>
            <input type="hidden" :value="value" />
        </div>
    </el-form-item>`,
        props: ['label', 'value'],
        data(){
            return {
                url:''
            }
        },
        mounted(){
            if(this.value){
                this.url = this.value;
            }
        },
        methods: {
            handleSuccess(res,file){
              console.log(res);
              this.url = res;
              this.$emit('input', this.url);
            },
            handlePreview(file) {
              console.log(file);
            },
          }
    });

    Vue.component('form-image', {
        template: `<el-form-item :label="label" label-width="120px">
        <div>
        <el-upload
            action="/upload/image"
            :show-file-list="false"
            :on-success="handleSuccess"
            :on-preview="handlePreview">
            <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
            <img v-if="url" :src="url" width="200" height="130" style="margin-top:3px" />
            <input type="hidden" :value="value" />
        </div>
    </el-form-item>`,
        props: ['label', 'value'],
        data(){
            return {
                url:''
            }
        },
        mounted(){
            if(this.value){
                this.url = this.value;
            }
        },
        methods: {
            handleSuccess(res,file){
              console.log(res);
              this.url = res;
              this.$emit('input', this.url);
            },
            handlePreview(file) {
              console.log(file);
            },
          }
    });

    function isNumber(val) {

        var regPos = /^\d+(\.\d+)?$/; //非负浮点数
        var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
        if (regPos.test(val) || regNeg.test(val)) {
            return true;
        } else {
            return false;
        }

    }


    Vue.component('form-switch', {
        template: `<el-form-item :label="label" label-width="120px">
            <el-switch :value="isSelected" @change="change"></el-switch>
        </el-form-item>`,
        props: ['label', 'value'],
        computed: {
            isSelected() {
                if (typeof this.value == 'string') {
                    return this.value == '1';
                }
                if (isNumber(this.value)) {
                    return this.value != 0;
                }
                return this.value;
            }
        },
        methods: {
            change(value) {
                this.$emit('input', value);
            }
        }
    });

    Vue.component('form-select', {
        props: ['search', 'label', 'placeholder', 'api', 'value', 'labelField', 'size'],
        template: 
        `<el-form-item :label="label" label-width="120px">
            <api-select 
                :api="api" 
                :search="search" 
                :labelField="labelField" 
                :value="value" 
                :size="size" 
                @selecteded="change" 
                :placeholder="placeholder" />
        </el-form-item>`,
        methods: {
            change(event) {
                this.$emit('input', event.id);
            }
        }
    });


    Vue.component('auto-complete', {
        props: ['search', 
            'label', 
            'placeholder', 
            'api',
            'keyField',
            'value', 
            'idField',
            'labelField', 
            'size'],
        template:
            `<el-select 
                :value="value" 
                :size="size" 
                remote
                filterable
                reserve-keyword
                placeholder="请输入关键词"
                :remote-method="remoteMethod"
                :loading="loading"
                @change="change" >
                    <el-option
                        v-for="item in data"
                        :key="item[idField||'id']"
                        :label="item[labelField]"
                        :value="item[idField||'id']">
                    </el-option>
            </el-select>`,
        mounted() {
        },
        data() {
            return {
                data: [],
                loading: false,
                search: {},
            };
        },
        methods: {
            change(event) {
                console.log('======change===='+event)
                this.$emit('input', event);
                //data
                for (var i in this.data) {
                    if (event == this.data[i].id) {
                        this.$emit('selecteded', this.data[i]);
                        break;
                    }
                }
            },
            remoteMethod(query) {
                if (query !== '') {
                   this.refresh(query);
                } else {
                    this.data= [];
                }
            },
            refresh: async function ( query ) {
                this.loading = true;
                var self = this;
                try {
                    let querydata = {};
                    querydata['like@'+this.keyField] = query;
                    this.data = await api(this.api,Object.assign({}, this.search , querydata) );
                } finally {
                    self.loading = false;
                }
            },
        }
    });

    Vue.component('api-select', {
        props: ['search', 
            'label', 
            'placeholder', 
            'api', 
            'value', 
            'labelField', 
            'size'],
        template: `<el-select 
            :value="value" 
            :size="size" 
            :loading="loading"
            @change="change" 
            :placeholder="placeholder">
                <el-option
                    v-for="item in data"
                    :key="item.id"
                    :label="item[labelField]"
                    :value="item.id">
                </el-option>
            </el-select>`,
        mounted() {
            this.refresh();
        },
        data() {
            return {
                data: [],
                loading: false,
                search: {},
            };
        },
        methods: {
            change(event) {
                console.log('======change===='+event)
                this.$emit('input', event);
                //data
                for (var i in this.data) {
                    if (event == this.data[i].id) {
                        this.$emit('selecteded', this.data[i]);
                        break;
                    }
                }

            },
            refresh: async function () {
                this.loading = true;
                var self = this;
                try {
                    this.data = await api(this.api, this.search);
                } finally {
                    self.loading = false;
                }
            },
        }
    });


    Vue.component('icon-selector',{
        
    });

    Vue.component('simple-action-list',{
        template:``,
    });

    

    Vue.component('side-bar', {
        template: ` 
        <el-menu default-active="0" @select="handleSelect" class="el-menu-vertical-demo">
            <template v-for="item in data">
                <el-submenu :index="item.id">
                    <template slot="title">
                        <i :class="item.icon?item.icon:'el-icon-location'"></i>
                        <span>{{item.label}}</span>
                    </template>
                    <el-menu-item-group>
                        <template v-for="subitem in item.children">
                            <el-menu-item :index="subitem.url">
                                <template slot="title">
                                <i :class="subitem.icon"></i>
                                    {{subitem.label}}
                                </template>

                            </el-menu-item>
                        </template>
                    </el-menu-item-group>
                </el-submenu>
                
            </template>
            
        </el-menu>`,
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


    Vue.component('main-login', {
        template: `
        <div style="position:absolute;margin:auto;
        top: 0; left: 0; bottom: 0; right: 0; border-radius:10px; background-color:#fff; height:250px; width:400px;padding:30px; border:1px solid #ccc">
    <div style="width:100%; text-align:center;margin-bottom:15px">登录</div>
    <el-input v-model="account" placeholder="请输入帐号">
        <template slot="prepend">帐号</template>
    </el-input>
    <el-input style="margin-top:20px" v-model="pwd" placeholder="请输入密码">
        <template slot="prepend">密码</template>
    </el-input>
    <el-button  v-on:click="login" style="width:100%;margin-top:20px" type="primary">登录</el-button>
</div>
    `,
        data() {
            return {
                account: '',
                pwd: ''
            }
        },
        methods:{
            async login(){
                try{
                    var data = await api('login',{account:this.account,pwd:this.account});
                    this.$root.setLoginInfo(data);
                }catch(e){
                    this.$root.$handleError(e);
                }
                
            }
        }
    });



    Vue.component('main-frame', {
        template: `<div style="height:100%;width:100%">
            <div class="navigation">
                <img src="img/logo.jpg" class="logo" />
                <span><a href="">退出</a></span>
            </div>
            <side-bar class="sidebar-container"></side-bar>
            <div class="single-app">
                <main-router ref="main"></main-router>
            </div>
        <submit-dialog ref="dialog"></submit-dialog>
    </div>`,
        mounted() {
            this.go("/mod/index", getCurdData("/mod/index"));
        },
        data() {
            return {
                routes: {}, //所有模板(路由)
                url: null, //当前加载的url
                props: null, //当前加载url的数据
            }
        },
        methods: {
            async openDialog(props) {
                this.$refs.dialog.loadDialog(props);
            },
            async go(url, props) {
                this.url = url;
                this.props = props || getCurdData(url);
                this.$refs.main.load(url, this.props);
            },
            async refresh() {
                this.$refs.main.load(this.url, this.props);
            },

            getCurrentData() {
                var content = this.$refs.main.$refs.submit;
                if (!content) return null;
                var data = content.current;
                return data;
            }
        }
    });


    const ErrorView = {
        template: '<p>Error...</p>'
    };

    Vue.component('custom-router', {
        props: ['route'],
        render(h) {
            return h(this.route || Loading);
        }
    });

    Vue.use(VueHtml5Editor, {
        showModuleName: true,
        image: {
            sizeLimit: 512 * 1024,
            compress: true,
            width: 500,
            height: 500,
            quality: 80
        }
    })

    Vue.component('form-html',{
        template:`<el-form-item :label="label" label-width="120px"><vue-html5-editor :content="text" :height="300" :show-module-name="false"
        :zIndex="4000"
        @change="change" ref="editor"></vue-html5-editor></el-form-item>`,
        props:['value','label'],
        data(){
            return {
                text: "",
            }
        },
        mounted(){
            if(this.value){
                this.text = this.value;
            }
        },
        methods:{
            change(data){
                this.text = data
                this.$emit('input', this.text);
            }
        },
    });

    Vue.component('code-editor', {
        props: ['value', 'style', 'language', 'theme'],
        template: `<monaco-editor 
            @mounted="loaded" 
            :code="value" 
            :theme="theme" 
            @change="change" 
            :language="language" 
            class="code"
            :style="style"></monaco-editor>`,
        data() {
            return {
                editor: null,
                text: null,
            }
        },
        beforeUpdate() {
            this.setValue(this.value);
        },
        methods: {
            loaded(editor) {
                this.editor = editor;
                if (this.text) {
                    editor.getModel().setValue(this.text);
                }

            },
            change(editor) {
                this.text = editor.getModel().getValue();
                this.$emit('input', this.text);
            },
            setValue(value) {
                if (value != this.text) {
                    this.text = value;
                    if (this.editor) {
                        this.editor.getModel().setValue(value);
                    }

                }


            }
        }
    });

    

    Vue.component('api-table', {
        template: `<simple-table :loading="loading" :list="list" :columns="columns"></simple-table>`,
        props: ['search', 'api', 'list', 'columns'],
        data() {
            return {
                list: [],
                loading: false,
            };
        },
        mounted() {
            this.refresh();
        },
        methods: {
            async refresh() {
                try {
                    this.loading = true;
                    this.list = await api(this.api, this.search || {});
                } finally {
                    this.loading = false;
                }

            }
        }
    })
  

    Vue.component('simple-table', {
        template: `<el-table 
            size="small"
            :loading="loading" 
            style="width:100%" 
            :data="list" 
            highlight-current-row                
            @current-change="handleSelect">
           <slot>
            <template v-for="(val,index) in columns">
                <el-table-column sortable :prop="val[0]" :label="val[1]">
                </el-table-column>
            </template>
           </slot>
           <el-table-column
           v-if="actions"
           width="100"
           fixed="right"
            label="操作">
            <template slot-scope="scope">
                <el-button-group>
                    <el-button type="info" size="mini" icon="el-icon-edit" />
                    <el-button type="danger" size="mini" icon="el-icon-delete" />
                </el-button-group>
            </template>
           </el-table-column>

        </el-table>`,
        props: ['loading', 'list', 'columns','actions'],
        methods: {
            handleSelect(row) {
                this.$emit('change', row);
            }
        }
    });

    Vue.component('simple-pagination', {
        template: `<div class="pannel" >
        <el-pagination 
            style="float:right" 
            @size-change="handle" 
            @current-change="handle" 
            :current-page="search._page" 
            :page-sizes="[30, 50, 100,200]"
            :page-size="search._pageSize" 
            layout="total, sizes, prev, pager, next, jumper" 
            :total="total">
        </el-pagination>
    </div>`,
        props: ["search","total"],
        methods: {
            handle() {
                this.$emit('refresh');
            }
        }
    });

    Vue.component('simple-list', {
        template: `<div class="flex column" style="overflow:auto">
        <simple-table 
        style="width:100%;" 
        :list="list" 
        :loading="loading" 
        @change="$emit('change',$event)"   
        :columns="columns" 
        />
    <simple-pagination 
        @refresh="$emit('refresh',$event)"  
        :search="search" :total="total" />
        </div>`,
        props: ['loading', 'list', 'columns', "search", "total"],

    });


    Vue.component('form-tree', {
        props: ['api', 'search', 'value'],
        template: `<el-tree
                    :data="data"
                    node-key="id"
                    :default-checked-keys="checkedKeys"
                    style="width:100%" 
                    show-checkbox
                    @check-change="handleSelect"
                    :expand-on-click-node="false"
                    default-expand-all>
                </el-tree>
                `,
        mounted() {
            if(this.value){
                var arr = this.value.split(',');
                for(var i in arr){
                    this.selected[ arr[i] ] = true;
                }
            }
            this.refresh();
        },
        data() {
            return {
                data: [],
                loading: false,
                selected: {},
                timer: null,
                checkedKeys:[],
            }
        },
        methods: {
            handleSelect(data, selected, subselected) {
                //选中的id取出来
                if (selected) {
                    this.selected[data.id] = true;
                } else {
                    delete this.selected[data.id];
                }

                if (this.timer) {
                    return;
                }
                this.timer = setTimeout(() => {
                    var str = "";
                    var first = true;
                    for (var k in this.selected) {
                        if (first) {
                            first = false;
                        } else {
                            str += ",";
                        }
                        str += k;
                    }
                    this.timer = null;
                    this.$emit('input', str);
                }, 1);

            },
            async refresh() {
                this.loading = true;
                try {
                    this.data = await api(this.api, this.search);
                    if(this.value){
                        //计算checked Keys
                        var args = this.value.split(',');
                        var m = {};
                        args.map( a=>m[a]=true );
                        var result = [];
                        var list = tree2list(this.data);
                        list.map( (d)=>{
                            if(!d.children){
                                if(d.id in m){
                                    result.push(d.id);
                                }
                            }
                        } );

                        this.checkedKeys =result;
                    }
                } finally {
                    self.loading = false;
                }
            }
        }
    });

    function tree2list(src, level) {
        var arr = [];
        for (var i = 0; i < src.length; ++i) {
            var data = src[i];
            arr.push(data);
            data.level = level;
            if (data.children) {
                arr = arr.concat(tree2list(data.children, level + 1));
            }
        }
        return arr;
    }

    /**
     * 树形选择器
     */
    Vue.component('tree-select', {
        props: ['search', 'label', 'placeholder', 'api', 'value'],
        template: `<el-form-item :label="label" label-width="120px">
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
        computed: {
            tree: function () {
                var data = this.data;
                return tree2list(data, 0);
            }
        },
        mounted() {
            this.refresh();
        },

        data() {
            return {
                data: [],
                loading: false,
                search: {},
            };
        },
        methods: {
            change(event) {
                this.$emit('input', event);
            },
            async refresh() {
                this.loading = true;
                try {
                    this.data = await api(this.api, this.search);
                } finally {
                    self.loading = false;
                }
            },
            getSpace(c) {
                var str = "";
                for (var i = 0; i < c; ++i) {
                    str += '　';
                }
                return str;
            },
            
        }
    });

})();



(function(){

    new Vue({
        data: function () {
            return {
                login:false,
                routes: {}, //所有模板(路由)
            }
        },
        mounted(){
            if(isLogin()){
                this.login = true;
            }
        },
        methods: {
            openDialog(props) {
                return this.$refs.main.openDialog( props);
            },
            go(url, props) {
                return this.$refs.main.go(url,props);
            },
            refresh(){
                return this.$refs.main.refresh();
            },
            getCurrentData(){       //当前页面被选中的数据
                return this.$refs.main.getCurrentData();
            },
            setLoginInfo(data){
                this.login = true;
                setToken(data);
            },
            $handleError(e){
                if(e.code=='server'){
                    this.$message.error(`服务器错误:${JSON.stringify(e.error)}`);
                }else if(e.code=='message') {
                    this.$message.error(e.error);
                }else if(e.code=='auth'){
                   this.login = false;
                   localStorage.setItem('token',undefined);
                }else if(e.code=='http'){
                    this.$message.error("网络错误");
                }else{
                    this.$message.error(`未知错误:${JSON.stringify(e.error)}`);
                }
            }
        }
    }).$mount('#app');

})();