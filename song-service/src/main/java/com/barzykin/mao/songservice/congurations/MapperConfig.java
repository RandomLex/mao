package com.barzykin.mao.songservice.congurations;

import com.barzykin.mao.songservice.dto.SongDto;
import com.barzykin.mao.songservice.model.Song;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Song.class, SongDto.class).setProvider(request -> {
            Song song = Song.class.cast(request.getSource());
            return new SongDto(
                song.name(),
                song.artist(),
                song.album(),
                song.length(),
                song.resourceId(),
                song.year()
            );
        });

        modelMapper.createTypeMap(SongDto.class, Song.class).setConverter(context -> {
            SongDto songDto = context.getSource();
            return new Song(
                null,
                songDto.name(),
                songDto.artist(),
                songDto.album(),
                songDto.length(),
                songDto.resourceId(),
                songDto.year());
        });
        return modelMapper;
    }

}
