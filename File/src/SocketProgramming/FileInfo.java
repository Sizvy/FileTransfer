package SocketProgramming;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FileInfo {
    String fileID;
    String Frompath;
    String ToPath;
    String fileName;
    FileOutputStream fileOutputStream;

    public FileInfo(String fileID, String Frompath, String ToPath, String fileName, FileOutputStream fileOutputStream) {
        this.fileID = fileID;
        this.Frompath = Frompath;
        this.ToPath = ToPath;
        this.fileName = fileName;
        this.fileOutputStream = fileOutputStream;
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public void setFrompath(String frompath) {
        Frompath = frompath;
    }

    public void setToPath(String toPath) {
        ToPath = toPath;
    }

    public String getFrompath() {
        return Frompath;
    }

    public String getToPath() {
        return ToPath;
    }

    public String getFileID() {
        return fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

