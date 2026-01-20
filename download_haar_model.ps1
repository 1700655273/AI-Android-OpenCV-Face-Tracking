# 下载 Haar Cascades 模型文件
# 运行此脚本下载 haarcascade_frontalface_alt.xml 到正确的位置

$outputDir = "app\src\main\res\raw"
$targetFile = "$outputDir\haarcascade_frontalface_alt.xml"

# 创建目录（如果不存在）
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
    Write-Host "Created directory: $outputDir"
}

# 下载模型文件
Write-Host "Downloading haarcascade_frontalface_alt.xml..."
try {
    Invoke-WebRequest -Uri "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml" -OutFile $targetFile -TimeoutSec 60
    Write-Host "Download completed: $targetFile"
    Write-Host "File size: $((Get-Item $targetFile).Length) bytes"
} catch {
    Write-Host "Download failed: $_" -ForegroundColor Red
    Write-Host "Please download manually from:"
    Write-Host "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml"
    Write-Host "And save to: $targetFile"
    exit 1
}
