package com.example.bingenow.service;

import com.example.bingenow.model.Media;
import com.example.bingenow.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    public List<Media> getAllFeaturedMedia() {
        return mediaRepository.findByIsFeaturedTrue();
    }

}
