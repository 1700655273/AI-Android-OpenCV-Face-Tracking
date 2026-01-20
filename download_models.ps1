# OpenCV Face Detection Model Download Script for Windows PowerShell
# This script downloads the required model files for face detection

Write-Host "开始下载OpenCV人脸检测模型文件..." -ForegroundColor Green

$assetsDir = "app\src\main\assets"
if (-not (Test-Path $assetsDir)) {
    New-Item -ItemType Directory -Path $assetsDir -Force
}

# Download model configuration file
Write-Host "正在下载 deploy.prototxt..." -ForegroundColor Yellow
try {
    $prototxtUrl = "https://raw.githubusercontent.com/opencv/opencv_zoo/master/models/face_detection_resnet_ssd/inputs/deploy.prototxt"
    Invoke-WebRequest -Uri $prototxtUrl `
        -OutFile "$assetsDir\deploy.prototxt" `
        -UseBasicParsing
    Write-Host "✓ deploy.prototxt 下载成功" -ForegroundColor Green
} catch {
    Write-Host "✗ deploy.prototxt 下载失败: $_" -ForegroundColor Red
    exit 1
}

# Download pre-trained model weights
Write-Host "正在下载 res10_300x300_ssd_iter_140000.caffemodel..." -ForegroundColor Yellow
try {
    $modelUrl = "https://raw.githubusercontent.com/opencv/opencv_zoo/master/models/face_detection_resnet_ssd/inputs/res10_300x300_ssd_iter_140000_fp16.caffemodel"
    Invoke-WebRequest -Uri $modelUrl `
        -OutFile "$assetsDir\res10_300x300_ssd_iter_140000.caffemodel" `
        -UseBasicParsing
    Write-Host "✓ res10_300x300_ssd_iter_140000.caffemodel 下载成功" -ForegroundColor Green
} catch {
    Write-Host "✗ res10_300x300_ssd_iter_140000.caffemodel 下载失败: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "模型文件下载完成！" -ForegroundColor Green
Write-Host "文件位置: $assetsDir"
Write-Host "现在可以构建并运行应用了。"
