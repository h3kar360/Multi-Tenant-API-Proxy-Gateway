package org.h3kar360.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, List<String>> headers;

    private byte[] body;
}
