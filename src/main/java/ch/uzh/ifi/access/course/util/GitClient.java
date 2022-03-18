package ch.uzh.ifi.access.course.util;

import ch.uzh.ifi.access.config.AccessProperties;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Component
public class GitClient {

    private AccessProperties accessProperties;

    private SshTransportConfigCallback sshTransportConfigCallback;

    public GitClient(AccessProperties accessProperties) {
        this.accessProperties = accessProperties;
        this.sshTransportConfigCallback = new SshTransportConfigCallback();
    }

    public File getRepoDir(String gitURL) throws URISyntaxException {
        return new File(accessProperties.getCacheDir() + new URIish(gitURL).getPath().replace(".git", ""));
    }

    public String pullOrClone(String gitURL) {
        String token = gitURL.contains("gitlab") ? accessProperties.getGitlabWebhook() : accessProperties.getGithubWebhook();
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(token, "");
        try {
            File repoDir = getRepoDir(gitURL);
            if (repoDir.exists()) {
                try (Git git = new Git(new FileRepository(repoDir.getPath() + "/.git"))) {
                    git.pull()
                            .setCredentialsProvider(credentialsProvider)
                            .setTransportConfigCallback(sshTransportConfigCallback)
                            .call();
                    return getCommitHash(git);
                }
            } else {
                Git git = Git.cloneRepository()
                        .setURI(gitURL)
                        .setDirectory(repoDir)
                        .setCredentialsProvider(credentialsProvider)
                        .setTransportConfigCallback(sshTransportConfigCallback)
                        .call();
                return getCommitHash(git);
            }
        } catch (GitAPIException | URISyntaxException | IOException e) {
            log.error("Failed to pull or clone repository: {}", gitURL, e);
            return null;
        }
    }

    private String getCommitHash(Git git) {
        try {
            Ref head = git.getRepository().findRef("HEAD");
            if (head != null) {
                ObjectId headId = head.getObjectId();
                if (headId != null)
                    return headId.getName();
            }
        } catch (NullPointerException | IOException e) {
            log.error("Failed to fetch commit hash of repository {}", git.getRepository().getIdentifier());
        }
        return null;
    }

    private static class SshTransportConfigCallback implements TransportConfigCallback {

        private final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        };

        @Override
        public void configure(Transport transport) {
            if (transport instanceof SshTransport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        }
    }
}
