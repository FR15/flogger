#import "LoggerPlugin.h"
#import "XLog.h"

static NSString *const CHANNEL_NAME = @"com.gaussian.gsbot/log";

@implementation LoggerPlugin

- (instancetype)init {
    self = [super init];
    if (self) {
        
        [XLog startWithPath:@"/logs"];
                
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillTerminate) name:UIApplicationWillTerminateNotification object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)appWillTerminate {
    [XLog flush];
    [XLog close];
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:CHANNEL_NAME
            binaryMessenger:[registrar messenger]];
  LoggerPlugin* instance = [[LoggerPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall *)call result:(FlutterResult)result {
    if ([@"xlog" isEqualToString:call.method]) {
        
        NSNumber *type = [self checkArgument:call.arguments forKey:@"type" ofType:[NSNumber class]];
        NSString *msg = [self checkArgument:call.arguments forKey:@"msg" ofType:[NSString class]];
        
        [XLog logWithLevel:(XloggerType)type.intValue message:msg];
        [XLog flush];
        
    } else {
        result(FlutterMethodNotImplemented);
    }
}


- (id)checkArgument:(NSDictionary *)arguments forKey:(NSString *)key ofType:(Class)clazz {
    if (key == nil || arguments == nil || clazz == nil) {
        return  nil;
    }
    id arg = [arguments objectForKey:key];
    
    if (arg == nil)
        return  nil;
    
    if (![arg isKindOfClass:clazz])
        return nil;
    
    return arg;
}
@end
