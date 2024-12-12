var fs = require('fs');
var path = require('path');
var json = JSON.parse(fs.readFileSync('./cn.json', 'utf-8')).cn;

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

                            var values = Object.values(json);
                            var keys = Object.keys(json)
                            values = values.sort((a, b) => b.length - a.length);

                            values.forEach((it) => {
                                var key = keys.find(k => json[k] == it)
                                // title="单表校验"
                                // name="技术规则"
                                // <span class="colorTag">未通过校验</span>
                                var titleReg = new RegExp(` title="${it}"`, 'g')
                                var nameReg = new RegExp(` name="${it}"`, 'g')
                                var tagReg = new RegExp(`>${it}<`, 'g')
                                content = content.replace(titleReg, ` :title="$t('${key}')"`)
                                content = content.replace(nameReg, ` :name="$t('${key}')"`)
                                content = content.replace(tagReg, `>{{ $t('${key}') }}<`)
                                content = content.replace(new RegExp(it, 'g'), `$t('${key}')`)
                            })

                            fs.writeFile(filedir, content, function (err) {
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
var filePath = path.resolve('./src/pages/taskQuery');
fileDisplay(filePath);
