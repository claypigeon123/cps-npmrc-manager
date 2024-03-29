package com.cps.cli.npmrcmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.aot.hint.annotation.Reflective;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Reflective // for native usage
@NoArgsConstructor
@AllArgsConstructor
public class NpmrcmConfiguration implements Serializable {

    private String npmrcPath;

    @Builder.Default
    private transient List<NpmrcProfile> profiles = new ArrayList<>();
}
