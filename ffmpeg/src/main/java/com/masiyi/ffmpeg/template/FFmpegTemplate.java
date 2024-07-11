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
    public String execute(String command) throws IOException, InterruptedException {
        log.info("执行命令: " + command);
        Process process = Runtime.getRuntime().exec(java.lang.String.valueOf(command));
        //命令行输出最终的内容
        StringBuilder output = new StringBuilder();
        try (InputStream stdoutStream = process.getInputStream();
             BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutStream, "GBK"));
             InputStream stderrStream = process.getErrorStream();
             BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrStream, "GBK"))) {

            Thread stdoutThread = new Thread(() -> {
                String line;
                try {
                    while ((line = stdoutReader.readLine()) != null) {
                        log.info("控制台输出: " + line);
                        output.append(line);
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
                log.error("进程异常结束");
            }
        }
        return output.toString();
    }


    /**
     * 格式转换
     */
    public void convert(String inputFile, String outputFile) {
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath).append("ffmpeg ").append(" -i ").append(inputFile).append(" -y ").append(outputFile);
        try {
            execute(command.toString());
            log.info("格式转换成功");
        } catch (IOException | InterruptedException e) {
            log.error("格式转换失败", e);
        }
    }

    /**
     * 提取媒体时长
     */
    public String extractAudio(String inputFile) {
        String out = null;
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath).append("ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ").append(inputFile);
        try {
            out = execute(command.toString());
            log.info("提取媒体时长成功");
        } catch (IOException | InterruptedException e) {
            log.error("提取媒体时长失败", e);
        }
        return out;
    }

    /**
     * 流复制
     * 省去了重新编码的时间，格式转换将十分迅速
     */
    public void copy(String inputFile, String outputFile) {
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath).append("ffmpeg ").append(" -i ").append(inputFile).append(" -y -c copy ").append(outputFile);
        try {
            execute(command.toString());
            log.info("流复制成功");
        } catch (IOException | InterruptedException e) {
            log.error("流复制失败", e);
        }
    }

    /**
     * 提取流（音频、字幕）
     */
    public void extract(String inputFile, String outputFile) {
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath).append("ffmpeg ").append(" -i ").append(inputFile).append(" -y -c:a copy ").append(outputFile);
        try {
            execute(command.toString());
            log.info("提取流成功");
        } catch (IOException | InterruptedException e) {
            log.error("提取流失败", e);
        }
    }

    /**
     * 截取视频片段
     */
    public void captureVideoFootage(String inputFile, String outputFile, String startTime, String endTime) {
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath).append("ffmpeg ").append(" -ss ").append(startTime).append(" -to ").append(endTime).append(" -i ").append(inputFile).append(" -y -c copy ").append(outputFile);
        try {
            execute(command.toString());
            log.info("截取视频片段成功");
        } catch (IOException | InterruptedException e) {
            log.error("截取视频片段失败", e);
        }
    }



}
