package com.xwintop.xcore.util.javafx;

import com.xwintop.xcore.javafx.helper.DropContentHelper;
import com.xwintop.xcore.util.FileUtil;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.List;

/**
 * 文件选择工具
 *
 * @author xufeng
 */
public class FileChooserUtil {

    public static final File HOME_DIRECTORY = FileSystemView.getFileSystemView().getHomeDirectory();

    //选择多个文件
    public static List<File> chooseFiles() {
        return chooseFiles(null);
    }

    public static List<File> chooseFiles(ExtensionFilter... extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择文件");
        fileChooser.setInitialDirectory(HOME_DIRECTORY);

        if (extensionFilter != null) {
            fileChooser.getExtensionFilters().addAll(extensionFilter);
        }
        return fileChooser.showOpenMultipleDialog(null);
    }

    public static File chooseFile() {
        return chooseFile(null);
    }

    public static File chooseFile(ExtensionFilter... extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择文件");
        fileChooser.setInitialDirectory(HOME_DIRECTORY);

        if (extensionFilter != null) {
            fileChooser.getExtensionFilters().addAll(extensionFilter);
        }

        return fileChooser.showOpenDialog(null);
    }

    ///////////////////////////////////////////////////////////////

    public static File chooseSaveFile(ExtensionFilter... extensionFilter) {
        return chooseSaveFile(null, extensionFilter);
    }

    public static File chooseSaveFile(String fileName) {
        return chooseSaveFile(fileName, null);
    }

    public static File chooseSaveFile(String fileName, ExtensionFilter... extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(HOME_DIRECTORY);

        if (fileName != null) {
            fileChooser.setInitialFileName(fileName);
        }

        if (extensionFilter != null) {
            fileChooser.getExtensionFilters().addAll(extensionFilter);
        }

        return fileChooser.showSaveDialog(null);
    }

    public static File chooseSaveCommonImageFile(String fileName) {
        return chooseSaveFile(fileName,
            new ExtensionFilter("All Images", "*.*"),
            new ExtensionFilter("JPG", "*.jpg"),
            new ExtensionFilter("PNG", "*.png"),
            new ExtensionFilter("GIF", "*.gif"),
            new ExtensionFilter("JPEG", "*.jpeg"),
            new ExtensionFilter("BMP", "*.bmp"));
    }

    public static File chooseSaveImageFile(String fileName) {
        return chooseSaveFile(fileName,
            new ExtensionFilter("All Images", "*.*"),
            new ExtensionFilter("JPG", "*.jpg"),
            new ExtensionFilter("PNG", "*.png"),
            new ExtensionFilter("gif", "*.gif"),
            new ExtensionFilter("jpeg", "*.jpeg"),
            new ExtensionFilter("bmp", "*.bmp"),
            new ExtensionFilter("ICO", "*.ico"),
            new ExtensionFilter("RGBE", "*.rgbe")
        );
    }

    ///////////////////////////////////////////////////////////////

    public static File chooseDirectory() {
        return chooseDirectory(null);
    }

    public static File chooseDirectory(File initialDirectory) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (initialDirectory != null) {
            directoryChooser.setInitialDirectory(initialDirectory);
        }
        return directoryChooser.showDialog(null);
    }

    ///////////////////////////////////////////////////////////////

    public static void setOnDrag(TextField textField, FileType fileType) {
        DropContentHelper.accept(textField,

            dragboard -> dragboard.hasFiles() &&
                dragboard.getFiles().stream().anyMatch(fileType::match),

            (__, dragboard) -> textField.setText(
                dragboard.getFiles().stream()
                    .filter(fileType::match)
                    .map(File::getAbsolutePath)
                    .findFirst().orElse("")
            )
        );
    }

    public static void setOnDragByOpenFile(TextInputControl textField) {
        DropContentHelper.accept(textField,

            dragboard -> dragboard.hasFiles() &&
                dragboard.getFiles().stream().anyMatch(File::isFile),

            (__, dragboard) -> textField.setText(
                dragboard.getFiles().stream()
                    .filter(File::isFile)
                    .map(FileUtil::readText)
                    .findFirst().orElse("")
            )
        );
    }

    public enum FileType {
        FILE, FOLDER;

        public boolean match(File file) {
            return
                (this == FILE && file.isFile()) ||
                    (this == FOLDER && file.isDirectory());
        }
    }
}
