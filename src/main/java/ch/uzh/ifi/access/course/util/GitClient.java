package ch.uzh.ifi.access.course.util;

import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.*;

import java.io.File;
import java.io.IOException;

public class GitClient {

    private SshTransportConfigCallback sshTransportConfigCallback = new SshTransportConfigCallback();

    public String pull(String pathname) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(new File(pathname)));
        git.pull().setTransportConfigCallback(sshTransportConfigCallback).call();
        String head = getCommitHash(git);
        git.close();

        return head;
    }

    public String clone(String url, File directory) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(url)
                .setTransportConfigCallback(sshTransportConfigCallback)
                .setDirectory(directory)
                .call();
        String head = getCommitHash(git);
        git.close();
        return head;
    }

    private String getCommitHash(Git git) {
        return git.getRepository().getAllRefs().get("HEAD").getObjectId().getName();
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
