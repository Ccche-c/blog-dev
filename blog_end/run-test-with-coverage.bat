@echo off
echo ========================================
echo ApiAiController 测试和覆盖率报告生成
echo ========================================
echo.
echo 步骤1: 清理并编译项目...
call mvn clean compile test-compile
echo.
echo 步骤2: 运行测试（包含JaCoCo代理）...
call mvn test -Dtest=ApiAiControllerTest
echo.
echo 步骤3: 生成覆盖率报告...
call mvn jacoco:report
echo.
echo ========================================
echo 完成！
echo ========================================
echo.
echo 覆盖率报告位置: target\site\jacoco\index.html
echo.
echo 按任意键打开报告...
pause >nul
start target\site\jacoco\index.html

