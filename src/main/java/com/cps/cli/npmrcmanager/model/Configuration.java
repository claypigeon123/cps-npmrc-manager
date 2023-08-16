package com.cps.cli.npmrcmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    private String npmrcPath;

    @JsonIgnore
    private String activeProfile;

    @JsonIgnore
    private List<NpmrcProfile> profiles = new ArrayList<>();
}
