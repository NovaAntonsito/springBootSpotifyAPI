package com.springbackend.training.Servicios.Response;

public record TrackResponse(Integer trackNro, String trackName, String previewURL, String artistName, String lyrics) {
}
