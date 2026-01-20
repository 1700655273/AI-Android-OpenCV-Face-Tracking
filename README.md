# 人脸实时跟踪应用

基于 Android 16（SDK 36）、Kotlin 语言和 OpenCV Haar Cascades 的实时人脸检测与跟踪应用。

## 功能特性

- ✅ 支持前后摄像头切换
- ✅ 实时人脸检测与跟踪（基于 IOU 匹配）
- ✅ 人脸框显示与置信度颜色分级
- ✅ 显示人脸 ID 和置信度
- ✅ 检测分辨率优化（640x480）
- ✅ 位置平滑算法减少闪烁

## 技术栈

- **开发语言**: Kotlin
- **最低SDK版本**: 27
- **目标SDK版本**: 36 (Android 16)
- **编译SDK版本**: 36 (Android 16)
- **计算机视觉库**: OpenCV 4.9.0
- **人脸检测**: OpenCV Haar Cascades (haarcascade_frontalface_alt.xml)
- **跟踪算法**: 基于 IOU 的帧间关联 + 位置平滑

## 项目结构

```
app/src/main/
├── java/com/android/example/myapplication/
│   ├── MainActivity.kt                 # 主Activity
│   └── detector/
│       ├── FaceDetector.kt            # Haar Cascades 人脸检测器
│       └── FaceTrackingManager.kt     # 人脸跟踪管理器
├── res/
│   ├── layout/
│   │   └── activity_main.xml          # 主布局（含摄像头切换按钮）
│   └── raw/
│       └── haarcascade_frontalface_alt.xml  # Haar 模型文件
```

## 快速开始

### 1. 下载 Haar 模型文件

**方式1：使用 PowerShell 脚本**
```powershell
powershell -ExecutionPolicy Bypass -File download_haar_model.ps1
```

**方式2：手动下载**
1. 访问: https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml
2. 保存到: `app/src/main/res/raw/haarcascade_frontalface_alt.xml`

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

或在 Android Studio 中直接运行项目。

## 使用说明

### 权限
应用需要相机权限以使用人脸检测功能。

### 功能按钮

- **右下角相机图标**: 在前后摄像头之间切换

### 显示信息

- **人脸框**: 彩色矩形框标识检测到的人脸
- **标签**: 显示 `ID:X C:Y`，其中 X 为人脸 ID，Y 为置信度（1-20）
- **颜色分级**:
  - 黄色: 置信度低（1-2）
  - 橙色: 置信度中低（3-4）
  - 浅红: 置信度中高（5-6）
  - 深红: 置信度高（7-20）

## 技术实现

### 人脸检测算法

使用 OpenCV Haar Cascades 分类器进行人脸检测：
- 模型: haarcascade_frontalface_alt.xml
- 检测分辨率: 640x480（自动缩放适配屏幕）
- 缩放因子 (scaleFactor): 1.15
- 最小邻居数 (minNeighbors): 4
- 最小人脸尺寸: 24x24 像素

### 人脸跟踪算法

1. **IOU 匹配**: 计算检测框与跟踪框的重叠度，阈值 0.3
2. **运动预测**: 基于速度向量预测下一帧位置
3. **位置平滑**: 使用 0.3 权重进行指数平滑
4. **置信度系统**:
   - 成功匹配: 置信度 +1（最大 20）
   - 丢失跟踪: 置信度 -2（清零时移除）
   - 显示阈值: 置信度 ≥ 1

### 性能优化

- 检测分辨率降低至 640x480，减少计算量
- 每帧检测，结合跟踪保证实时性
- 过滤重叠检测（IOU > 0.5）
- 平滑算法减少闪烁

## 系统要求

- Android 8.0 (API 27) 或更高版本
- 支持摄像头的设备
- 至少 2GB RAM
- 建议使用中高端设备以获得更好的性能

## 常见问题

### 1. OpenCV 加载失败
确保 OpenCV 库正确集成，Haar 模型文件已下载到 `res/raw` 目录。

### 2. 人脸检测不工作
检查相机权限是否已授予，确认 `haarcascade_frontalface_alt.xml` 已放置在正确位置。

### 3. 人脸框闪烁
- 确保在光线良好的环境中使用
- 避免快速移动头部
- 如问题持续，可调整 `smoothingFactor` 参数

### 4. 性能问题
在高分辨率设备上，检测已优化至 640x480。如仍卡顿，可尝试:
- 降低检测频率（修改 `detectEveryNFrames`）
- 降低 `scaleFactor` 值以减少检测层级

## 许可证

本项目仅供学习和研究使用。

## 参考资料

- [OpenCV Documentation](https://docs.opencv.org/)
- [OpenCV Haar Cascades](https://docs.opencv.org/4.x/db/d28/tutorial_cascade_classifier.html)
- [Android Developers](https://developer.android.com/)
