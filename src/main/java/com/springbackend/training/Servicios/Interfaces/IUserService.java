package com.springbackend.training.Servicios.Interfaces;

import com.springbackend.training.Controladores.DTO.TrackResponse;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.Base.IServicioBase;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import java.io.IOException;
import java.util.List;

public interface IUserService extends IServicioBase<UserDB, Long> {

     SpotifyApi getProfile();

     List<TrackResponse> savePlaylists(SpotifyApi spotifyApiArmada) throws IOException, ParseException, SpotifyWebApiException;
}
