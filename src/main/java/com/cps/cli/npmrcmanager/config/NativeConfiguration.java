package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import com.cps.cli.npmrcmanager.model.NpmrcmConfiguration;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({ NpmrcmConfiguration.class, NpmrcProfile.class })
public class NativeConfiguration {
}
