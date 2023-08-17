package com.cps.cli.npmrcmanager.config;

import com.cps.cli.npmrcmanager.model.Configuration;
import com.cps.cli.npmrcmanager.model.NpmrcProfile;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@org.springframework.context.annotation.Configuration
@RegisterReflectionForBinding({ Configuration.class, NpmrcProfile.class })
public class NativeConfiguration {
}
