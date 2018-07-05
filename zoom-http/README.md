# http


实现基本的aop

```
Client client = Http.newClient()
        .setReadTimeout(10000).setConnectTimeout(10000)
        .addDefaultHeader( Http.Content_Type , Http.APPLICATION_JSON)
				.setBaseUrl(baseUrl).setCallback(new HttpCallback(){
                    @Override
                    public void preHandle(HttpURLConnection connection) throws IOException {
                        //添加公共头
                    }

                    @Override
                    public Response afterHandle(HttpURLConnection connection, Response response) throws IOException {
                        //这里可以修改返回Response
                        return response;
                    }
                });


	
```