package com.barzykin.mao.resourceservice.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.barzykin.mao.resourceservice.utils.TimeUtils.secondsAsDoubleToHMS;

@Service
@Slf4j
public class Mp3Handler {

    public void handleMp3(String filename) {
        log.debug("Handling mp3");
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        Mp3Parser parser = new Mp3Parser();
        try (FileInputStream fis = new FileInputStream(getFileFromClasspath(filename))) {
            parser.parse(fis, handler, metadata, null);
            log.info("name: {}", metadata.get("dc:title"));
            log.info("artist: {}", metadata.get("xmpDM:artist"));
            log.info("album: {}", metadata.get("xmpDM:album"));
            log.info("length: {}", secondsAsDoubleToHMS(metadata.get("xmpDM:duration")));
            log.info("year: {}", metadata.get("xmpDM:releaseDate"));
        } catch (FileNotFoundException e) {
            log.error("File not found", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("IO exception", e);
            throw new RuntimeException(e);
        } catch (TikaException e) {
            log.error("Tika exception", e);
            throw new RuntimeException(e);
        } catch (SAXException e) {
            log.error("SAX exception", e);
            throw new RuntimeException(e);
        }
    }

    private static File getFileFromClasspath(String path) throws FileNotFoundException {
        return ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + path);
    }

}
