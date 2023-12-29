package com.springbackend.training.Servicios.Interfaces;

import com.springbackend.training.Controladores.Response.TrackResponse;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.Base.IServicioBase;
import org.apache.hc.core5.http.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import java.io.IOException;
import java.util.List;

public interface IUserService extends IServicioBase<UserDB, Long> {

     SpotifyApi getProfile();

     Page<TrackResponse> savePlaylists(SpotifyApi spotifyApiArmada, Pageable pageable) throws IOException, ParseException, SpotifyWebApiException;

     Page<TrackResponse> getMyPlaylist (SpotifyApi spotifyApiArmada, Pageable pageable) throws IOException, ParseException, SpotifyWebApiException;
}
