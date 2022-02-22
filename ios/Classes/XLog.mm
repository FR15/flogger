//
//  XLog.m
//  Runner
//
//  Created by syx on 2022/2/15.
//

#import "XLog.h"
#import <mars/xlog/xlogger.h>
#import <mars/xlog/appender.h>
#import <sys/xattr.h>

@implementation XLog

+ (void)startWithPath:(NSString *)path {
    
    NSString* logPath = [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0] stringByAppendingString:path];
    
    // set do not backup for logpath
    const char* attrName = "com.apple.MobileBackup";
    u_int8_t attrValue = 1;
    setxattr([logPath UTF8String], attrName, &attrValue, sizeof(attrValue), 0, 0);
    
#if DEBUG
    xlogger_SetLevel(kLevelDebug);
    mars::xlog::appender_set_console_log(true);
#else
    xlogger_SetLevel(kLevelInfo);
    mars::xlog::appender_set_console_log(false);
#endif
    
    mars::xlog::XLogConfig config;
    config.logdir_ = [logPath UTF8String];
    config.pub_key_ = "";
    config.nameprefix_ = "GS";
    mars::xlog::appender_open(config);
}

+ (void)close {
    mars::xlog::appender_close();
}

+ (void)flush {
    mars::xlog::appender_flush();
}

+ (void)logWithLevel:(XloggerType)logLevel message:(NSString *)message {
    XLoggerInfo info;
    info.level = (TLogLevel)logLevel;
    gettimeofday(&info.timeval, NULL);
    info.tag = "";
    info.filename = "";
    info.func_name = "";
    info.line = 0;
    info.tid = (uintptr_t)[NSThread currentThread];
    info.maintid = (uintptr_t)[NSThread mainThread];
    xlogger_Write(&info, message.UTF8String);
}

@end
