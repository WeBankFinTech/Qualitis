package com.webank.wedatasphere.qualitis.project.util;

//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.Session;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.PushCommand;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.transport.JschConfigSessionFactory;
//import org.eclipse.jgit.transport.OpenSshConfig;
//import org.eclipse.jgit.transport.PushResult;
//import org.eclipse.jgit.transport.RemoteConfig;
//import org.eclipse.jgit.transport.SshSessionFactory;
//import org.eclipse.jgit.transport.SshTransport;
//import org.eclipse.jgit.transport.URIish;
//import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author allenzhou@webank.com
 * @date 2023/4/26 9:09
 */
public class GitUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitUtils.class);

//    public static boolean gitPush(String repoRemoteUrl, String repoBranch, String sshPrivateKeyPath, File forCloneDirFile, String projectId, String fileRootDir, String commitMessage)
//        throws GitAPIException, IOException, URISyntaxException {
//        // Set up SSH authentication with JGit
//        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
//            @Override
//            protected void configure(OpenSshConfig.Host host, Session session) {
//                session.setConfig("StrictHostKeyChecking", "no");
//            }
//
//            @Override
//            protected JSch createDefaultJSch(FS fs) throws JSchException {
//                JSch defaultJSch = super.createDefaultJSch(fs);
//                defaultJSch.addIdentity(sshPrivateKeyPath);
//                return defaultJSch;
//            }
//        };
//
//        File repo = new File(forCloneDirFile.getParent() + File.separator + projectId);
//        if (! (repo.listFiles() != null && repo.listFiles().length > 0)) {
//            LOGGER.info("Git clone repo: {}", repoRemoteUrl);
//            Git.cloneRepository()
//                .setURI(repoRemoteUrl)
//                .setBranch(repoBranch)
//                .setTransportConfigCallback(transport -> {
//                    if (transport instanceof SshTransport) {
//                        ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
//                    }
//                })
//                .setDirectory(repo)
//                .call();
//        } else {
//            LOGGER.info("Project repo already exists, open it.");
//        }
//        Git git = Git.open(repo);
//        LOGGER.info("Git open successfully.");
//
//        // Pull changes from remote repository
//        LOGGER.info("Git pull first.");
//        git.pull().setTransportConfigCallback(transport -> {
//            if (transport instanceof SshTransport) {
//                ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
//            }
//        }).call();
//        LOGGER.info("Git pull successfully.");
//
//        LOGGER.info("Git check project root dir existence.");
//        File fileRootDirFile = new File(repo.getPath() + File.separator + fileRootDir);
//        LOGGER.info("Git check project root dir not exist, if not, create.");
//        if (!fileRootDirFile.exists()) {
//            fileRootDirFile.mkdirs();
//            LOGGER.info("Git check project root dir create successfully.");
//        }
//
//        moveDirectoryContents(forCloneDirFile.toString(), fileRootDirFile.getPath());
//
//        git.add().addFilepattern(".").call();
//        LOGGER.info("Git add all successfully.");
//        git.commit().setMessage(commitMessage).call();
//        LOGGER.info("Git commit successfully. Message :{}", commitMessage);
//        // Push changes to remote repository
//        RemoteConfig remoteConfig = new RemoteConfig(git.getRepository().getConfig(), "origin");
//        remoteConfig.addURI(new URIish(repoRemoteUrl));
//        remoteConfig.update(git.getRepository().getConfig());
//        PushCommand pushCommand = git.push()
//            .setRemote("origin")
//            .setTransportConfigCallback(transport -> {
//                if (transport instanceof SshTransport) {
//                    ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
//                }
//            })
//            .setPushAll();
//
//        Iterable<PushResult> remotePushResult = pushCommand.call();
//        remotePushResult.forEach(pushResult -> {
//            LOGGER.info(pushResult.getMessages());
//        });
//        LOGGER.info("Git push successfully.");
//        return true;
//    }
//
//    public static boolean gitPull(List<File> projectFiles, String remoteUrl, String repoBranch, String fileRootDir, String sshPrivateKeyPath, String localForClonePath) throws IOException, GitAPIException {
//        // Set up SSH authentication with JGit
//        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
//            @Override
//            protected void configure(OpenSshConfig.Host host, Session session) {
//                session.setConfig("StrictHostKeyChecking", "no");
//            }
//
//            @Override
//            protected JSch createDefaultJSch(FS fs) throws JSchException {
//                JSch defaultJSch = super.createDefaultJSch(fs);
//                defaultJSch.addIdentity(sshPrivateKeyPath);
//                return defaultJSch;
//            }
//        };
//
//        File repo = new File(localForClonePath);
//        if (! (repo.listFiles() != null && repo.listFiles().length > 0)) {
//            Git.cloneRepository()
//                .setURI(remoteUrl)
//                .setBranch(repoBranch)
//                .setTransportConfigCallback(transport -> {
//                    if (transport instanceof SshTransport) {
//                        ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
//                    }
//                })
//                .setDirectory(repo)
//                .call();
//        } else {
//            LOGGER.info("Project repo already exists, open it.");
//        }
//        Git git = Git.open(repo);
//
//        // Pull changes from remote repository
//        git.pull().setTransportConfigCallback(transport -> {
//            if (transport instanceof SshTransport) {
//                ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
//            }
//        }).call();
//
//        String projectPathStr = repo.getPath() + File.separator + fileRootDir;
//        Path projectPath = Paths.get(projectPathStr);
//
//        try (Stream<Path> paths = Files.walk(projectPath)) {
//            paths.forEach(currPath -> {
//                if (Files.isDirectory(currPath)) {
//                    LOGGER.info("Walk current path is directory. Path: {}", currPath);
//                } else {
//                    LOGGER.info("Walk current path is file. Path: {}", currPath);
//
//                    projectFiles.add(currPath.toFile());
//                    LOGGER.info("Success add current path file. Path: {}", currPath);
//                }
//            });
//        }
//
//        return true;
//    }
//
//    public static void moveDirectoryContents(String sourceDirPath, String destDirPath) throws IOException {
//        Path sourceDir = Paths.get(sourceDirPath);
//        Path targetDir = Paths.get(destDirPath);
//
//        try (Stream<Path> paths = Files.walk(sourceDir)) {
//            paths.forEach(sourcePath -> {
//                Path targetPath = targetDir.resolve(sourceDir.relativize(sourcePath));
//                LOGGER.info("Start to move {} to {}", sourcePath, targetPath);
//                try {
//                    if (Files.isDirectory(sourcePath)) {
//                        try {
//                            Files.createDirectories(targetPath);
//                        } catch (FileAlreadyExistsException e) {
//                            LOGGER.error("Target path already exists. Path: " + targetPath);
//                        }
//                    } else {
//                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
//                    }
//                } catch (IOException e) {
//                    LOGGER.error("Failed to move " + sourcePath + " to " + targetPath);
//                }
//            });
//        }
//
//    }
}
