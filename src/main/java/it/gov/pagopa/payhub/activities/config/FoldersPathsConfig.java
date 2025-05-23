package it.gov.pagopa.payhub.activities.config;

import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "folders")
public class FoldersPathsConfig {

  private Path shared;
  private Path tmp;

  @NestedConfigurationProperty
  private ProcessTargetSubFolders processTargetSubFolders;
  @NestedConfigurationProperty
  private FoldersPaths paths;

  @PostConstruct
  void checkConfig(){
    if (!Files.exists(shared)) {
      throw new IllegalStateException("Shared folder doesn't exist: " + shared);
    }
    if (!Files.exists(tmp)) {
      throw new IllegalStateException("Temp folder doesn't exist: " + tmp);
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @SuperBuilder
  public static class ProcessTargetSubFolders {
    private String archive;
    private String errors;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @SuperBuilder
  public static class FoldersPaths {
    private String rtFolder;
  }
}
