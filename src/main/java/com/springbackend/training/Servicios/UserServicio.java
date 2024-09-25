package com.springbackend.training.Servicios;

import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import com.springbackend.training.Repositorios.RepositorioSongs;
import com.springbackend.training.Repositorios.RepositorioUser;
import com.springbackend.training.Servicios.Base.ServicioBase;
import com.springbackend.training.Servicios.Interfaces.IUserService;
import com.springbackend.training.Servicios.Response.TrackResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.*;


import java.io.IOException;
import java.net.URI;
import java.util.*;

import java.util.stream.Collectors;


@Service
@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
public class UserServicio extends ServicioBase<UserDB, Long> implements IUserService {


    private final RepositorioUser personaRepository;

    private final RepositorioSongs cancionesRepository;

    private final Environment env;

    private final MusixMatch musixMatch;

    private final PasswordEncoder passwordEncoder;



    public UserServicio(RepositorioBase<UserDB, Long> baseRepository, RepositorioUser personaRepository, RepositorioUser repositorioUser, RepositorioSongs cancionesRepository, Environment env, MusixMatch musixMatch, PasswordEncoder passwordEncoder) {
        super(baseRepository);
        this.personaRepository = repositorioUser;
        this.cancionesRepository = cancionesRepository;
        this.env = env;
        this.musixMatch = musixMatch;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SpotifyApi getProfile() {
        URI redirectedURL = SpotifyHttpManager.makeUri(env.getProperty("spotify.api.redirectURI"));
        return new SpotifyApi
                .Builder()
                .setClientId(env.getProperty("spotify.api.clientID"))
                .setClientSecret(env.getProperty("spotify.api.secretKey"))
                .setRedirectUri(redirectedURL)
                .build();
    }

    @Bean
    public MusixMatch getMusixMatch() {
        return new MusixMatch(env.getProperty("musicxmatch.api.key"));
    }


    private SpotifyApi spotifyUser (String accessToken) throws ParseException, SpotifyWebApiException {
        return new SpotifyApi.Builder().setAccessToken(accessToken).build();
    }

    @Override
    public List<Map<String, String>> getPlaylistIDFromUser(String AccessToken) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = spotifyUser(AccessToken);
        Paging<PlaylistSimplified> playlist = spotifyApi.getListOfCurrentUsersPlaylists().build().execute();
            return Arrays.stream(playlist.getItems()).map(
                    playlistSimplified -> {
                        Map<String, String> playListMap = new HashMap<>();
                        playListMap.put("nombre", playlistSimplified.getName());
                        playListMap.put("id", playlistSimplified.getId());
                        return playListMap;
                    }).toList();
    }

    @Override
    public void crearUsuarioLocal(UserDB userDB) {
        userDB.setPassword(passwordEncoder.encode(userDB.getPassword()));
        personaRepository.save(userDB);
    }

    //TODO: Primero el usuario tiene que crear una cuenta en mi backend despues se le pregunta si quiere
    // iniciar sesion en spotify y otorge permisos para acceder a su cuenta de spotify (Averiguar como jaja)
    @Override
    public void savePlaylistToUser(List<PlaylistTrack> playlistFromUser, String accessToken) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = spotifyUser(accessToken);
        User userSpotify = spotifyApi.getCurrentUsersProfile().build().execute();
        UserDB newUserDB = new UserDB();
        newUserDB.setUsuarioSpotify(userSpotify.getDisplayName());
        personaRepository.save(newUserDB);
        List<SongsDB> userSongs = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistFromUser) {
            try {
                Track track = getTrackFromPlaylistTrack(playlistTrack);
                assert track != null;
                ArtistSimplified artistOnTrack = track.getArtists()[0];
                SongsDB newSongDB = new SongsDB(newUserDB, track.getName(), artistOnTrack.getName(), track.getPreviewUrl());
                cancionesRepository.save(newSongDB);
                userSongs.add(newSongDB);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        newUserDB.setCancionesPlaylist(userSongs);
        personaRepository.save(newUserDB);
    }

    @Override
    public List<PlaylistTrack> getSpotifyPlaylist(String accessToken, String playlistID) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = spotifyUser(accessToken);
        Playlist playlist = spotifyApi.getPlaylist(playlistID).build().execute();
        return Arrays.stream(playlist.getTracks().getItems()).collect(Collectors.toList());
    }

    @Override
    public Page<SongsDB> getUserPlayListFromDB(Long id, Pageable pageable) {
        return cancionesRepository.getSongsDBByUsuarioPropietario_ID(id, pageable);
    }

    @Override
    public TrackResponse getCurrentSongPlaying(String accessToken) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = spotifyUser(accessToken);
        CurrentlyPlaying trackPlaying = spotifyApi.getUsersCurrentlyPlayingTrack()
                .build()
                .execute();
        if (trackPlaying != null){
            Track track = (Track) trackPlaying.getItem();
                try {
                    org.jmusixmatch.entity.track.Track TrackMusicMatch = musixMatch.getMatchingTrack(track.getName(), Arrays.stream(track.getArtists()).toList().get(0).getName());
                    Lyrics lyricsAPI = musixMatch.getLyrics(TrackMusicMatch.getTrack().getTrackId());
                    return new TrackResponse(
                            track.getTrackNumber(),
                            track.getName(),
                            track.getPreviewUrl(),
                            Arrays.stream(track.getArtists()).toList().get(0).getName(),
                            lyricsAPI.getLyricsBody().replaceAll("\n", "<br>")

                    );
                }catch (MusixMatchException e){
                    return new TrackResponse(
                            track.getTrackNumber(),
                            track.getName(),
                            track.getPreviewUrl(),
                            Arrays.stream(track.getArtists()).toList().get(0).getName(),
                            "Ups, no encontramos la letra :)"

                    );
                }
        }else {
            throw new SpotifyWebApiException();
        }
    }

    private Track getTrackFromPlaylistTrack(PlaylistTrack playlistTrack) {
        try {
            return (Track)playlistTrack.getTrack();
        } catch (Exception e) {
            return null;
        }
    }


}
