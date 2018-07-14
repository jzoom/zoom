

组件定义和用法
```
 <tree-select label="父级模块" placeholder="请选择" api="/mod/list" v-model="form.p_id"></tree-select>

 <add-button  url="\${mod}" title="增加模块" label="增加" confirm="是否要增加"></add-button>
```

## 表单页面

```
 <el-form :model="form" ref="submit" :rules="rules">
    <form-input label="模块名称" v-model="form.title" />
    <form-switch label="模块名称" v-model="form.title" />
    <form-select label="模块名称" api="" data="" v-model="form.title" />
    <code-editor language="java" v-model="form.code" />
    <md-editor label="代码" v-model="form.code" />
    <h5-editor label="代码" v-model="form.code" />
    <img-uploader label="代码" v-model="form.code" />
    <img-wall label="代码" v-model="form.code" />
    <file-uploader label="代码" v-model="form.code" />
 </el-form>

```
表格页面最简单:
```
<div class="flex column container">
    <curd-pane module="/page" 
        :fullscreen="true" 
        comment="页面" 
        :current="current"></curd-pane>
    
    <simple-table 
        :list="list" 
        :loading="loading" 
        @change="handleSelect"
        :columns="[['id','编号'],['url','路径']]" 
        />
    <simple-pagination :search="search" :total="total" @refresh="refresh" />
    
</div>
```

表格页面(基本表格+分页）
```
   <simple-table 
        :list="list" 
        :loading="loading" 
        @change="handleSelect"
        :columns="[['id','编号'],['url','路径']]" 
        />
    <simple-pagination :search="search" :total="total" @refresh="refresh" />

```


表格页面最简单:
```
<simple-list  
        :list="list" 
        :loading="loading" 
        :columns="[['id','编号'],['url','路径']]" 
        :search="search" :total="total"
        @refresh="refresh"
        @change="handleSelect"
         />
```

表格页面原始：
```
<div class="flex column container">
    <curd-pane module="/template" :fullscreen="true" comment="模板" :current="current"></curd-pane>
    <div class="pannel" >
        <el-table 
            v-loading="loading" 
            style="width:100%" 
            :data="list" 
            highlight-current-row                
            @current-change="handleSelect">
            <el-table-column prop="id" label="id"></el-table-column>
            <el-table-column prop="name" label="标志"></el-table-column>
            <el-table-column prop="title" label="名称"></el-table-column>
        </el-table>
    </div>

    <div class="pannel" >
        <el-pagination 
            style="float:right" 
            @size-change="refresh" 
            @current-change="refresh" 
            :current-page="search._page" 
            :page-sizes="[20, 50, 100,200]"
            :page-size="search._pageSize" 
            layout="total, sizes, prev, pager, next, jumper" 
            :total="total">
        </el-pagination>
    </div>
</div>
```

//表单


问题：
1、在edit页面如果是定制的如何拿到id?  解决
2、增加调用detail的api接口           解决



