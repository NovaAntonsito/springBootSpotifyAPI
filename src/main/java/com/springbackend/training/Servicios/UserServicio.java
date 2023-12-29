package com.springbackend.training.Servicios;

import com.springbackend.training.Controladores.Response.TrackResponse;
import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import com.springbackend.training.Repositorios.RepositorioSongs;
import com.springbackend.training.Repositorios.RepositorioUser;
import com.springbackend.training.Servicios.Base.ServicioBase;
import com.springbackend.training.Servicios.Interfaces.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.core.env.Environment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
public class UserServicio extends ServicioBase<UserDB, Long> implements IUserService {


    private final RepositorioUser personaRepository;

    private final RepositorioSongs cancionesRepository;

    private final Environment env;


    public UserServicio(RepositorioBase<UserDB, Long> baseRepository, RepositorioUser personaRepository, RepositorioUser repositorioUser, RepositorioSongs cancionesRepository, Environment env) {
        super(baseRepository);
        this.personaRepository = repositorioUser;
        this.cancionesRepository = cancionesRepository;
        this.env = env;
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

    @Override
    public Page<TrackResponse> savePlaylists(SpotifyApi spotifyApiArmada, Pageable pageable) throws IOException, ParseException, SpotifyWebApiException {
        try {
            User userSpotify = spotifyApiArmada.getCurrentUsersProfile().build().execute();
            String usernameSpotify = userSpotify.getDisplayName();
            if (personaRepository.existsByUsuarioSpotify(usernameSpotify)) {
                throw new RuntimeException("Ya existe un usuario");
            }
            String userID = userSpotify.getId();
            GetListOfUsersPlaylistsRequest usersPlaylistsRequest = spotifyApiArmada.getListOfUsersPlaylists(userID).build();
            Paging<PlaylistSimplified> userPlaylists = usersPlaylistsRequest.execute();
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            UserDB newUserDB = new UserDB();
            newUserDB.setUsuarioSpotify(usernameSpotify);
            personaRepository.save(newUserDB);

            AtomicInteger index = new AtomicInteger(0);
            ArrayList<TrackResponse> trackResponses = Arrays.stream(userPlaylists.getItems())
                    .flatMap(userPlaylist -> {
                        try {
                            return Arrays.stream(spotifyApiArmada.getPlaylist(userPlaylist.getId()).build().execute().getTracks().getItems());
                        } catch (IOException | ParseException | SpotifyWebApiException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(playlistTrack -> {
                        Track track = getTrackFromPlaylistTrack((PlaylistTrack) playlistTrack);
                        List<ArtistSimplified> artists = List.of(track.getArtists());
                        if (!artists.isEmpty()) {
                            String trackName = track.getName();
                            String previewUrl = track.getPreviewUrl();
                            String artistName = artists.get(0).getName();
                            SongsDB newSongDB = new SongsDB(newUserDB, trackName, artistName, previewUrl);
                            cancionesRepository.save(newSongDB);
                            return new TrackResponse(index.getAndIncrement(), trackName, previewUrl, artistName);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<TrackResponse> trackResponsesPaged = trackResponses.stream()
                    .skip((pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            int totalPages = (int) Math.ceil((double) trackResponses.size() / (double) pageSize);


            return new PageImpl<>(trackResponsesPaged, pageable, totalPages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<TrackResponse> getMyPlaylist(SpotifyApi spotifyApiArmada, Pageable pageable) throws IOException, ParseException, SpotifyWebApiException {
        ArrayList<TrackResponse> trackList = new ArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        Playlist playlist = spotifyApiArmada
                .getPlaylist(env.getProperty("spotify.anton.MainPlaylist"))
                .build()
                .execute();
        for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {
            Track track = (Track) playlistTrack.getTrack();
            List<ArtistSimplified> artists = List.of(track.getArtists());
            if (!artists.isEmpty()) {
                String trackName = track.getName();
                String previewUrl = track.getPreviewUrl();
                String artistName = artists.get(0).getName();
                trackList.add(new TrackResponse(atomicInteger.getAndIncrement(), trackName, previewUrl, artistName));
            }
        }
        return new PageImpl<>(trackList, pageable, trackList.size());
    }

    private Track getTrackFromPlaylistTrack(PlaylistTrack playlistTrack) {
        try {
            return (Track)playlistTrack.getTrack();
        } catch (Exception e) {
            return null;
        }
    }


}
