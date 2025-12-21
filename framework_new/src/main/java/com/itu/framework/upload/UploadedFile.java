package com.itu.framework.upload;

public class UploadedFile {
    private final String fieldName;
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public UploadedFile(String fieldName, String fileName, String contentType, byte[] content) {
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
