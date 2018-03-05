### Retrofit下载
```java

RetrofitDownLoader.downloadUrl(this, "http://47.94.132.220/build/TopCleaner/TopCleaner-release-v1.0.4-5.apk")
                .setFileName("TopCleaner-release-v1.0.4-5.apk")
                .setFileSaveLocation("想要存放在哪个文件目录下,默认在app包下的cache目录")
                .request(new DcDownLoaderCallback() {
                    @Override
                    public void onProgress(DownLoadProgress progressData) {
                        // progressData.getIntPercent() 返回int型整数的进度
                        // progressData.getFormatPercent() 返回格式化String的进度,例如：66.66%
                        // progressData.getFormatDownloadSize() 返回格式化的当前下载文件大小,例如：66K
                        // progressData.getFormatTotalSize() 返回格式化的文件总大小,例如66M
                    }
                    
                    @Override
                    public void onSuccess(DownLoadProgress progressData) {
                        //progressData.getDownloadFile() 返回下载文件的File
                    }

                    @Override
                    public void onFail(String errMsg) {

                    }
                });

```
