# Attachment 附件管理

Jboot 定位是分布式的开发系统，在项目进行分布式部署的时候，用户在上传文件时，我们需要对文件进行分布式同步也是必须的。

在分布式部署的需求下，我们假设我们把我们的应用部署在 A/B/C 三台服务器上，这三台服务器通过 nginx 或者 SLB 等做负载均衡。

此时，也就意味着当用户每次访问我们的应用的时候，可能访问到了 A 服务器，也有可能访问到 B 服务器 或者 C 服务器。

当我们的应用假设有一个图片上传的功能，用户在上传图片的时候，假设上传到了 A 服务器，但是第二次去访问附件的时候，可能访问到了 B 服务器，但是 B 服务器却不存在 A 用户刚刚上传的图片，Jboot 的 AttachmentContainer 就是为了解决这一系列问题而存在的。

在使用 AttachmentContainer 之前，我们先来了解下以下的几个概念。

- AttachmentContainer : 附件容器，就是专门用来存放图片、读取图片和渲染http请求图片的。
- AttachmentManager : 用来管理 AttachmentContainer 的，一个应用里可以有多个 AttachmentContainer 。比如，阿里云OSS存储的，我们可以来定义一个 AliyunOSSAttachmentContainer； fastDFS 存储我们一样可以来定义一个 FastDFSAttachmentContainer；同时，Attachment 内置了一个默认的 AttachmentContainer，用来存在 ”本地“ 附件的。

在使用 AttachmentContainer 之前，我们需要需要编写自己的一个类，来实现 AttachmentContainer 接口，并添加到 AttachmentManager 里去。

```java
AliyunOssAttachmentContainer aliyunOss = new AliyunOssAttachmentContainer();
AttachmentManager.me().addContainer(aliyunOss);
```

当我们的 Controller 有文件上传的时候，我们需要调用 AttachmentManager 进行保存，AttachmentManager 最终会保持到其所有的容器里去。

例如：

```java
public void upload() {
    if (!isMultipartRequest()) {
        renderError(404);
        return;
    }

    UploadFile uploadFile = getFile();
    if (uploadFile == null) {
        renderJson(Ret.fail().set("message", "请选择要上传的文件"));
        return;
    }

    //通过 AttachmentManager 去保存文件
    String relativePath = AttachmentManager.me().saveFile(file);
    file.delete();  

    renderJson(Ret.ok().set("success", true).set("src", relativePath));
}
```

通过  `AttachmentManager.me().saveFile(file);` 保存文件，AttachmentManager 会保持到所有的容器里。

当需要读取文件的时候，我们也可以通过 AttachmentManger 去读文件。

```java
File attachment = AttachmentManager.me().getFile(relativePath);
```

`AttachmentManager.me().getFile(relativePath);` 读取文件的时候，会优先从 默认 的 ”容器“ 去读取，当默认 ”容器“ 不存在该文件的时候，AttachmentManager 会遍历所有的 Container ，直到读到为此。


以下是 `AttachmentManager.me().getFile()` 代码的实现逻辑：

```java
public File getFile(String relativePath) {

    AttachmentContainer defaultContainer = getDefaultContainer();

    //优先从 默认的 container 去获取
    File file = defaultContainer.getFile(relativePath);
    if (file != null && file.exists()) {
        return file;
    }

    for (Map.Entry<String, AttachmentContainer> entry : containerMap.entrySet()) {
        AttachmentContainer container = entry.getValue();
        try {
            if (container != defaultContainer) {
                file = container.getFile(relativePath);
                if (file != null && file.exists()) {
                    return file;
                }
            }
        } catch (Exception ex) {
            LOG.error("get file error in container :" + container, ex);
        }
    }
    return null;
}
```

以下是阿里云 Oss 的代码实现逻辑，可供参考：

```java
public class AliyunOssAttachmenetContainer implements AttachmentContainer {

    private String basePath = PathKit.getWebRootPath();
    private AliyunOssAttachmentConfig config = JbootConfigManager.me().get(AliyunOssAttachmentConfig.class);
    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3, "aliyun-oss-upload");


    public AliyunOssAttachmenetContainer() {
    }

    @Override
    public String saveFile(File file) {
        if (!config.isEnable()) {
            return null;
        }
        String relativePath = getRelativePath(file);
        fixedThreadPool.execute(() -> {
            upload(relativePath, file);
        });
        return relativePath;
    }


    @Override
    public boolean deleteFile(String relativePath) {
        if (!config.isEnable()) {
            return false;
        }
        relativePath = removeFirstFileSeparator(relativePath);
        OSSClient ossClient = createOSSClient();
        try {
            ossClient.deleteObject(config.getBucketname(), relativePath);
        } catch (Exception ex) {
            LogKit.error(ex.toString(), ex);
        } finally {
            ossClient.shutdown();
        }
        return true;
    }


    @Override
    public File getFile(String relativePath) {
        if (!config.isEnable()){
            return null;
        }
        File localFile = new File(basePath, relativePath);
        if (localFile.exists()) {
            return localFile;
        }
        if (download(relativePath, localFile)) {
            return localFile;
        }
        return null;
    }


    @Override
    public String getRelativePath(File file) {
        return FileUtil.removePrefix(file.getAbsolutePath(), basePath);
    }


    /**
     * 同步本地文件到阿里云OSS
     *
     * @param path
     * @param file
     * @return
     */
    public boolean upload(String path, File file) {
        if (StrUtil.isBlank(path)) {
            return false;
        }

        path = removeFirstFileSeparator(path);
        path = path.replace('\\', '/');

        String ossBucketName = config.getBucketname();
        OSSClient ossClient = createOSSClient();

        try {
            ossClient.putObject(ossBucketName, path, file);
            boolean success = ossClient.doesObjectExist(ossBucketName, path);
            if (!success) {
                LogKit.error("aliyun oss upload error! path:" + path + "\nfile:" + file);
            }
            return success;

        } catch (Throwable e) {
            LogKit.error("aliyun oss upload error!!!", e);
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 如果文件以 / 或者 \ 开头，去除 / 或 \ 符号
     */
    private static String removeFirstFileSeparator(String path) {
        while (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 下载 阿里云 OSS 到本地
     *
     * @param path
     * @param toFile
     * @return
     */
    public boolean download(String path, File toFile) {
        if (StrUtil.isBlank(path)) {
            return false;
        }
        path = removeFirstFileSeparator(path);
        OSSClient ossClient = createOSSClient();
        try {
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }

            if (!toFile.exists()) {
                toFile.createNewFile();
            }
            ossClient.getObject(new GetObjectRequest(config.getBucketname(), path), toFile);
            return true;
        } catch (Throwable e) {
            LogKit.error("aliyun oss download error!!!  path:" + path + "   toFile:" + toFile, e);
            if (toFile.exists()) {
                toFile.delete();
            }
            return false;
        } finally {
            ossClient.shutdown();
        }
    }


    private OSSClient createOSSClient() {
        String endpoint = config.getEndpoint();
        String accessId = config.getAccessKeyId();
        String accessKey = config.getAccessKeySecret();
        return new OSSClient(endpoint, new DefaultCredentialProvider(accessId, accessKey), null);
    }


}
```

```java
@ConfigModel(prefix = "aliyunoss")
public class AliyunOssAttachmentConfig {

    private boolean enable = false;
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketname;
    private boolean delSync;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketname() {
        return bucketname;
    }

    public void setBucketname(String bucketname) {
        this.bucketname = bucketname;
    }

    public boolean isDelSync() {
        return delSync;
    }

    public void setDelSync(boolean delSync) {
        this.delSync = delSync;
    }
}
```