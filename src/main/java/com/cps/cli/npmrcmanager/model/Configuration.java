package com.cps.cli.npmrcmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Configuration implements Serializable {

    private String npmrcPath;

    @JsonIgnore
    private String activeProfile;

    @JsonIgnore
    private List<NpmrcProfile> profiles = new ArrayList<>();
}
