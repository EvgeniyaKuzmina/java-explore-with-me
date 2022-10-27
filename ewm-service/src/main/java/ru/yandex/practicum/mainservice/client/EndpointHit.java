package ru.yandex.practicum.mainservice.client;

import lombok.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EndpointHit {

    private String app;
    private String uri;
    private String ip;
    private byte[] timestamp;


}
