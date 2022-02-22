#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint logger.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'logger'
  s.version          = '0.0.10'
  s.summary          = 'A new Flutter project.'
  s.description      = <<-DESC
A new Flutter project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.platform = :ios, '8.0'

  # 依赖系统的framework
  # s.frameworks = "ImageIO"
  # 依赖非系统的framework
   s.vendored_frameworks = 'Assets/mars.framework'
  # 依赖系统的library
  s.libraries = 'z'
  # 依赖非系统的library
  # s.vendored_libraries = []
  
  # Flutter.framework does not contain a i386 slice.
  # s.pod_target_xcconfig = { 
  #   'ENABLE_BITCODE' => 'NO'
  # }
end
