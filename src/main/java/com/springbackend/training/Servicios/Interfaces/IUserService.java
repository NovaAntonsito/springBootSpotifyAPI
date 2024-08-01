package com.springbackend.training.Servicios.Interfaces;

import com.fasterxml.jackson.core.JsonParser;
import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.Base.IServicioBase;
import org.apache.hc.core5.http.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IUserService extends IServicioBase<UserDB, Long> {

     SpotifyApi getProfile();

     List<Map<String, String>> getPlaylistIDFromUser (String accessToken) throws IOException, ParseException, SpotifyWebApiException;

     void savePlaylistToUser (List<PlaylistTrack> playlistFromUser, String accessToken) throws IOException, ParseException, SpotifyWebApiException;

     List<PlaylistTrack> getSpotifyPlaylist (String accessToken, String playlistID) throws IOException, ParseException, SpotifyWebApiException;

     Page<SongsDB> getUserPlayListFromDB(Long id, Pageable pageable);

     Track getCurrentSongPlaying (String accessToken) throws IOException, ParseException, SpotifyWebApiException;


}
