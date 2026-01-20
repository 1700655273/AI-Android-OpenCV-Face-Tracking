# 人脸实时跟踪应用

基于Android 16（SDK 36）、Kotlin语言和OpenCV DNN模型的实时人脸跟踪应用。

## 功能特性

- ✅ 支持前后摄像头切换
- ✅ 实时人脸检测与跟踪
- ✅ 显示检测性能指标（FPS、检测人数）
- ✅ 人脸置信度显示
- ✅ 动态权限申请
- ✅ 专业的UI设计，半透明悬浮界面

## 技术栈

- **开发语言**: Kotlin
- **最低SDK版本**: 27
- **目标SDK版本**: 36 (Android 16)
- **编译SDK版本**: 36 (Android 16)
- **计算机视觉库**: OpenCV 4.10.0
- **UI绑定**: ViewBinding
- **人脸检测**: OpenCV DNN模块 (ResNet-10 SSD)

## 项目结构

```
app/src/main/
├── java/com/android/example/myapplication/
│   ├── MainActivity.kt                 # 主Activity
│   ├── detector/
│   │   ├── FaceDetector.kt            # 人脸检测器接口
│   │   ├── FaceTrackingManager.kt      # 人脸跟踪管理器
│   │   └── OpenCVDNNDetector.kt       # OpenCV DNN实现
│   ├── camera/
│   │   └── CameraPreviewManager.kt    # 相机预览管理器
│   ├── ui/
│   │   └── FaceOverlayView.kt         # 人脸绘制View
│   └── utils/
│       └── PermissionHelper.kt        # 权限申请辅助类
├── res/
│   ├── layout/
│   │   └── activity_main.xml          # 主布局
│   └── values/
│       └── strings.xml                # 字符串资源
└── assets/                            # 模型文件目录
    ├── deploy.prototxt                # 模型配置文件
    └── res10_300x300_ssd_iter_140000.caffemodel  # 预训练模型
```

## 快速开始

### 1. 下载模型文件

**Windows用户**:
```powershell
.\download_models.ps1
```

**Linux/macOS用户**:
```bash
chmod +x download_models.sh
./download_models.sh
```

**手动下载**:
1. 从 [deploy.prototxt](https://raw.githubusercontent.com/opencv/opencv_extra/master/testdata/dnn/face_detector/deploy.prototxt) 下载配置文件
2. 从 [res10_300x300_ssd_iter_140000.caffemodel](https://raw.githubusercontent.com/opencv/opencv_3rdparty/dnn_samples_face_detector_20170830/res10_300x300_ssd_iter_140000.caffemodel) 下载模型文件
3. 将文件放到 `app/src/main/assets/` 目录

### 2. 构建项目

```bash
# 使用Gradle构建
./gradlew clean build
```

### 3. 运行应用

```bash
# 连接Android设备并运行
./gradlew installDebug
```

或在Android Studio中直接运行项目。

## 使用说明

### 权限
应用首次启动时会请求相机权限，请授予权限以使用人脸跟踪功能。

### 功能按钮

- **播放/暂停按钮**: 开始或停止人脸检测
- **切换摄像头按钮**: 在前后摄像头之间切换
- **截图按钮**: 截取当前画面（待实现）
- **设置按钮**: 打开设置页面（待实现）

### 性能指标

应用会实时显示以下性能指标：
- **FPS**: 每秒帧数
- **检测数**: 当前检测到的人脸数量
- **相机状态**: 相机的运行状态

## 技术实现

### 人脸检测算法

使用OpenCV DNN模块加载预训练的ResNet-10 SSD模型进行人脸检测：
- 输入尺寸: 300x300
- 置信度阈值: 0.7
- 后端: OpenCV
- 目标设备: CPU

### 性能优化

- 使用单线程执行器处理帧数据
- 在独立线程中进行模型推理
- 限制检测区域提高速度
- 优化UI绘制流程

## 系统要求

- Android 8.0 (API 27) 或更高版本
- 支持摄像头的设备
- 至少2GB RAM
- 建议使用中高端设备以获得更好的性能

## 常见问题

### 1. OpenCV加载失败
确保OpenCV库正确集成，模型文件已下载到assets目录。

### 2. 人脸检测不工作
检查相机权限是否已授予，确认模型文件已正确下载。

### 3. 性能问题
降低检测频率或使用性能更好的设备。

## 许可证

本项目仅供学习和研究使用。

## 参考资料

- [OpenCV Documentation](https://docs.opencv.org/)
- [OpenCV DNN Face Detection](https://github.com/opencv/opencv/tree/master/samples/dnn/face_detector)
- [Android Developers](https://developer.android.com/)
