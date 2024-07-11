package com.masiyi.ffmpeg.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FFmpegTemplate {
    private static final Logger log = LoggerFactory.getLogger(FFmpegTemplate.class);

    private String ffmpegPath;

    public FFmpegTemplate(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public FFmpegTemplate() {
    }

    /**
     * 执行命令
     */
    public void execute(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);

        try (InputStream stdoutStream = process.getInputStream();
             BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutStream, "GBK"));
             InputStream stderrStream = process.getErrorStream();
             BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrStream, "GBK"))) {

            Thread stdoutThread = new Thread(() -> {
                String line;
                try {
                    while ((line = stdoutReader.readLine()) != null) {
                        log.info("控制台输出: " + line);
                    }
                } catch (IOException e) {
                    log.error("读取标准输出时发生错误", e);
                }
            });

            Thread stderrThread = new Thread(() -> {
                String line;
                try {
                    while ((line = stderrReader.readLine()) != null) {
                        log.error("控制台输出: " + line);
                    }
                } catch (IOException e) {
                    log.error("读取错误输出时发生错误", e);
                }
            });

            stdoutThread.start();
            stderrThread.start();

            // Wait for the threads to finish reading
            stdoutThread.join();
            stderrThread.join();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("进程正常完成");
            } else {
                log.info("进程异常结束");
            }
        }
    }


    /**
     * 格式转换
     */
    public void convert(String inputFile, String outputFile) {
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath).append(" -i ").append(inputFile).append(" -y ").append(outputFile);
        try {
            execute(command.toString());
            log.info("格式转换成功");
        } catch (IOException | InterruptedException e) {
            log.error("格式转换失败", e);
        }
    }
}
