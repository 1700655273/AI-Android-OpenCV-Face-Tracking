#!/bin/bash

# OpenCV Face Detection Model Download Script
# This script downloads the required model files for face detection

echo "开始下载OpenCV人脸检测模型文件..."

ASSETS_DIR="app/src/main/assets"
mkdir -p "$ASSETS_DIR"

# Download model configuration file
echo "正在下载 deploy.prototxt..."
curl -L -o "$ASSETS_DIR/deploy.prototxt" \
  https://raw.githubusercontent.com/opencv/opencv_extra/master/testdata/dnn/face_detector/deploy.prototxt

if [ $? -eq 0 ]; then
    echo "✓ deploy.prototxt 下载成功"
else
    echo "✗ deploy.prototxt 下载失败"
    exit 1
fi

# Download pre-trained model weights
echo "正在下载 res10_300x300_ssd_iter_140000.caffemodel..."
curl -L -o "$ASSETS_DIR/res10_300x300_ssd_iter_140000.caffemodel" \
  https://raw.githubusercontent.com/opencv/opencv_3rdparty/dnn_samples_face_detector_20170830/res10_300x300_ssd_iter_140000.caffemodel

if [ $? -eq 0 ]; then
    echo "✓ res10_300x300_ssd_iter_140000.caffemodel 下载成功"
else
    echo "✗ res10_300x300_ssd_iter_140000.caffemodel 下载失败"
    exit 1
fi

echo ""
echo "模型文件下载完成！"
echo "文件位置: $ASSETS_DIR"
echo "现在可以构建并运行应用了。"
