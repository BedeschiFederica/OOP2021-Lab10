package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    /**
    * {@inheritDoc}
    */
    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("Invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(Song::getSongName).sorted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.keySet().stream().filter(n -> this.albums.get(n) == year);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().orElseGet(() -> "").equals(albumName)).count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().isEmpty()).count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
                .filter(s -> s.getAlbumName().orElseGet(() -> "").equals(albumName))
                .mapToDouble(Song::getDuration)
                .average();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> longestSong() {
        final double longestDuration = this.songs.stream()
                .mapToDouble(Song::getDuration)
                .max()
                .orElseGet(() -> 0.0);
        final Optional<Song> longestSong = this.songs.stream()
                .filter(s -> s.getDuration() == longestDuration)
                .findAny();
        return longestSong.isEmpty() ? Optional.empty() : Optional.of(longestSong.get().getSongName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> longestAlbum() {
        return this.albums.keySet().stream()
                .max((a1, a2) -> Double.compare(totalDuration(a1), totalDuration(a2)));
    }

    private double totalDuration(final String albumName) {
        return this.songs.stream()
                .filter(s -> s.getAlbumName().orElseGet(() -> "").equals(albumName))
                .mapToDouble(Song::getDuration)
                .sum();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return this.songName;
        }

        public Optional<String> getAlbumName() {
            return this.albumName;
        }

        public double getDuration() {
            return this.duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = this.songName.hashCode() ^ this.albumName.hashCode() ^ Double.hashCode(this.duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return this.albumName.equals(other.albumName) && this.songName.equals(other.songName)
                        && this.duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + this.songName
                    + ", albumName=" + this.albumName
                    + ", duration=" + this.duration + "]";
        }

    }

}
