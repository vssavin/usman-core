package com.github.vssavin.usmancore.config;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Provides scanning and execution of sql scripts.
 *
 * @author vssavin on 12.12.2023.
 */
public class SqlScriptExecutor {

    private static final Logger log = LoggerFactory.getLogger(SqlScriptExecutor.class);

    private static final String READER_ACCESS_ERROR_MESSAGE = "Reader access error! ";

    private static final String SEARCHING_SQL_FILES_ERROR_MESSAGE = "Searching sql files error! ";

    private final DataSource dataSource;

    public SqlScriptExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeSqlScriptsFromResource(List<String> scriptsList, String resourcePath) {
        Path path;
        try {
            path = getResourcePath(resourcePath);
        }
        catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("Executing scripts from resource error!", e);
        }

        try (Stream<Path> paths = Files.walk(path)) {
            sqlScriptBufferedReaderStream(filterSqlFilesStream(paths)
                .filter(filePath -> scriptsList.contains(filePath.getFileName().toString()))).forEach(script -> {
                    try (Reader reader = script) {
                        executeSqlScript(reader, dataSource);
                    }
                    catch (IOException e) {
                        log.error(READER_ACCESS_ERROR_MESSAGE, e);
                    }
                });
        }
        catch (Exception e) {
            log.error(SEARCHING_SQL_FILES_ERROR_MESSAGE, e);
        }
    }

    public void executeSqlScriptsFromDirectory(List<String> scriptsList, String directoryPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            sqlScriptBufferedReaderStream(filterSqlFilesStream(paths)
                .filter(filePath -> scriptsList.contains(filePath.getFileName().toString()))).forEach(script -> {
                    try (Reader scriptReader = script) {
                        executeSqlScript(scriptReader, dataSource);
                    }
                    catch (IOException e) {
                        log.error(READER_ACCESS_ERROR_MESSAGE, e);
                    }
                });
        }
        catch (Exception e) {
            log.error(SEARCHING_SQL_FILES_ERROR_MESSAGE, e);
        }
    }

    public void executeAllSqlScriptsFromResource(String resourcePath) {
        Path path;
        try {
            path = getResourcePath(resourcePath);
        }
        catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("Executing scripts from resource error!", e);
        }

        try (Stream<Path> paths = Files.walk(path)) {
            sqlScriptBufferedReaderStream(filterSqlFilesStream(paths)).forEach(script -> {
                try (Reader reader = script) {
                    executeSqlScript(reader, dataSource);
                }
                catch (IOException e) {
                    log.error("Reader access error!", e);
                }
            });
        }
        catch (Exception e) {
            log.error(SEARCHING_SQL_FILES_ERROR_MESSAGE, e);
        }
    }

    public void executeAllScriptsFromDirectory(String directoryPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            sqlScriptBufferedReaderStream(filterSqlFilesStream(paths)).forEach(script -> {
                try (Reader reader = script) {
                    executeSqlScript(reader, dataSource);
                }
                catch (IOException e) {
                    log.error("Reader access error!", e);
                }
            });
        }
        catch (Exception e) {
            log.error(SEARCHING_SQL_FILES_ERROR_MESSAGE, e);
        }
    }

    private Stream<BufferedReader> sqlScriptBufferedReaderStream(Stream<Path> pathStream) {
        return pathStream.map(filePath -> {
            try {
                return new BufferedReader(new FileReader(filePath.toFile()));
            }
            catch (FileNotFoundException e) {
                // ignore because file is definitely exists if Files.walk(...) used
            }
            return null;
        });
    }

    private Stream<Path> filterSqlFilesStream(Stream<Path> paths) {
        return paths.map(Path::toFile)
            .filter(File::isFile)
            .filter(file -> file.getName().endsWith(".sql"))
            .map(File::toPath);
    }

    private Path getResourcePath(String resourcePath) throws URISyntaxException, IOException {
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Resource " + resourcePath + " nor found!");
        }
        URI uri = url.toURI();
        Path path;
        if (uri.getScheme().equals("jar")) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                path = fs.getPath(resourcePath);
            }

        }
        else {
            path = Paths.get(uri);
        }

        return path;
    }

    private void executeSqlScript(Reader reader, DataSource dataSource) {
        StringWriter logWriter = new StringWriter();
        try (Reader innerReader = reader) {
            Connection connection = dataSource.getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            StringWriter errorWriter = new StringWriter();
            scriptRunner.setLogWriter(new PrintWriter(logWriter));
            scriptRunner.setErrorLogWriter(new PrintWriter(errorWriter));
            scriptRunner.runScript(innerReader);
            String message = logWriter.toString();
            if (!message.isEmpty()) {
                log.debug(message);
            }
            if (!errorWriter.toString().isEmpty()) {
                throw new ExecuteSqlScriptException("Executing script error: " + errorWriter);
            }

        }
        catch (Exception e) {
            if (!logWriter.toString().isEmpty()) {
                log.debug(logWriter.toString());
            }
            log.error("Executing init script error: ", e);
        }
    }

    private static class ExecuteSqlScriptException extends RuntimeException {

        ExecuteSqlScriptException(String message) {
            super(message);
        }

    }

}
