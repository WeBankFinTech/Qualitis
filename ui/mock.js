module.exports = function (cgiMock, Mock) {
    var Random = Mock.Random;

    //前缀，全局（可选）
    //cgiMock.prefix = '';

    // 登录
    cgiMock({
        url: 'api/v1/login/local',
        method: 'POST',
        result: {
            "code": "200",
            "success": false
        }
    });

    //利用mock.js 产生随机文本
    cgiMock('/text', Random.cparagraph());

    // 返回一个字符串 利用mock.js 产生随机字符
    cgiMock('/string', Mock.mock({
        "string|1-10": "★"
    }));


    // 正则匹配url, 返回一个字符串
    // cgiMock(/\/abc|\/xyz/, 'regexp test!');

    // option.result 参数如果是一个函数, 可以实现自定义返回内容, 接收的参数是是经过 express 封装的 req 和 res 对象.
    // cgiMock(/\/function$/, function (req, res) {
    //     res.send('function test');
    // });

    // 返回文本 fs.readFileSync
    // cgiMock('/file', cgiMock.file('./test.json'));

    // 更复杂的规则配置
    cgiMock({
        url: /\/who/,
        method: 'GET',
        result: function (req, res) {
            if (req.query.name === 'kwan') {
                res.json({
                    'kwan': '孤独患者'
                });
            } else {
                res.send('Nooooooooooo');
            }
        },
        headers: {
            'Content-Type': 'text/plain',
            'Content-Length': '123',
            'ETag': '12345'
        },
        cookies: [{
            name: 'myname',
            value: 'kwan',
            maxAge: 900000,
            httpOnly: true
        }],
        //接口随机延迟
        timeout: Mock.mock({
            "number|1000-5000": 1000
        }).number
    });
    //登录
    cgiMock('/login', function (req, res) {
        res.send(JSON.stringify({
            code: "0",
            msg: "",
            result: {
                username: "万纯（harrywan）",
                roleName: "管理员"
            }
        }))
    });

    cgiMock('/getTestList', function (req, res) {
        var list = [];
        for (var i = 0; i < req.body.pageSize; i++) {
            list.push({
                a: i
            })
        }
        res.send(JSON.stringify({
            code: "0",
            msg: "this is message",
            result: {
                list: list,
                page: {
                    pageSize: req.body.pageSize,
                    currentPage: req.body.currentPage,
                    totalPage: 1000
                }
            }

        }))
    });

    cgiMock('/getNumber', function (req, res) {
        res.send(JSON.stringify({
            code: "0",
            msg: "this is message",
            result: 4
        }))
    });

    cgiMock('/getRoleName', function (req, res) {
        res.send(JSON.stringify({
            code: "0",
            msg: "this is message",
            result: 'admin'
        }))
    });
}
