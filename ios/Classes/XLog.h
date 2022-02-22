//
//  XLog.h
//  Runner
//
//  Created by syx on 2022/2/15.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, XloggerType) {
    kAll = 0,
    kVerbose = 0,
    kDebug,
    kInfo,
    kWarn,
    kError,
    kFatal,
    kNone,
};



@interface XLog : NSObject

+ (void)startWithPath:(NSString *)path;

+ (void)close;

+ (void)flush;

+ (void)logWithLevel:(XloggerType)logLevel message:(NSString *)message;

@end


