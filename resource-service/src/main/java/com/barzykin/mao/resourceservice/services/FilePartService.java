package com.barzykin.mao.resourceservice.services;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@Service
public class FilePartService {

    public Mono<byte[]> extractBytesFromFilePart(FilePart filePart) {
        return filePart.content().collectList()
            .map(this::mergeDataBuffers);
    }

    private byte[] mergeDataBuffers(List<DataBuffer> dataBuffers) {
        return dataBuffers.stream()
            .map(dataBufferToByteArray())
            .reduce(new byte[]{}, mergeBytes());
    }

    private static Function<DataBuffer, byte[]> dataBufferToByteArray() {
        return dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            return bytes;
        };
    }

    private static BinaryOperator<byte[]> mergeBytes() {
        return (bytes1, bytes2) -> {
            byte[] bytes = new byte[bytes1.length + bytes2.length];
            System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
            System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
            return bytes;
        };
    }

}
