package com.example.bingenow.controller;

import com.example.bingenow.model.Media;
import com.example.bingenow.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private final MediaRepository mediaRepository;


    public MediaController(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMedia(@RequestBody Media media) {
        System.out.println("Incoming Media: " + media);
        if (!isValidMedia(media)) {
            return new ResponseEntity<>("Invalid or incomplete media data", HttpStatus.BAD_REQUEST);
        }

            Media savedMedia = mediaRepository.save(media);
            return new ResponseEntity<>(savedMedia, HttpStatus.CREATED);
    
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Media>> getAllMovies() {
        List<Media> movies = mediaRepository.findByIsMovie(true);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/tvshows")
    public ResponseEntity<List<Media>> getAllTvShows() {
        List<Media> tvShows = mediaRepository.findByIsMovie(false);
        return new ResponseEntity<>(tvShows, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Media>> searchByName(@RequestParam String name) {
        List<Media> results = mediaRepository.findByNameContainingIgnoreCase(name);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Media>> getFeaturedMedia(@RequestParam(required = false) String type) {
        List<Media> featuredMedia;

        if (type != null) {
            boolean isMovie = "movies".equalsIgnoreCase(type);
            boolean isTvShow = "tvshows".equalsIgnoreCase(type);

            if (isMovie || isTvShow) {
                featuredMedia = mediaRepository.findByIsFeaturedAndIsMovie(true, isMovie);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            featuredMedia = mediaRepository.findByIsFeaturedTrue();
        }

        return new ResponseEntity<>(featuredMedia, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMediaById(@PathVariable String id)
    {
        
        if (!isValidId(id)) {
            return ResponseEntity.badRequest().body("Invalid ID format");
        }
        Optional<Media> media = mediaRepository.findById(id);
        if (media.isPresent()) {
            return ResponseEntity.ok(media.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedia(@PathVariable String id, @RequestBody Media updatedMedia) {
        if (!isValidId(id)) {
            return new ResponseEntity<>("Invalid movie ID format", HttpStatus.BAD_REQUEST);
        }

        Optional<Media> existingMediaOptional = mediaRepository.findById(id);
        if (existingMediaOptional.isEmpty()) {
            return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
        }

        Media existingMedia = existingMediaOptional.get();

        if (updatedMedia == null) {
            return new ResponseEntity<>("Incoming data is missing", HttpStatus.BAD_REQUEST);
        }

        if (updatedMedia.getName() != null) existingMedia.setName(updatedMedia.getName());
        if (updatedMedia.getPrice() != null) existingMedia.setPrice(updatedMedia.getPrice());
        if (updatedMedia.getSynopsis() != null) existingMedia.setSynopsis(updatedMedia.getSynopsis());
        if (updatedMedia.getIsMovie() != null) existingMedia.setIsMovie(updatedMedia.getIsMovie());
        if (updatedMedia.getSmallPosterPath() != null) existingMedia.setSmallPosterPath(updatedMedia.getSmallPosterPath());
        if (updatedMedia.getLargePosterPath() != null) existingMedia.setLargePosterPath(updatedMedia.getLargePosterPath());
        if (updatedMedia.getRentPrice() != null) existingMedia.setRentPrice(updatedMedia.getRentPrice());
        if (updatedMedia.getPurchasePrice() != null) existingMedia.setPurchasePrice(updatedMedia.getPurchasePrice());
        if (updatedMedia.getIsFeatured() != null) existingMedia.setIsFeatured(updatedMedia.getIsFeatured());

        Media savedMedia = mediaRepository.save(existingMedia);

        return new ResponseEntity<>(savedMedia, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedia(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return new ResponseEntity<>("Invalid ID provided", HttpStatus.BAD_REQUEST);
        }

        Optional<Media> optionalMedia = mediaRepository.findById(id);
        if (optionalMedia.isEmpty()) {
            return new ResponseEntity<>("Movie or TV show not found", HttpStatus.NOT_FOUND);
        }

        mediaRepository.deleteById(id);
        return new ResponseEntity<>("Movie or TV show deleted successfully", HttpStatus.OK);
    }

    private boolean isValidMedia(Media media) {
        return media != null && media.getName() != null && !media.getName().isEmpty() &&
                media.getPrice() != null && media.getSynopsis() != null && media.getIsMovie() != null &&
                media.getSmallPosterPath() != null && media.getLargePosterPath() != null &&
                media.getRentPrice() != null && media.getPurchasePrice() != null && media.getPrice() != null &&
                media.getIsFeatured() != null;
    }

    private boolean isValidId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        return true;
    }
}
