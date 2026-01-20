# 下载 Haar Cascades 模型文件

## 方法1：直接下载（推荐）

将以下文件下载到：`app/src/main/res/raw/haarcascade_frontalface_alt.xml`

下载链接：
```
https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml
```

## 方法2：使用 PowerShell 下载

在项目根目录运行：
```powershell
powershell -ExecutionPolicy Bypass -File download_haar_model.ps1
```

## 方法3：手动从 OpenCV 官方仓库获取

1. 访问 OpenCV GitHub 仓库：https://github.com/opencv/opencv
2. 导航到：`data/haarcascades/haarcascade_frontalface_alt.xml`
3. 下载文件并保存到 `app/src/main/res/raw/` 目录

## 文件信息

- 文件名：haarcascade_frontalface_alt.xml
- 大小：约 920KB
- 用途：OpenCV 人脸检测 Haar 级联分类器模型

下载完成后，FaceDetector 将自动从 `res/raw` 加载该模型文件。
