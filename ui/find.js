var fs = require('fs');
var path = require('path');
var reg = /[\u4e00-\u9fa5]+：?/g;
var json = {};
function fileDisplay(filePath) {
    fs.readdir(filePath, function (err, files) {
        if (err) {
            console.warn(err)
        } else {
            files.forEach(function (filename) {
                var filedir = path.join(filePath, filename);
                fs.stat(filedir, function (eror, stats) {
                    if (eror) {
                        console.warn('获取文件stats失败');
                    } else {
                        var isFile = stats.isFile();
                        var isDir = stats.isDirectory();
                        if (isFile) {
                            // console.log(filedir);
                            var content = fs.readFileSync(filedir, 'utf-8');
                            var arrs = content.match(reg) || [];
                            arrs.forEach(item => {
                                json[item] = item;
                            });

                            fs.writeFile('./cn.json', JSON.stringify(json), function(err) {
                                if (err) {
                                    throw err;
                                }
                                console.log('done.');
                            })
                        }
                        if (isDir) {
                            fileDisplay(filedir);
                        }
                    }
                })
            });
        }
    });
}
var filePath = path.resolve('./src/pages/verifyFailData');
fileDisplay(filePath);


// 1.首先修改filePath，指定要处理的目录
// 2.然后执行node find.js, 找出所有需要替换的中文形成json
// 3.然后执行node translate.js 调用API批量翻译，翻译结束结果输出cn.json ，对接口翻译结果检查及修改
// 3.接着修改cn.json文件里面的key值，key值建议按如下格式：`模块文件名.文字内容英文简写`，key中英文部分保持一致
// 4.生成的json文件每个key值修改完成后，修改replace.js filePath 值为1中指定的目录 ,执行node replace.js, 会把所指定目录下的中文全部按第三步的key值替换为$t('key')
// 5.把json文件内容复制到国际配置里

