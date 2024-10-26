package com.barzykin.mao.resourceservice.services;

import com.barzykin.mao.resourceservice.dto.SongDto;
import com.barzykin.mao.resourceservice.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.helpers.DefaultHandler;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
public class Mp3Service {

    public SongDto toSongDto(Metadata metadata, long resourceId) {
        return new SongDto(
            metadata.get("dc:title"),
            metadata.get("xmpDM:artist"),
            metadata.get("xmpDM:album"),
            TimeUtils.secondsAsDoubleToHMS(metadata.get("xmpDM:duration")),
            resourceId,
            metadata.get("xmpDM:releaseDate")
        );
    }

    public Mono<Metadata> extractMetadata(byte[] mp3Bytes) {
        return Mono.fromCallable(() -> {
            try (InputStream input = new ByteArrayInputStream(mp3Bytes)) {
                Metadata metadata = new Metadata();
                new Mp3Parser().parse(input, new BodyContentHandler(), metadata, new ParseContext());
                return metadata;
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract MP3 metadata", e);
            }
        });
    }

    // Check if the MIME type is MP3 (audio/mpeg)
    public Mono<Boolean> isMp3(byte[] fileBytes) {
        Tika tika = new Tika();
        return Mono.fromCallable(() -> {
            String mimeType = tika.detect(fileBytes);
            return mimeType.equals("audio/mpeg");
        });
    }

    // Validate MP3 structure by parsing it
    public Mono<Boolean> validateMp3Structure(byte[] fileBytes) {
        return Mono.fromCallable(() -> {
            try (InputStream input = new ByteArrayInputStream(fileBytes)) {
                new Mp3Parser().parse(input, new DefaultHandler(), new Metadata(), new ParseContext());
                return true;  // If parsing succeeds, the file is a valid MP3
            } catch (Exception e) {
                return false;  // If parsing fails, the file is invalid
            }
        });
    }

}
