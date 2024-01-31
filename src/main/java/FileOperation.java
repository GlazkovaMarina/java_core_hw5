import exseptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileOperation {
    private final Path sourcePath;
    private final Path targetPath;

    public Path getSourcePath(){
        return sourcePath;
    }
    public Path getTargetPath(){
        return targetPath;
    }
    public FileOperation(String sourcePath, String targetPath)
            throws SourceInvalidPathException, TargetInvalidPathException {
        try {
            this.sourcePath = Paths.get(sourcePath);
        } catch (InvalidPathException e) {
            throw new SourceInvalidPathException(sourcePath, e.getReason());
        }
        try {
            this.targetPath = Paths.get(targetPath);
        } catch (InvalidPathException e) {
            throw new TargetInvalidPathException(targetPath, e.getReason());
        }
    }


    public List<FileBackupResult> backup()
            throws SourceNotFoundException,
            SourceNoAccessException,
            TargetUnableCreateException,
            TargetNoAccessException {
        if (!Files.isDirectory(sourcePath)) {
            throw new SourceNotFoundException(sourcePath.toString());
        }
        if (!Files.isReadable(sourcePath)) {
            throw new SourceNoAccessException(sourcePath.toString());
        }
        if (!Files.isDirectory(targetPath)) {
            try {
                Files.createDirectory(targetPath);
            } catch (Exception e) {
                throw new TargetUnableCreateException(targetPath.toString(), e);
            }
            if (!Files.isDirectory(targetPath)) {
                throw new TargetUnableCreateException(targetPath.toString());
            }
        }
        if (!Files.isWritable(targetPath)) {
            throw new TargetNoAccessException(targetPath.toString());
        }
        try (Stream<Path> filePathStream = Files.list(sourcePath)) {
            return filePathStream.filter(f -> !Files.isDirectory(f))
                    .map(f -> backupFile(f)).toList();
        } catch (Exception e) {
            throw new SourceNoAccessException(sourcePath.toString());
        }
    }

    private FileBackupResult backupFile(Path filePath){
        String shortFilePathStr = sourcePath.relativize(filePath).toString();
        if (!Files.isRegularFile(filePath)) {
            return new FileBackupResult(shortFilePathStr, CopyStatus.SKIPPED, null);
        }
        if (!Files.isReadable(filePath)) {
            return new FileBackupResult(
                    shortFilePathStr,
                    CopyStatus.ERROR,
                    "Недостаточно прав для чтения файла.");
        }
        String fileName = filePath.getFileName().toString();

            Path destFilePath = Paths.get(targetPath.toString(), fileName);
            if (Files.isDirectory(destFilePath)) {
                return new FileBackupResult(
                        shortFilePathStr,
                        CopyStatus.ERROR,
                        "В месте назначения существует директория с тем же именем.");
            }
            return null;

    }

}
