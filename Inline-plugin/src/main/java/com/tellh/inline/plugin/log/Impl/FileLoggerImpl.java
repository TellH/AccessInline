//
//                            _ooOoo_  
//                           o8888888o  
//                           88" . "88  
//                           (| -_- |)  
//                            O\ = /O  
//                        ____/`---'\____  
//                      .   ' \\| |// `.  
//                       / \\||| : |||// \  
//                     / _||||| -:- |||||- \  
//                       | | \\\ - /// | |  
//                     | \_| ''\---/'' | |  
//                      \ .-\__ `-` ___/-. /  
//                   ___`. .' /--.--\ `. . __  
//                ."" '< `.___\_<|>_/___.' >'"".  
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |  
//                 \ \ `-. \_ __\ /__ _/ .-` / /  
//         ======`-.____`-.___\_____/___.-`____.-'======  
//                            `=---='  
//  
//         .............................................
package com.tellh.inline.plugin.log.Impl;


import com.google.common.io.Files;

import org.gradle.api.logging.LogLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by gengwanpeng on 16/7/6.
 */
public class FileLoggerImpl extends BaseLogger {

    public static FileLoggerImpl of(String fileName) throws IOException {
        File logFile = new File(fileName);
        if (!logFile.exists()) {
            Files.createParentDirs(logFile);
        }
        PrintWriter pr = new PrintWriter(new FileOutputStream(fileName), true);
        return new FileLoggerImpl(pr);
    }

    private PrintWriter pr;

    private FileLoggerImpl(PrintWriter pr) {
        this.pr = pr;
    }

    @Override
    protected void write(LogLevel level, String prefix, String msg, Throwable t) {
        pr.println(String.format("%s [%-10s] %s", level.name(), prefix, msg));
        if (t != null) {
            t.printStackTrace(pr);
        }
    }
}
