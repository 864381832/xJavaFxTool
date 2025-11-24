package com.xwintop.xJavaFxTool.services.littleTools;

import java.text.DecimalFormat;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ImageInfo {

    ////////////////////////////////////////////////////////////// 原始属性

    private final StringProperty imageFilePath = new SimpleStringProperty();

    private final SimpleObjectProperty<Integer> width = new SimpleObjectProperty<>(0);

    private final SimpleObjectProperty<Integer> height = new SimpleObjectProperty<>(0);

    private final SimpleObjectProperty<Integer> originalSize = new SimpleObjectProperty<>(0);

    private final SimpleObjectProperty<Integer> compressedSize = new SimpleObjectProperty<>(0);

    ////////////////////////////////////////////////////////////// 衍生属性（只读）

    private final ObservableValue<String> imageFileName = new StringBinding() {
        {
            super.bind(imageFilePath);
        }

        @Override
        protected String computeValue() {
            return FilenameUtils.getName(imageFilePath.get());
        }
    };

    private final ObservableValue<String> imageSize = new StringBinding() {
        {
            super.bind(width, height);
        }

        @Override
        protected String computeValue() {
            return String.format("%dx%d", width.getValue(), height.getValue());
        }
    };

    private final ObservableValue<String> compressionRate = new ObjectBinding<String>() {
        {
            super.bind(originalSize, compressedSize);
        }

        @Override
        protected String computeValue() {
            return new DecimalFormat("#.00%").format((double) compressedSize.getValue() / originalSize.getValue());
        }
    };

    private final ObservableValue<String> originalSizeText = new StringBinding() {
        {
            super.bind(originalSize);
        }

        @Override
        protected String computeValue() {
            return FileUtils.byteCountToDisplaySize(originalSize.get());
        }
    };

    private final ObservableValue<String> compressedSizeText = new StringBinding() {
        {
            super.bind(compressedSize);
        }

        @Override
        protected String computeValue() {
            return FileUtils.byteCountToDisplaySize(compressedSize.get());
        }
    };

    //////////////////////////////////////////////////////////////


    public ObservableValue<String> compressedSizeTextProperty() {
        return compressedSizeText;
    }

    public ObservableValue<String> originalSizeTextProperty() {
        return originalSizeText;
    }

    public ObservableValue<String> compressionRateProperty() {
        return compressionRate;
    }
    public ObservableValue<String> imageSizeProperty() {
        return imageSize;
    }

    public ObservableValue<String> imageFileNameProperty() {
        return imageFileName;
    }

    public String getImageFilePath() {
        return imageFilePath.get();
    }

    public StringProperty imageFilePathProperty() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath.set(imageFilePath);
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public Integer getWidth() {
        return width.getValue();
    }

    public ObservableValue<Integer> widthProperty() {
        return width;
    }

    public void setHeight(int height) {
        this.height.set(height);
    }

    public Integer getHeight() {
        return height.getValue();
    }

    public ObservableValue<Integer> heightProperty() {
        return height;
    }

    public void setOriginalSize(int originalSize) {
        this.originalSize.set(originalSize);
    }

    public void setCompressedSize(int compressedSize) {
        this.compressedSize.set(compressedSize);
    }

    public Integer getOriginalSize() {
        return originalSize.getValue();
    }

    public ObservableValue<Integer> originalSizeProperty() {
        return originalSize;
    }

    public Integer getCompressedSize() {
        return compressedSize.getValue();
    }

    public ObservableValue<Integer> compressedSizeProperty() {
        return compressedSize;
    }
}
