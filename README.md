#zoom

Zoom is a java frame support AOP/IOC/WEB,high performance,easy to expand,easy to lean, very fast development.

#Motivation

This's project has been developed in 2013-2014, back then I was developing my first online application, which contains Anroid,Ios,Admin service,Api service. I had to do all the work by myself at that time. So I needed a effective way to develop my app very very fast.

# Core features 
## Caster
It is simple to cast variables into another type.
```
Caster.to( "{\"hello\":\"world\"}", Map.class) =>  {hello=world}
```
It is very useful when you take a value like BLOB,CLOB,TIMESTAMP,DATETIME from database and convert it into simple java types like byte[],String,Date,long.

## Aop(Aspect-Oriented Programming)
Easy to enhance a class.
```
AopFactory factory = new SimpleAopFactory();
        factory.addFilter(new MethodInterceptor() {
            
            @Override
            public void intercept(MethodInvoker invoker) throws Throwable {
                log.info("before invoke method:"+invoker.getMethod());
                try {
                    invoker.invoke();
                }catch (Throwable e) {
                    log.error("When error invoke method:"+invoker.getMethod());
                }finally {
                    log.error("When complete invoke method:"+invoker.getMethod());
                }
                
            }
        }, "*",  0 );       //The MethodInterceptor will apply to every public method enhanced by the AopFactory
        Class<?> enhancedAClass = factory.enhance(A.class);
```

## Ioc(Dependency Injection)
Easy to manage inject.
```
public static class A {
        public B b;
        
        @Inject
        public C c;
        
        @Inject(value="d")
        private D d;
        
        public A(B b) {
            this.b = b;
        }

        public D getD() {
            return d;
        }

        public void setD(D d) {
            this.d = d;
        }
        
        private E e;
        
        @Inject
        public void setE(E e) {
            this.e = e;
        }
        
        boolean inited;
        
        @Inject
        public void init(E e,B b, @Inject(value="d") D d) {
            inited = true;
        }
        
        @Log
        public void test() {
            
        }
        
        @Override
        public String toString() {
            return "A";
        }
    }
IocContainer ioc = new SimpleIocContainer(new ClassEnhanceAdapter());
A a = ioc.get(A.class);
```

## Dao

Currently support mysql and oracle.

Support none entity mode,easy to use
```
Record record = Db.table("my_table").where("id",1).fetch();
Db.table("my_table").set("id",2).set("name","table").inert();
Db.table("my_table").where("id",2).set("name","table2").update();
```

Entity mode
```
Db.entity(A.class).where('id',1).fetch();
```




