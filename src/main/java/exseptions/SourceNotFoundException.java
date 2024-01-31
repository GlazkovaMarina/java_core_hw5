package exseptions;

import java.nio.file.FileSystemException;
public class SourceNotFoundException extends FileSystemException{
    public SourceNotFoundException(String file){
        super(file);
    }
}
