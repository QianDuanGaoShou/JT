package com.guitar.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {

    Map<String, Object> uploadVideo(MultipartFile file, String type);

    Map<String, Object> uploadImage(MultipartFile file, String type);
}
