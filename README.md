# AGV Backend JUnit Tests

## 项目简介
本项目是AGV（Automated Guided Vehicle）后端系统的JUnit测试集合。它全面覆盖后端各核心模块的单元测试，包括用户管理、验证码生成、文件上传、JWT验证等功能，旨在通过系统化的测试保障代码质量与系统稳定性。

## ✨ 测试覆盖范围 (Test Coverage)
### 1. 工具类测试 (Utility Classes)
- **ResultCodeTest**：验证系统状态码定义的准确性，包括成功、失败及未授权等核心状态码的匹配性。
- **EmailUtilsTest**：测试邮箱验证码相关功能，涵盖验证码生成、邮件主题与内容的正确性验证。
- **JwtUtilTest**：针对JWT令牌的生成与解析过程进行全面测试，包括有效令牌的正常生成、无效令牌及过期令牌的异常处理逻辑。
- **CaptchaUtilsTest**：验证图形验证码的生成机制，包括验证码对象的完整性、图片Base64编码的正确性及异常处理。
- **ThreadLocalUtilTest**：测试线程局部变量的管理功能，确保变量的设置、获取与清除操作在多线程环境下的准确性。

### 2. 控制器测试 (Controllers)
- **UserControllerTest**：覆盖用户信息获取接口的各类场景，包括用户存在时的正常返回、用户不存在的异常处理及服务层异常的捕获机制。
- **CaptchaControllerTest**：验证验证码获取接口的响应正确性，包括返回数据的完整性与服务层调用的准确性。
- **UploadControllerTest**：测试文件上传接口的多种情况，涵盖正常上传、空文件处理、文件格式异常及服务层错误的反馈机制。
- **AuthControllerTest**：针对登录与注册接口进行全面测试，包括正常登录注册流程、输入参数验证失败的处理及安全机制的有效性。

### 3. 服务实现类测试 (Service Implementations)
- **UserServiceImplTest**：验证用户信息服务的核心逻辑，包括用户数据的正确获取、边界条件处理及异常场景的容错机制。
- **CaptchaServiceImplTest**：测试验证码服务的生成与存储逻辑，包括Redis存储的有效性、过期时间设置及异常恢复机制。
- **UploadServiceImplTest**：覆盖文件上传服务的全流程，包括文件存储路径生成、第三方存储（如Minio）的交互逻辑及异常处理。

### 4. 配置类测试 (Configuration Classes)
- **MinioConfigTest**：验证Minio客户端的配置与创建过程，确保文件存储服务的初始化正确性。
- **RestTemplateConfigTest**：测试HTTP客户端的配置有效性，保障服务间通信的可靠性。
- **MybatisPlusConfigTest**：验证数据访问层的配置逻辑，包括分页插件等核心功能的正确性。

### 5. 实体类和DTO测试 (Entities & DTOs)
- **ResultTest**：测试统一响应对象的构建逻辑，确保响应码、消息与数据的正确封装。
- **DefectTest**：验证缺陷实体类的属性管理，包括getter/setter方法的准确性。
- **LoginDTOTest**：测试登录数据传输对象的属性设置与获取，保障前端参数的正确解析。

### 6. 异常类测试 (Exception Classes)
- **AppExceptionTest**：验证自定义业务异常的构造逻辑，确保异常码与消息的正确传递及异常链的完整性。

## 🚀 运行测试 (Running Tests)
确保本地环境已安装Maven，在项目根目录执行以下命令即可运行所有测试：
```sh
mvn test
```

## 贡献 (Contribution)
若发现测试用例的疏漏或有改进建议，欢迎提交issue或发起pull request，共同完善测试体系。

## 许可证 (License)
本项目采用[MIT许可证](LICENSE)。
