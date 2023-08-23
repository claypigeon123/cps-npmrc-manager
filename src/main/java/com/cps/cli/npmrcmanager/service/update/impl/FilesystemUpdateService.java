package com.cps.cli.npmrcmanager.service.update.impl;

import com.cps.cli.npmrcmanager.client.updates.UpdatesClient;
import com.cps.cli.npmrcmanager.model.system.SupportedOperatingSystem;
import com.cps.cli.npmrcmanager.service.update.UpdateService;
import com.cps.cli.npmrcmanager.util.ArchiveHelper;
import com.cps.cli.npmrcmanager.util.FilesystemHelper;
import com.cps.cli.npmrcmanager.util.OperatingSystemProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class FilesystemUpdateService implements UpdateService {

    @NonNull
    private final UpdatesClient updatesClient;

    @NonNull
    private final FilesystemHelper filesystemHelper;

    @NonNull
    private final OperatingSystemProvider operatingSystemProvider;

    @NonNull
    private final ArchiveHelper archiveHelper;

    @Override
    public void tryUpdate(boolean force) {
        boolean isUpdateAvailable = force || updatesClient.isUpdateAvailable();

        if (!isUpdateAvailable) {
            System.out.printf("Your installation is up-to-date!%n");
            return;
        }

        Path executablePath = filesystemHelper.getCurrentExecutingPath().toAbsolutePath();
        Path backup = executablePath.resolveSibling(format("%s.bak", executablePath.getFileName())).toAbsolutePath();
        boolean createdTempDir = false;
        boolean createdBackup = false;
        boolean error = false;

        try {
            SupportedOperatingSystem os = operatingSystemProvider.getOperatingSystem();

            Path tempDir = filesystemHelper.createTempDir().toAbsolutePath();
            createdTempDir = true;
            System.out.printf("Created temporary directory: [%s]%n", tempDir);

            Path downloadedAssetPath = updatesClient.downloadLatestRelease(tempDir, os);
            System.out.printf("Downloaded archive to: [%s]%n", downloadedAssetPath.toAbsolutePath());

            archiveHelper.extract(downloadedAssetPath);
            System.out.printf("Extracted archive in directory: [%s]%n", tempDir);

            filesystemHelper.move(executablePath, backup);
            createdBackup = true;
            System.out.printf("Backed up current npmrcm executable to [%s]%n", backup);

            filesystemHelper.copy(tempDir.resolve(executablePath.getFileName()), executablePath);
            System.out.printf("Copied executable from temp dir to [%s]%n", executablePath);

            int cleanedUp = filesystemHelper.cleanTempDirs();
            System.out.printf("Cleaned up %d temporary %s created during this run%n", cleanedUp, cleanedUp == 1 ? "directory" : "directories");

            System.out.printf("Update completed!%n");

            if (SupportedOperatingSystem.LINUX_X86_64.equals(os) || SupportedOperatingSystem.LINUX_ARM64.equals(os)) {
                System.out.printf("%n!!! If npmrcm stops working, you may have to run the following command: (add sudo if relevant)%n");
                System.out.printf("chmod +x \"%s\"%n", executablePath);
            }
            if (SupportedOperatingSystem.MAC_OS_ARM64.equals(os)) {
                System.out.printf("%n!!! If npmrcm stops working, you may have to run the following command:%n");
                System.out.printf("sudo xattr -r -d com.apple.quarantine \"%s\"%n", executablePath);
            }
        } catch (Exception e) {
            error = true;
            throw e;
        } finally {
            if (createdTempDir) {
                int cleanedUp = filesystemHelper.cleanTempDirs();
                System.out.printf("Cleaned up %d temporary %s created during this run%n", cleanedUp, cleanedUp == 1 ? "directory" : "directories");
            }

            if (createdBackup && error) {
                filesystemHelper.move(backup, executablePath);
                System.out.printf("Restored npmrcm executable from backup%n");
            }
        }

    }
}
